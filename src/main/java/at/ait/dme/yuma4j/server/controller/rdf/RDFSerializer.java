package at.ait.dme.yuma4j.server.controller.rdf;

import java.io.StringWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import at.ait.dme.yuma4j.Annotation;

/**
 * Serializer for OAC RDF (in different serialization languages).
 * 
 * @author Christian Mader
 */
public class RDFSerializer {
	
	public static final String NS_OAC = "http://www.openannotation.org/ns/";

	public String serialize(Annotation annotation, String serialization) {
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefix("oac", NS_OAC);
		
		// Resource body = createBodyResource(annotation, model);
		// createAnnotationResource(body);
		
		StringWriter sw = new StringWriter();
		model.write(sw, serialization);
		return sw.toString();
	}
	
	/*
	private Resource createAnnotationResource(Resource body) {
		Resource ret = model.createResource(
			URIBuilder.toURI(annotation.getID()).toString());
		
		addBasicProperties(ret, body);
		new AnnotationPropertiesAppender(ret).appendProperties(annotation);
		return ret;
	}
	
	private Resource createBodyResource(Annotation annotation, Model model) {
		Resource body = model.createResource(createBodyUri(annotation.getID()));
		
		body.addProperty(RDF.type, model.createProperty(NS_OAC, "Body"));
		new BodyPropertiesAppender(body, model).appendProperties(annotation);
		
		return body;
	}
	
	private void addBodyNode(Annotation annotation, Model model) {
		initResourceCreation(annotation, model);
		createBodyResource();
	}

		
	

	
	private void addBasicProperties(Resource annotResource, Resource body)
	{
		if (isReplyAnnotation()) {
			annotResource.addProperty(RDF.type, model.createProperty(NS_OAC, "Reply"));
			addReplyTargets(annotResource);
		}
		else {
			annotResource.addProperty(RDF.type, model.createProperty(NS_OAC, "Annotation"));
			addSingleTarget(annotResource);
		}
		
		annotResource.addProperty(
			model.createProperty(NS_OAC, "hasBody"), 
			body);
	}
	
	private boolean isReplyAnnotation() {
		return annotation.getParentID() != null && !annotation.getParentID().isEmpty();
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
	
	private void addSingleTarget(Resource annotationResource) {
		annotationResource.addProperty(
			model.createProperty(NS_OAC, "hasTarget"),
			createTarget());		
	}
	
	private Resource createTarget() {
		// if (annotation.getFragment() == null) {
			return createNonConstrainedTarget();
		// } else {
		// 	return createConstrainedTarget();
		// }
	}
	
	private Resource createNonConstrainedTarget() {
		Resource target = model.createResource(annotation.getObjectURI().toString());
		target.addProperty(RDF.type, model.createProperty(NS_OAC, "Target"));
		return target;
	}
	

	private Resource createConstrainedTarget() {
		Resource target = model.createResource(createConstrainedTargetUri(annotation.getID()));
		
		target.addProperty(RDF.type, model.createProperty(NS_OAC, "ConstrainedTarget"));
		new ConstrainedTargetPropertiesAppender(target, model).appendProperties(annotation);
		
		return target;
	}
	*/

}
