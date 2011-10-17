package at.ait.dme.yuma4j.server.controller;

import junit.framework.Assert;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma4j.bootstrap.EmbeddedAnnotationServer;
import at.ait.dme.yuma4j.bootstrap.testdata.JsonTestData;

public class JSONControllerTest {
	
	private static final String JSON_CONTROLLER_BASE_URL = 
		EmbeddedAnnotationServer.getApplicationURL() + "/api/annotation";

	private static final String ACCEPT_HEADER = "Accept";
	private static final String LOCATION_HEADER = "Location";
	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String ENCODING = "UTF-8";
	
	private Logger log = Logger.getLogger(getClass());
	
	@BeforeClass
	public static void setUp() throws Exception {
		EmbeddedAnnotationServer.start("test.properties");
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		EmbeddedAnnotationServer.stop();
	}
		
	@Test
	public void testCreateUpdateDeleteAnnotation() throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		log.info("Testing JSON API at " + JSON_CONTROLLER_BASE_URL);
					
		// Create
		StringEntity createEntity = new StringEntity(JsonTestData.ANNOTATION, ENCODING);
		createEntity.setContentType(CONTENT_TYPE_JSON);
		HttpPost createMethod = new HttpPost(JSON_CONTROLLER_BASE_URL);		
		createMethod.setEntity(createEntity);
		
		HttpResponse response = httpClient.execute(createMethod);
		Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
		Header location = response.getHeaders(LOCATION_HEADER)[0];						
		String createdAnnotationUrl = location.getValue();
		Assert.assertNotNull(createdAnnotationUrl);
		response.getEntity().consumeContent();
		
		// Read
		HttpGet findByIdMethod = new HttpGet(createdAnnotationUrl);
		findByIdMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		
		response = httpClient.execute(findByIdMethod);
		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		response.getEntity().consumeContent();
		
		// Update
		StringEntity putEntity = new StringEntity(JsonTestData.ANNOTATION_UPDATE, ENCODING);
		putEntity.setContentType(CONTENT_TYPE_JSON);
		HttpPut updateMethod = new HttpPut(createdAnnotationUrl);
		updateMethod.setEntity(putEntity);
		
		response = httpClient.execute(updateMethod);
		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		location = response.getHeaders(LOCATION_HEADER)[0];						
		String updatedAnnotationUrl = location.getValue();
		Assert.assertNotNull(updatedAnnotationUrl);
		response.getEntity().consumeContent();
		
		// Delete
		HttpDelete deleteMethod = new HttpDelete(updatedAnnotationUrl);
		deleteMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		
		response = httpClient.execute(deleteMethod);
		Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
	}
	
	@Test
	public void testReplyFunctionality() throws Exception {
		// TODO this unit test needs MUCH more work
		HttpClient httpClient = new DefaultHttpClient();
		
		// root #1
		StringEntity createEntity = new StringEntity(JsonTestData.ANNOTATION, ENCODING);
		createEntity.setContentType(CONTENT_TYPE_JSON);
		HttpPost createMethod = new HttpPost(JSON_CONTROLLER_BASE_URL);		
		createMethod.setEntity(createEntity);		
		
		HttpResponse response = httpClient.execute(createMethod);
		Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
		Header location = response.getHeaders(LOCATION_HEADER)[0];						
		String root1 = location.getValue();
		root1 = root1.substring(root1.lastIndexOf("/") + 1);
		response.getEntity().consumeContent();
		
		// root #2
		response = httpClient.execute(createMethod);
		response.getEntity().consumeContent();
		
		Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
		location = response.getHeaders(LOCATION_HEADER)[0];						
		String root2 = location.getValue();
		root2 = root2.substring(root2.lastIndexOf("/") + 1);
		
		// reply #1
		StringEntity replyEntity = new  StringEntity(JsonTestData.reply(root1), ENCODING);
		replyEntity.setContentType(CONTENT_TYPE_JSON);
		createMethod = new HttpPost(JSON_CONTROLLER_BASE_URL);		
		createMethod.setEntity(replyEntity);			
		
		response = httpClient.execute(createMethod);	
		Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
		location = response.getHeaders(LOCATION_HEADER)[0];						
		String reply1 = location.getValue();
		Assert.assertNotNull(reply1);
		response.getEntity().consumeContent();	
		
		// Delete
		HttpDelete deleteMethod = new HttpDelete(reply1);
		deleteMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		response = httpClient.execute(deleteMethod);
		Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
		
		deleteMethod = new HttpDelete(JSON_CONTROLLER_BASE_URL + "/" + root1);
		deleteMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		response = httpClient.execute(deleteMethod);
		Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
		
		deleteMethod = new HttpDelete(JSON_CONTROLLER_BASE_URL + "/" + root2);
		deleteMethod.addHeader(ACCEPT_HEADER, CONTENT_TYPE_JSON);
		response = httpClient.execute(deleteMethod);
		Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
	}
	
}
