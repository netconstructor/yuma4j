package at.ait.dme.yuma4j.db.impl.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import at.ait.dme.yuma4j.bootstrap.testdata.JsonTestData;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.exception.DeleteNotAllowedException;
import at.ait.dme.yuma4j.model.Annotation;

public class HibernateAnnotationStoreTest {
	
	private static ObjectMapper jsonMapper = new ObjectMapper();

	private static final String IMAGE_URI = "http://dme.ait.ac.at/object/lissabon.jpg";
	
	private Logger log = Logger.getLogger(getClass());
	
	@Test
	public void testHibernateCRUD() throws Exception {
		HibernateAnnotationStore db = new HibernateAnnotationStore();
		Map<Object, Object> initParams = new HashMap<Object, Object>();
		initParams.put("persistence.unit", "test");
		db.init(initParams);
		db.connect();
		
		// Create
		Annotation before = jsonMapper.readValue(JsonTestData.ANNOTATION, Annotation.class);
		Annotation created = db.createAnnotation(before);
		Assert.assertNotNull(created);
		String id = created.getID();
		log.info("Created annotation with ID " + id);
		Assert.assertEquals(before, created);
		
		// Read
		Annotation read = db.getAnnotation(id);
		Assert.assertNotNull(read);
		Assert.assertEquals(before, read);
		
		// Update
		Annotation after = jsonMapper.readValue(JsonTestData.ANNOTATION_UPDATE, Annotation.class);
		Annotation updated = db.updateAnnotation(id, after);
		Assert.assertNotNull(updated);
		Assert.assertEquals(after.getModified(), updated.getModified());
		Assert.assertEquals(after.getText(), updated.getText());
		Assert.assertEquals(after.getFragment(), updated.getFragment());
		Assert.assertEquals(after.getTags(), updated.getTags());
		Assert.assertEquals(id, updated.getID());
		
		// Create reply
		Annotation reply = jsonMapper.readValue(JsonTestData.reply(id), Annotation.class);
		reply = db.createAnnotation(reply);
		Assert.assertNotNull(reply);
		String replyID = reply.getID();
		Assert.assertNotNull(replyID);
		log.info("Created reply to " + id + ", reply ID " + reply.getID());
		
		// Search
		List<Annotation> annotations = db.findAnnotations("SUSPENSION BRIDGE");
		log.info("Testing keyword search");
		Assert.assertEquals(1, annotations.size());
		Assert.assertEquals(updated, annotations.get(0));
		
		// Count
		long count = db.countAnnotationsForObject(IMAGE_URI);
		log.info("Counting annotations for " + IMAGE_URI);
		Assert.assertEquals(2, count);
		
		// Try delete root annotation
		try {
			db.deleteAnnotation(id);
			Assert.fail("Annotation has reply - delete should fail!");
		} catch (DeleteNotAllowedException e) {
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
