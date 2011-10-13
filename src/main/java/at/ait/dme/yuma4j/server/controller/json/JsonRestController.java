package at.ait.dme.yuma4j.server.controller.json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.server.controller.AbstractController;

@Path("/api/annotation")
public class JsonRestController extends AbstractController {
	
    /**
     * Log message String constants
     */
    private static final String LOG_GET = " Fetching JSON for annotation ";
	
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

}
