package at.ait.dme.yuma4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * A 'Semantic Tag' which is part of an annotation. Semantic Tags
 * represent links to Linked Data resources, denoted by a URI, at least
 * one text label, and (optional) text descriptions.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class SemanticTag extends AbstractModelPOJO {

	/**
	 * The URI (MANDATORY)
	 */
	private URI uri;

	/**
	 * The label(s) (it is MANDATORY to have at least one label) 
	 */
	private List<PlainLiteral> labels = new ArrayList<PlainLiteral>();
	
	/**
	 * The description(s) (OPTIONAL)
	 */
	private List<PlainLiteral> descriptions = new ArrayList<PlainLiteral>();
	
	// Required for JSON serialization
	SemanticTag() { }
	
	public SemanticTag(URI uri, PlainLiteral label) {
		this.uri = uri;
		labels.add(label);
	}
		
	public URI getURI() {
		return uri;
	}
	
	// Required for JSON mapping
	void setURI(URI uri) {
		this.uri = uri;
	}
	
	public void addLabel(PlainLiteral label) {
		labels.add(label);
	}

	public List<PlainLiteral> getLabels() {
		return labels;
	}
	
	public void addDescription(PlainLiteral description) {
		descriptions.add(description);
	}

	public List<PlainLiteral> getDescriptions() {
		return descriptions;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SemanticTag))
			return false;
		
		// We consider tags equal if they point to the same resource
		return uri.equals(((SemanticTag) other).getURI());
	}
	
	@Override
	public int hashCode() {
		return uri.hashCode();
	}
	
}
