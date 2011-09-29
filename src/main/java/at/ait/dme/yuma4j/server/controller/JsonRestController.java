package at.ait.dme.yuma4j.server.controller;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;

@Path("/api")
public class JsonRestController extends AbstractJsonController {

	@POST
	@Consumes("application/json")
	@Path("/annotation")
	public Response createAnnotationREST(String annotation) throws AnnotationStoreException,
		JsonParseException, JsonMappingException, AnnotationModifiedException, IOException {
		
		return super.createAnnotation(annotation);
	}

}
