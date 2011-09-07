package at.ait.dme.yuma4j.db.impl.hibernate;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.bootstrap.testdata.JsonTestData;
import at.ait.dme.yuma4j.db.exception.AnnotationHasReplyException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;

public class HibernateAnnotationStoreTest {
	
	private static ObjectMapper jsonMapper = new ObjectMapper();

	private static final String IMAGE_URI = "http://dme.ait.ac.at/object/lissabon.jpg";
	
	private Logger log = Logger.getLogger(getClass());
	
	@Test
	public void testHibernateCRUD() throws Exception {
		HibernateAnnotationStore db = new HibernateAnnotationStore();
		db.init("test");
		db.connect();
		
		// Create
		Annotation before = jsonMapper.readValue(JsonTestData.ANNOTATION, Annotation.class);
		String id = db.createAnnotation(before);
		log.info("Created annotation with ID " + id);
		Assert.assertNotNull(id);
		Assert.assertEquals(id, before.getAnnotationID());
		
		// Read
		Annotation annotation = db.getAnnotation(id);
		Assert.assertNotNull(annotation);
		Assert.assertEquals(before, annotation);
		
		// Update
		Annotation after = jsonMapper.readValue(JsonTestData.ANNOTATION_UPDATE, Annotation.class);
		id = db.updateAnnotation(id, after);
		log.info("Annotation updated, new ID " + id);
		Assert.assertNotNull(id);
		Assert.assertFalse(before.equals(after));
		Assert.assertEquals(id, after.getAnnotationID());
		
		// Create reply
		Annotation reply = jsonMapper.readValue(JsonTestData.reply(id, id), Annotation.class);
		String replyID = db.createAnnotation(reply);
		log.info("Created reply to " + id + ", reply ID " + replyID);
		Assert.assertNotNull(replyID);
		Assert.assertEquals(replyID, reply.getAnnotationID());
		
		// Search
		List<Annotation> annotations = db.findAnnotations("SUSPENSION BRIDGE");
		log.info("Testing keyword search");
		Assert.assertEquals(1, annotations.size());
		Assert.assertEquals(after, annotations.get(0));
		
		// Count
		long count = db.countAnnotationsForObject(IMAGE_URI);
		log.info("Counting annotations for " + IMAGE_URI);
		Assert.assertEquals(2, count);
		
		// Try delete root annotation
		try {
			db.deleteAnnotation(id);
			Assert.fail("Annotation has reply - delete should fail!");
		} catch (AnnotationHasReplyException e) {
			log.info("Deleting annotation " + id + " failed as expected");
		}
		
		// Delete
		db.deleteAnnotation(replyID);
		try {
			db.getAnnotation(replyID);
			Assert.fail("Annotation " + replyID + " should be deleted, but is still in DB!");
		} catch (AnnotationNotFoundException e) {
			log.info("Annotation " + replyID + " deleted successfully");
		}
		
		db.deleteAnnotation(id);
		try {
			db.getAnnotation(id);
			Assert.fail("Annotation " + id + " should be deleted, but is still in DB!");
		} catch (AnnotationNotFoundException e) {
			log.info("Annotation " + id + " deleted successfully");
		}
		Assert.assertEquals(0, db.countAnnotationsForObject(IMAGE_URI));
		log.info("No more annotations for " + IMAGE_URI + " left in DB");
		db.shutdown();
	}
	
}
