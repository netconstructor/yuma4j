package at.ait.dme.yuma4j.server.controller.json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.InvalidAnnotationException;
import at.ait.dme.yuma4j.model.Annotation;
import at.ait.dme.yuma4j.server.URIBuilder;
import at.ait.dme.yuma4j.server.controller.AbstractController;

@Path("/api/annotation")
public class JsonRestController extends AbstractController {
	
    /**
     * Log message String constants
     */
    private static final String LOG_GET = " Fetching JSON for annotation ";
    private static final String LOG_CREATE = " Creating new annotation: ";
	
	@GET
	@Produces("application/json")
	@Path("/{id}")
	public Response getAnnotation(@PathParam("id") String id) throws AnnotationStoreException, 
		AnnotationNotFoundException, JsonGenerationException, JsonMappingException, IOException {

		log.info(servletRequest.getRemoteAddr() + LOG_GET + id);
		
		return Response.ok(jsonMapper.writeValueAsString(super._getAnnotation(id))).build();
	}
	
	@GET
	@Produces("application/json")
	@Path("/{id:.+\\.json}")
	public Response getAnnotation_forceJSON(@PathParam("id") String id) throws JsonGenerationException,
		JsonMappingException, UnsupportedEncodingException, IOException, AnnotationStoreException, 
		AnnotationNotFoundException {
		
		id = id.substring(0, id.indexOf('.'));
		
		log.info(servletRequest.getRemoteAddr() + LOG_GET + id);
		
		return Response.ok(jsonMapper.writeValueAsString(super._getAnnotation(id))).build();
	}
	
	@POST
	@Consumes("application/json")
	@Path("/")
	public Response createAnnotation(String json) 
		throws JsonParseException, JsonMappingException, IOException, AnnotationStoreException,
		InvalidAnnotationException {
		
		log.info(servletRequest.getRemoteAddr() + LOG_CREATE + json);
		Annotation a = jsonMapper.readValue(json, Annotation.class);
		a = super._createAnnotation(a);
			
		return Response
			.created(URIBuilder.toURI(getConfig().getServerBaseURL(), a.getID()))
			.entity(jsonMapper.writeValueAsString(a))
			.build();
	}

}
