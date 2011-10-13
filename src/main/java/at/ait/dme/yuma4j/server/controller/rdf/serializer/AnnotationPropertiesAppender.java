package at.ait.dme.yuma4j.server.controller.rdf.serializer;

import at.ait.dme.yuma4j.Annotation;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;

/**
 * Used to add RDF properties to an annotation resource.
 * 
 * @author Christian Mader
 */
public class AnnotationPropertiesAppender extends PropertiesAppender {
	
	public AnnotationPropertiesAppender(Resource resource) {
		super(resource);
	}
	
	@Override
	void populatePropertiesMap(Annotation annotation) {
		addProperty(DC.creator, annotation.getCreator().getUsername());
		addProperty(DCTerms.created, annotation.getCreated().toString());
		addProperty(DCTerms.modified,  annotation.getModified().toString());
		
		//TODO: What about annotation.getScope()?
	}
	
}
