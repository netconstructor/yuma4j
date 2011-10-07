package at.ait.dme.yuma4j.server.controller;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;
import at.ait.dme.yuma4j.server.URIBuilder;

@Path("/api/annotation")
public class JsonRestController extends AbstractJsonController {

	@POST
	@Consumes("application/json")
	@Path("/")
	public Response createAnnotationREST(String json) throws AnnotationStoreException,
		JsonParseException, JsonMappingException, AnnotationModifiedException, IOException {
		
		Annotation a = super.createAnnotation(json);
		return Response.created(URIBuilder.toURI(getConfig().getServerBaseURL(), a.getAnnotationID()))
				.entity(a).build();
	}

}
