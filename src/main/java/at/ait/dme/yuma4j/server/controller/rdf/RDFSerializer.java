package at.ait.dme.yuma4j.server.controller.rdf;

import java.io.StringWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.server.URIBuilder;
import at.ait.dme.yuma4j.server.controller.rdf.serializer.BodyPropertiesAppender;

/**
 * Serializer for OAC RDF (in different serialization languages).
 * 
 * @author Christian Mader
 */
public class RDFSerializer {
	
	private static final String NS_OAC = "http://www.openannotation.org/ns/";
	private static final String PREFIX_OAC = "oac";
	private static final String ANNOTATION = "Annotation";
	private static final String BODY_FRAGMENT = "#body";
	private static final String BODY = "Body";
	private static final String HASBODY = "hasBody";
	private static final String HASTARGET = "hasTarget";
	
	private String serverBaseURL;

	public RDFSerializer(String serverBaseURL) {
		this.serverBaseURL = serverBaseURL;
	}
	
	public String serialize(Annotation annotation, String serialization) {
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix(PREFIX_OAC, NS_OAC);
		
		Resource body = createBodyResource(annotation, model);
		createAnnotationResource(annotation, model, body);
		
		StringWriter sw = new StringWriter();
		model.write(sw, serialization);
		return sw.toString();
	}
	
	private Resource createBodyResource(Annotation a, Model m) {
		Resource body = 
			m.createResource(URIBuilder.toURI(serverBaseURL, a.getID()) + BODY_FRAGMENT);
		
		body.addProperty(RDF.type, m.createProperty(NS_OAC, BODY));
		new BodyPropertiesAppender(body, m).appendProperties(a);
		
		return body;
	}

	private Resource createAnnotationResource(Annotation a, Model m, Resource body) {
		Resource annotationResource = 
			m.createResource(URIBuilder.toURI(serverBaseURL, a.getID()).toString());
		
		annotationResource.addProperty(RDF.type, m.createProperty(NS_OAC, ANNOTATION));
		annotationResource.addProperty(m.createProperty(NS_OAC, HASTARGET), createTarget(a, m));	
		annotationResource.addProperty(m.createProperty(NS_OAC, HASBODY), body);
		
		// new AnnotationPropertiesAppender(ret).appendProperties(annotation);
		return annotationResource;
	}
	
	private String createTarget(Annotation a, Model m) {
		return a.getObjectURI() + "#" + a.getFragment();
	}
	
	
	/*
	
	private void addBodyNode(Annotation annotation, Model model) {
		initResourceCreation(annotation, model);
		createBodyResource();
	}

		
	

	

	
	private void addReplyTargets(Resource annotationResource) {
		annotationResource.addProperty(
			model.createProperty(NS_OAC, "hasTarget"),
			URIBuilder.toURI(annotation.getParentID()).toString());
		
		if (annotation.getFragment() != null) {
			annotationResource.addProperty(
				model.createProperty(NS_OAC, "hasTarget"),
				createConstrainedTarget());	
		}		
	}
	

	private Resource createConstrainedTarget() {
		Resource target = model.createResource(createConstrainedTargetUri(annotation.getID()));
		
		target.addProperty(RDF.type, model.createProperty(NS_OAC, "ConstrainedTarget"));
		new ConstrainedTargetPropertiesAppender(target, model).appendProperties(annotation);
		
		return target;
	}
	*/

}
