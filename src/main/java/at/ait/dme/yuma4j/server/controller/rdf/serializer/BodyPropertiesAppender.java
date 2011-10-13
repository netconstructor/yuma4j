package at.ait.dme.yuma4j.server.controller.rdf.serializer;

import java.util.List;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.SemanticTag;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Used to add properties to the body of an annotation.
 * 
 * @author Christian Mader
 */
public class BodyPropertiesAppender extends PropertiesAppender {

	private Model model;
	
	public BodyPropertiesAppender(Resource resource, Model model) {
		super(resource);
		this.model = model;
	}
	
	@Override
	void populatePropertiesMap(Annotation annotation) {
		addProperty(RDFS.label, annotation.getText());
		appendSemanticTags(annotation);
	}

	private void appendSemanticTags(Annotation annotation) {
		List<SemanticTag> semanticTags = annotation.getTags();
		
		if (semanticTags == null)
			return;
			
		for (SemanticTag semanticTag : semanticTags) {
			addCommonTag(semanticTag);
		}
	}
	
	private void addCommonTag(SemanticTag semanticTag) {
		addProperty(
			CTAG.tagged,
			createTagResource(semanticTag));
	}
	
	private Resource createTagResource(SemanticTag semanticTag) {
		Resource ret = model.createResource(CTAG.Tag);
		ret.addProperty(CTAG.means, semanticTag.getURI().toString());
		
		// addPrimaryLabel(ret, semanticTag);
		
		return ret;
	}
	
	/*
	private void addPrimaryLabel(Resource resource, SemanticTag semanticTag) {
		String primaryLabel = semanticTag.getPrimaryLabel();
		
		if (primaryLabel != null && !primaryLabel.isEmpty()) {
			resource.addProperty(CTAG.label, primaryLabel);			
		}
	}
	*/
	
}
