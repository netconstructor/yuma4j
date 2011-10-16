package at.ait.dme.yuma4j.db.impl.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import at.ait.dme.yuma4j.bootstrap.testdata.JsonTestData;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.model.Annotation;

/**
* Test for concurrent access to the annotation database.
*  
* @author Christian Sadilek <christian.sadilek@gmail.com>
*/
public class ConcurrentTest {
	
	private static ObjectMapper jsonMapper = new ObjectMapper();
	
	private static final int THREADS = 25;
	
	@Test
	public void testConcurrentCrud() throws Exception {	
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(THREADS);			
		
		for (int i = 0; i < THREADS; i++) {
			final int index = i;
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						startGate.await();						
						HibernateAnnotationStore db = new HibernateAnnotationStore();
						Map<Object, Object> initParams = new HashMap<Object, Object>();
						initParams.put("persistence.unit", "test");
						db.init(initParams);		
						try {
							long start = System.currentTimeMillis();
							db.connect();						
							String id = db.createAnnotation(jsonMapper.readValue(JsonTestData.ANNOTATION, Annotation.class));
							db.disconnect();
							System.out.println("CREATE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms" + " ID:"+id);														

							start = System.currentTimeMillis();
							db.connect();																											
							id = db.updateAnnotation(id, jsonMapper.readValue(JsonTestData.ANNOTATION_UPDATE, Annotation.class));
							db.disconnect();
							System.out.println("UPDATE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms");
					
							start = System.currentTimeMillis();																																			
							db.connect();
							db.deleteAnnotation(id);							
							db.disconnect();
							System.out.println("DELETE TIME for:"+index +"="+(System.currentTimeMillis()- start)+" ms");
						} finally {
							endGate.countDown();
							db.disconnect();
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			};
			thread.start();
		}
		long start = System.currentTimeMillis();
		startGate.countDown();
		endGate.await();
		System.out.println("TOTAL TIME:"+(System.currentTimeMillis()- start)+" ms");
	}
	
	@Test
	public void testReadCommited() throws Exception {		
		Map<Object, Object> initParams = new HashMap<Object, Object>();
		initParams.put("persistence.unit", "test");
		
		HibernateAnnotationStore db1 = new HibernateAnnotationStore();
		db1.init(initParams);
		HibernateAnnotationStore db2 = new HibernateAnnotationStore();
		db2.init(initParams);
		
		String id = null;
		try {
			db1.connect();
			id = db1.createAnnotation(jsonMapper.readValue(JsonTestData.ANNOTATION, Annotation.class));
			
			db2.connect();
			
			try {
				Annotation foundAnnotation = db2.getAnnotation(id.toString());

				assertEquals(id, foundAnnotation.getID());
			} catch(AnnotationNotFoundException e) {
				fail("created annotation not found");
			}
			db2.getAnnotation(id.toString());			
		} finally {
			if(id != null) {
				db1.deleteAnnotation(id.toString());
			}
			db1.disconnect();
			db2.disconnect();			
		}
	}
	
}
