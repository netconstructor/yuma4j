package at.ait.dme.yuma4j.server.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

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
import at.ait.dme.yuma4j.server.config.ServerConfig;

public class AbstractController {
	
	private static final String URL_ENCODING = "UTF-8";
	
	protected ObjectMapper jsonMapper = new ObjectMapper();
	
	@Context
	private ServletContext servletContext;
	
    @Context
    protected HttpServletRequest servletRequest;
       
    protected Logger log = Logger.getLogger(getClass());
    
    protected ServerConfig getConfig() {
    	return ServerConfig.getInstance(servletContext.getInitParameter(ServerConfig.INIT_PARAM_PROPERTIES));
    }
    
	protected List<Annotation> _listAnnotations(String objectURI) throws AnnotationStoreException {
		AnnotationStore db = null;
		List<Annotation> tree = null;
		
		try {
			db = getConfig().getAnnotationStore();
			db.connect();
			tree = db.listAnnotationsForObject(URLDecoder.decode(objectURI, URL_ENCODING)).asFlatList();
		} catch (IOException e) {
			// Should never happen
			throw new RuntimeException(e);		
		} finally { 
			if(db != null) db.disconnect();
		}

		return tree;
	}
	
	protected Annotation _getAnnotation(@PathParam("id") String annotationId)
		throws AnnotationStoreException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		AnnotationStore db = null;
		Annotation annotation = null;
				
		try {
			db = getConfig().getAnnotationStore();
			db.connect();
			annotation = db.getAnnotation(URLDecoder.decode(annotationId, URL_ENCODING));
		} catch (IOException e) {
			// Should never happen (except in case of DB inconsistency)
			throw new RuntimeException(e);
		} finally {
			if(db != null) db.disconnect();
		}
		
		return annotation;
	}

	protected Annotation _createAnnotation(Annotation annotation) throws AnnotationStoreException,
		JsonParseException, JsonMappingException, AnnotationModifiedException, IOException {
		
		AnnotationStore db = null;
				
		try {
			db = getConfig().getAnnotationStore();
			db.connect();
			
			// It's a new annotation, created now - set timestamps to current time
			annotation.setCreated(new Date());
			annotation.setModified(new Date());
			
			// TODO get user from session! This is just a dummy for now
			annotation.setCreator(new User("guest"));
			
			db.createAnnotation(annotation);
		} finally {
			if (db != null) db.disconnect();
		}
		
		return annotation;
	}
	
	protected Annotation _updateAnnotation(Annotation annotation) throws AnnotationStoreException,
		AnnotationHasReplyException, AnnotationNotFoundException, UnsupportedEncodingException {
		
		AnnotationStore db = null;
		try {
			db = getConfig().getAnnotationStore();
			db.connect();
			Annotation old = db.getAnnotation(annotation.getID());
			
			// TODO verify whether the logged in user is really the creator 
			
			// Make sure no-one messed with the server-generated values
			annotation.setCreated(old.getCreated());

			// Set modfied timestamp to now
			annotation.setModified(new Date());
			
			// Update
			db.updateAnnotation(annotation.getID(), annotation);
		} finally {
			if(db != null) db.disconnect();
		}	
		
		return annotation;
	}
	
	protected void _deleteAnnotation(@PathParam("id") String annotationId)
		throws AnnotationStoreException, AnnotationHasReplyException, UnsupportedEncodingException, AnnotationNotFoundException {
		
		AnnotationStore db = null;
		try {			
			db = getConfig().getAnnotationStore();
			db.connect();
			db.deleteAnnotation(URLDecoder.decode(annotationId, URL_ENCODING));
		} finally {
			if(db != null) db.disconnect();
		}			
	}

}
