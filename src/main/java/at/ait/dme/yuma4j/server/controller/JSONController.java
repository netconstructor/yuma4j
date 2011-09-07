package at.ait.dme.yuma4j.server.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationHasReplyException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.server.URIBuilder;
import at.ait.dme.yuma4j.server.config.ServerConfig;

@Path("/api")
public class JSONController {
	
	private static final String URL_ENCODING = "UTF-8";
	
	private ObjectMapper jsonMapper = new ObjectMapper();

	@POST
	@Consumes("application/json")
	@Path("/annotation")
	protected Response createAnnotation(String annotation) throws AnnotationStoreException, 
		JsonParseException, JsonMappingException, AnnotationModifiedException, IOException {
		
		AnnotationStore db = null;
		String annotationId = null;
		
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			annotationId = db.createAnnotation(jsonMapper.readValue(annotation, Annotation.class));
		} finally {
			if (db != null) db.disconnect();
		}
		return Response.created(URIBuilder.toURI(annotationId)).entity(annotationId).build();
	}
	
	/**
	 * Find an annotation by its ID
	 * @param annotationId the annotation ID
	 * @return status code 200 and found annotation
	 * @throws AnnotationStoreException (500)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response getAnnotation(String annotationId)
		throws AnnotationStoreException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		AnnotationStore db = null;
		String annotation = null;
		
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
				annotation = jsonMapper
					.writeValueAsString(db.getAnnotation(URLDecoder.decode(annotationId, URL_ENCODING)));
		} catch (IOException e) {
			// Should never happen (except in case of DB inconsistency)
			throw new RuntimeException(e);
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok(annotation).build();
	}
	
	/**
	 * Update an existing annotation
	 * @param annotationId the annotation ID 
	 * @param annotation the JSON representation of the annotation
	 * @return status code 200 and updated annotation representation
	 * @throws AnnotationStoreException (500)
	 * @throws InvalidAnnotationException (415)
	 * @throws AnnotationHasReplyException (409)
	 * @throws AnnotationNotFoundException
	 */
	protected Response updateAnnotation(String annotationId, String annotation)
			throws AnnotationStoreException, AnnotationHasReplyException, AnnotationNotFoundException {
		
		AnnotationStore db = null;
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			annotationId = db.updateAnnotation(
					URLDecoder.decode(annotationId, URL_ENCODING),
					jsonMapper.readValue(annotation, Annotation.class));
			
			annotation = jsonMapper.writeValueAsString(db.getAnnotation(annotationId));
		} catch (IOException e) {
			// Should never happen (except in case of DB inconsistency)
			throw new RuntimeException(e);
		} finally {
			if(db != null) db.disconnect();
		}	
		return Response.ok().entity(annotationId.toString()).header("Location", URIBuilder.toURI(annotationId)).build(); 
	}
	
	/**
	 * Delete an annotation
	 * @param annotationId the annotation ID
	 * @return status code 204
	 * @throws AnnotationStoreException (500)
	 * @throws UnsupportedEncodingException (500)
	 * @throws AnnotationHasReplyException (409)
	 * @throws AnnotationNotFoundException (404)
	 */
	protected Response deleteAnnotation(String annotationId)
		throws AnnotationStoreException, AnnotationHasReplyException, UnsupportedEncodingException, AnnotationNotFoundException {
		
		AnnotationStore db = null;
		try {			
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			db.deleteAnnotation(URLDecoder.decode(annotationId, URL_ENCODING));
		} finally {
			if(db != null) db.disconnect();
		}	
		
		// response to DELETE without a body should return 204 NO CONTENT see 
		// http://www.w3.org/Protocols/rfc2616/rfc2616.html
		return Response.noContent().build(); 
	}
	
	/**
	 * Retrieve the thread which contains the given annotation
	 * @param annotationId the annotation ID
	 * @return status code 200 and representation of the annotation thread
	 * @throws AnnotationStoreException (500)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response getReplies(String annotationId)
		throws AnnotationStoreException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		AnnotationStore db = null;
		String thread = null;
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			thread = jsonMapper.writeValueAsString(db.listRepliesToAnnotation(URLDecoder.decode(annotationId, URL_ENCODING)));
		} catch (IOException e) {
			// Should never happen
			throw new RuntimeException(e);		
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok().entity(thread).build();
	}
	
	/**
	 * Returns the entire tree of annotations for a given object
	 * @param objectId the object ID
	 * @return status code 200 and the representation of the found annotations
	 * @throws AnnotationStoreException (500)
	 * @throws InvalidAnnotationException (415)
	 * @throws UnsupportedEncodingException (500)
	 */
	protected Response getAnnotationTree(String objectId) throws AnnotationStoreException {
		AnnotationStore db = null;
		String tree = null;
		
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			tree = jsonMapper.writeValueAsString(db.listAnnotationsForObject(URLDecoder.decode(objectId, URL_ENCODING)));
		} catch (IOException e) {
			// Should never happen
			throw new RuntimeException(e);		
		} finally { 
			if(db != null) db.disconnect();
		}

		return Response.ok().entity(tree).build();
	}
	
	/**
	 * Retrieves the number of annotations for the given object
	 * @param objectId the object ID
	 * @return status code and count representation
	 * @throws AnnotationStoreException (500)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response countAnnotationsForObject(String objectID) throws AnnotationStoreException {
		AnnotationStore db = null;
		long count = 0;
		
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			count = db.countAnnotationsForObject(URLDecoder.decode(objectID, URL_ENCODING));
		} catch (UnsupportedEncodingException e) {
			// Should never happen
			throw new RuntimeException(e);		
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok().entity(count).build();
	}
	
	protected Response getAnnotationsForUser(String username) throws AnnotationStoreException {
		AnnotationStore db = null;
		String annotations = null;
		
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			annotations = jsonMapper.writeValueAsString(db.listAnnotationsForUser(URLDecoder.decode(username, URL_ENCODING)));
		} catch (IOException e) {
			// Should never happen (except in case of DB inconsistency)
			throw new RuntimeException(e);
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok().entity(annotations).build();	
	}
	
	protected Response getMostRecent(int n) throws AnnotationStoreException {
		AnnotationStore db = null;
		String mostRecent = null;
		
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			mostRecent = jsonMapper.writeValueAsString(db.getMostRecent(n, true));
		} catch (IOException e) {
			// Should never happen (except in case of DB inconsistency)
			throw new RuntimeException(e);
		} finally {
			if(db != null) db.disconnect();
		}
		
		return Response.ok().entity(mostRecent).build();
	}
				
	/**
	 * Find annotations that match the given search term
	 * @param query the query term
	 * @return status code 200 and found annotations
	 * @throws AnnotationStoreException (500)
	 * @throws UnsupportedEncodingException (500
	 */
	protected Response searchAnnotations(String query) throws AnnotationStoreException {
		AnnotationStore db = null;
		String annotations = null;		
		
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			annotations = jsonMapper.writeValueAsString(db.findAnnotations(URLDecoder.decode(query, URL_ENCODING)));
		} catch (IOException e) {
			// Should never happen (except in case of DB inconsistency)
			throw new RuntimeException(e);
		} finally {
			if(db != null) db.disconnect();
		}
		return Response.ok(annotations).build();
	}
}
