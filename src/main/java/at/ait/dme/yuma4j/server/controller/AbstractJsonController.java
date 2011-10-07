package at.ait.dme.yuma4j.server.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.User;
import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationHasReplyException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.server.URIBuilder;
import at.ait.dme.yuma4j.server.config.ServerConfig;

public class AbstractJsonController {
	
	private static final String URL_ENCODING = "UTF-8";
	
	private ObjectMapper jsonMapper = new ObjectMapper();
	
	@Context
	private ServletContext servletContext;
	
    @Context
    protected HttpServletRequest servletRequest;
    
    protected Logger log = Logger.getLogger(getClass());
    
	protected String listAnnotations(String objectURI) throws AnnotationStoreException {
		AnnotationStore db = null;
		String tree = null;
		
		try {
			db = ServerConfig.getInstance(servletContext.getInitParameter(ServerConfig.INIT_PARAM_PROPERTIES)).getAnnotationStore();
			db.connect();
			tree = jsonMapper.writeValueAsString(db.listAnnotationsForObject(URLDecoder.decode(objectURI, URL_ENCODING)).asFlatList());
		} catch (IOException e) {
			// Should never happen
			throw new RuntimeException(e);		
		} finally { 
			if(db != null) db.disconnect();
		}

		return tree;
	}

	protected Response createAnnotation(String annotation) throws AnnotationStoreException,
		JsonParseException, JsonMappingException, AnnotationModifiedException, IOException {
		
		AnnotationStore db = null;
		String annotationId = null;
		
		ServerConfig config = 
			ServerConfig.getInstance(servletContext.getInitParameter(ServerConfig.INIT_PARAM_PROPERTIES));
		
		try {
			db = config.getAnnotationStore();
			db.connect();
			
			// Parse annotation
			Annotation a = jsonMapper.readValue(annotation, Annotation.class);
			
			// It's a new annotation, created now - set timestamps to current time
			a.setCreated(new Date());
			a.setModified(new Date());
			
			// TODO get user from session! This is just a dummy for now
			a.setCreator(new User("guest"));
			
			annotationId = db.createAnnotation(a);
		} finally {
			if (db != null) db.disconnect();
		}
		return Response.created(URIBuilder.toURI(config.getServerBaseURL(), annotationId)).entity(annotationId).build();
	}
	
	protected Response getAnnotation(@PathParam("id") String annotationId)
		throws AnnotationStoreException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		AnnotationStore db = null;
		String annotation = null;
				
		try {
			db = ServerConfig.getInstance(servletContext.getInitParameter(ServerConfig.INIT_PARAM_PROPERTIES)).getAnnotationStore();
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
	
	protected Response updateAnnotation(@PathParam("id") String annotationId, String annotation)
			throws AnnotationStoreException, AnnotationHasReplyException, AnnotationNotFoundException {
		
		AnnotationStore db = null;
		
		ServerConfig config = 
			ServerConfig.getInstance(servletContext.getInitParameter(ServerConfig.INIT_PARAM_PROPERTIES));
		
		try {
			db = config.getAnnotationStore();
			db.connect();
			
			Annotation a = jsonMapper.readValue(annotation, Annotation.class);
			
			// Set modfied timestamp to now
			a.setModified(new Date());
			
			annotationId = db.updateAnnotation(URLDecoder.decode(annotationId, URL_ENCODING), a);
		} catch (IOException e) {
			// Should never happen (except in case of DB inconsistency)
			throw new RuntimeException(e);
		} finally {
			if(db != null) db.disconnect();
		}	
		return Response.ok().entity(annotationId.toString()).header("Location", URIBuilder.toURI(config.getServerBaseURL(), annotationId)).build(); 
	}
	
	protected Response deleteAnnotation(@PathParam("id") String annotationId)
		throws AnnotationStoreException, AnnotationHasReplyException, UnsupportedEncodingException, AnnotationNotFoundException {
		
		AnnotationStore db = null;
		try {			
			db = ServerConfig.getInstance(servletContext.getInitParameter(ServerConfig.INIT_PARAM_PROPERTIES)).getAnnotationStore();
			db.connect();
			db.deleteAnnotation(URLDecoder.decode(annotationId, URL_ENCODING));
		} finally {
			if(db != null) db.disconnect();
		}	
		
		// response to DELETE without a body should return 204 NO CONTENT see 
		// http://www.w3.org/Protocols/rfc2616/rfc2616.html
		return Response.noContent().build(); 
	}

}
