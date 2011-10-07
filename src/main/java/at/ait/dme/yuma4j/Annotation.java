package at.ait.dme.yuma4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The annotation.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class Annotation extends AbstractModelPOJO {

	/**
	 * The ID of this annotation (NOTE: the annotation ID is 
	 * usually auto-assigned by the database!)
	 */
	private String annotationID = null;
	
	/**
	 * The URI of the object which this annotation 
	 * annotates (MANDATORY)
	 */
	private String objectURI;
	
	/**
	 * The creator of this annotation (MANDATORY)
	 */
	private User creator;
	
	/**
	 * Date and time of creation (MANDATORY)
	 */
	private Date created;
	
	/**
	 * Date and time of last modification (MANDATORY)
	 */
	private Date modified;
	
	/**
	 * The media type of this annotation (MANDATORY)
	 */
	private MediaType mediatype;
	
	/**
	 * The ID of the root annotation, if this annotation
	 * is a reply. A root annotation is the first annotation
	 * in an annotation thread and does not have a root or
	 * parent ID itself. (OPTIONAL)
	 */
	private String rootID = null;
	
	/**
	 * The ID of the parent annotation, if this annotation
	 * is a reply. (OPTIONAL)
	 */
	private String parentID = null;
	
	/**
	 * The text of this annotation (OPTIONAL)
	 */
	private String text = null;
	
	/**
	 * The media fragment this annotation annotates (OPTIONAL)
	 */
	private String fragment = null;
	
	/**
	 * The scope of this annotation (OPTIONAL - will default to PUBLIC)
	 */
	private Scope scope = null;
	
	/**
	 * This annotation's semantic tags (OPTIONAL)
	 */
	private List<SemanticTag> tags = new ArrayList<SemanticTag>();
	
	// Required for JSON mapping
	Annotation() { }
	
	public Annotation(String objectURI, User creator, Date created, Date modified, MediaType mediatype) {
		this.objectURI = objectURI;
		this.creator = creator;
		this.created = created;
		this.modified = modified;
		this.mediatype = mediatype;
	}
	
	public boolean isValid() {
		return this.objectURI != null &&
			   this.creator != null &&
			   this.created != null &&
			   this.modified != null &&
			   this.mediatype != null;
	}
	
	public String getAnnotationID() {
		return annotationID;
	}

	public void setAnnotationID(String annotationID) {
		this.annotationID = annotationID;
	}
	
	public String getObjectURI() {
		return objectURI;
	}
	
	// Required for JSON mapping
	void setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}

	public User getCreator() {
		return creator;
	}
	
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}
	
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
	public MediaType getMediatype() {
		return mediatype;
	}
	
	// Required for JSON mapping
	void setMediatype(MediaType mediatype) {
		this.mediatype = mediatype;
	}

	public String getRootID() {
		return rootID;
	}

	public void setRootID(String rootId) {
		this.rootID = rootId;
	}
	
	public String getParentID() {
		return parentID;
	}
	
	public void setParentID(String parentId) {
		this.parentID = parentId;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}	
	
	public String getFragment() {
		return fragment;
	}
	
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public Scope getScope() {
		if (scope == null)
			return Scope.PUBLIC;
		
		return scope;
	}
	
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public List<SemanticTag> getTags() {
		return tags;
	}
	
	public void addTag(SemanticTag t) {
		tags.add(t);
	}
	
	@Override
	public boolean equals(Object other) {		
		if (!(other instanceof Annotation))
			return false;
		
		Annotation a = (Annotation) other;
		
		if (!a.getObjectURI().equals(this.getObjectURI()))
			return false;
		
		if (!a.getCreator().equals(this.getCreator()))
			return false;

		if (a.getCreated().getTime() != this.getCreated().getTime())
			return false;
		
		if (a.getModified().getTime() != this.getModified().getTime())
			return false;
		
		if (!a.getMediatype().equals(this.getMediatype()))
			return false;

		if (!a.getScope().equals(this.getScope()))
			return false;
		
		if (!equalsNullable(a.getAnnotationID(), this.getAnnotationID()))
			return false;
		
		if (!equalsNullable(a.getRootID(), this.getRootID()))
			return false;
	
		if (!equalsNullable(a.getParentID(), this.getParentID()))
			return false;
		
		if (!equalsNullable(a.getText(), this.getText()))
			return false;
		
		if (!equalsNullable(a.getFragment(), this.getFragment()))
			return false;
				
		List<SemanticTag> myTags = this.getTags();
		List<SemanticTag> othersTags = a.getTags();
		
		if (myTags.size() != othersTags.size())
			return false;
	
		if (!myTags.containsAll(othersTags))
			return false;
		
		if (!othersTags.containsAll(myTags))
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		return (annotationID + 
				objectURI + 
				created.getTime() + 
				modified.getTime() + 
				creator.getUsername()).hashCode();
	}
	
}
