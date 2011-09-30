package at.ait.dme.yuma4j.server.controller;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;

@Path("/api")
public class JsonPController extends AbstractJsonController {
	
    /**
     * Log message String constants
     */
    private static final String LOG_CREATE = " Creating new annotation: ";
	
	@GET
	@Path("/annotation/jsonp/create")
	@Override
	public Response createAnnotation(@QueryParam("json") String json) throws AnnotationStoreException,
		JsonParseException, JsonMappingException, AnnotationModifiedException, IOException {

		log.info(servletRequest.getRemoteAddr() + LOG_CREATE + json);
		return super.createAnnotation(json);
	}
	
}
