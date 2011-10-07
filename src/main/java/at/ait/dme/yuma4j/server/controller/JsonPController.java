package at.ait.dme.yuma4j.server.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.db.exception.AnnotationHasReplyException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;
import at.ait.dme.yuma4j.server.URIBuilder;

@Path("/api/annotation/jsonp")
public class JsonPController extends AbstractJsonController {
	
    /**
     * Log message String constants
     */
    private static final String LOG_LIST = " Listing annotations for object ";
    private static final String LOG_CREATE = " Creating new annotation: ";
	
    @GET
    @Path("/list")
	public Response listAnnotations(@QueryParam("objectURI") String objectURI, 
			@QueryParam("callback") String callback) throws AnnotationStoreException {

		log.info(servletRequest.getRemoteAddr() + LOG_LIST + objectURI);
		
		String jsonp = callback + "(" + super.listAnnotations(objectURI) + ");";
		return Response.ok().entity(jsonp).build();
	}
    
	@GET
	@Path("/create")
	public Response createAnnotation(@QueryParam("json") String json, @QueryParam("callback") String callback)
			throws AnnotationStoreException, JsonParseException, JsonMappingException, AnnotationModifiedException,
			IOException {

		log.info(servletRequest.getRemoteAddr() + LOG_CREATE + json);
		
		Annotation a = super.createAnnotation(json);
		String response = callback + "(" + jsonMapper.writeValueAsString(a) + ");";
		return Response.created(URIBuilder.toURI(config.getServerBaseURL(), a.getAnnotationID()))
			.entity(response).build();
	}
	
	@GET
	@Path("/delete")
	public Response deleteAnnotation(@PathParam("id") String annotationId, @QueryParam("callback") String callback)
			throws UnsupportedEncodingException, AnnotationStoreException, AnnotationHasReplyException, AnnotationNotFoundException {
		
		super.deleteAnnotation(annotationId);
		
		// response to DELETE without a body should return 204 NO CONTENT see 
		// http://www.w3.org/Protocols/rfc2616/rfc2616.html
		return Response.noContent().build(); 
	}

}
