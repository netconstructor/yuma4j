package at.ait.dme.yuma4j.server.controller.rdf;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.server.controller.AbstractController;

@Path("/api/annotation")
public class RDFController extends AbstractController {

	private static final String RDFXML = "RDF/XML";
	private static final String TURTLE = "TURTLE";
	private static final String N3 = "N3";
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/{id}")
	public Response getAnnotationRDFXML(@PathParam("id") String id) throws
		UnsupportedEncodingException, AnnotationStoreException, AnnotationNotFoundException {
		
		return Response.ok(serializeAnnotation(id, RDFXML)).build();
	}
	
	@GET
	@Produces("application/rdf+xml")
	@Path("/{id:.+\\.rdf}")
	public Response getAnnotation_forceRDFXML(@PathParam("id") String id) throws
		UnsupportedEncodingException, AnnotationStoreException, AnnotationNotFoundException {

		return Response.ok(serializeAnnotation(id.substring(0, id.indexOf('.')), RDFXML)).build();
	}
	
	@GET
	@Produces("application/x-turtle")
	@Path("/{id}")
	public Response getAnnotationTurtle(@PathParam("id") String id) throws
		UnsupportedEncodingException, AnnotationStoreException, AnnotationNotFoundException {
		
		return Response.ok(serializeAnnotation(id, TURTLE)).build();
	}
	
	@GET
	@Produces("application/x-turtle")
	@Path("/{id:.+\\.turtle}")
	public Response getAnnotation_forceTurtle(@PathParam("id") String id) throws
		UnsupportedEncodingException, AnnotationStoreException, AnnotationNotFoundException {

		return Response.ok(serializeAnnotation(id.substring(0, id.indexOf('.')), TURTLE)).build();
	}
	
	@GET
	@Produces("text/rdf+n3")
	@Path("/{id}")
	public Response getAnnotationN3(@PathParam("id") String id) throws
		UnsupportedEncodingException, AnnotationStoreException, AnnotationNotFoundException {
		
		return Response.ok(serializeAnnotation(id, N3)).build();
	}
	
	@GET
	@Produces("text/rdf+n3")
	@Path("/{id:.+\\.n3}")
	public Response getAnnotation_forceN3(@PathParam("id") String id) throws
		UnsupportedEncodingException, AnnotationStoreException, AnnotationNotFoundException {
		
		return Response.ok(serializeAnnotation(id.substring(0, id.indexOf('.')), N3)).build();
	}
	
	private String serializeAnnotation(String id, String serialization) throws
		UnsupportedEncodingException, AnnotationStoreException, AnnotationNotFoundException {
		
		Annotation a = super._getAnnotation(id);
		return new RDFSerializer().serialize(a, serialization);
	}
	
}
