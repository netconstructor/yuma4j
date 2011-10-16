package at.ait.dme.yuma4j.server.controller.json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import at.ait.dme.yuma4j.db.exception.AnnotationHasReplyException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;
import at.ait.dme.yuma4j.model.Annotation;
import at.ait.dme.yuma4j.server.controller.AbstractController;

@Path("/api/annotation/jsonp")
public class JsonPController extends AbstractController {
	
    /**
     * Log message String constants
     */
    private static final String LOG_LIST = " Listing annotations for object ";
    private static final String LOG_CREATE = " Creating new annotation: ";
    private static final String LOG_UPDATE = " Updating annotation: ";
    private static final String LOG_DELETE = " Deleting annotation ";
	
    @GET
    @Path("/list")
	public Response listAnnotations(@QueryParam("objectURI") String objectURI, 
			@QueryParam("callback") String callback) throws AnnotationStoreException, JsonGenerationException, 
			JsonMappingException, IOException {

		log.info(servletRequest.getRemoteAddr() + LOG_LIST + objectURI);
				
		String jsonp = callback + "(" + jsonMapper.writeValueAsString(super._listAnnotations(objectURI)) + ");";
		return Response.ok().entity(jsonp).build();
	}
    
	@GET
	@Path("/create")
	public Response createAnnotation(@QueryParam("json") String json, @QueryParam("callback") String callback)
			throws AnnotationStoreException, JsonParseException, JsonMappingException, AnnotationModifiedException,
			IOException, AnnotationHasReplyException, AnnotationNotFoundException {

		String jsonp;
		Annotation a = jsonMapper.readValue(json, Annotation.class);
		if (a.getID() == null) {
			log.info(servletRequest.getRemoteAddr() + LOG_CREATE + json);
			a = super._createAnnotation(a);
			jsonp = callback + "(" + jsonMapper.writeValueAsString(a) + ");";
		} else {
			log.info(servletRequest.getRemoteAddr() + LOG_UPDATE + json);
			a = super._updateAnnotation(a);
			jsonp = callback + "(" + jsonMapper.writeValueAsString(a) + ");";
		}
			
		return Response.ok().entity(jsonp).build();
	}
	
	@GET
	@Path("/delete")
	public Response deleteAnnotation(@QueryParam("id") String id, @QueryParam("callback") String callback)
			throws UnsupportedEncodingException, AnnotationStoreException, AnnotationHasReplyException, AnnotationNotFoundException {
		
		log.info(servletRequest.getRemoteAddr() + LOG_DELETE + id);
		
		super._deleteAnnotation(id);
		
		return Response.ok().entity(callback + "();").build(); 
	}

}
