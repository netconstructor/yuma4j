package at.ait.dme.yuma4j.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

import at.ait.dme.yuma4j.model.tags.SemanticTag;


/**
 * The annotation.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class Annotation extends AbstractModelPOJO {

	private static final long serialVersionUID = 8805537432094228535L;

	/**
	 * The ID of this annotation (NOTE: the annotation ID is 
	 * usually auto-assigned by the database!)
	 */
	private String id = null;
	
	/**
	 * The URI of the object which this annotation 
	 * annotates (MANDATORY)
	 */
	private String objectURI;
	
	/**
	 * The Web context (i.e. the enclosing page) in which the 
	 * object was annotated (MANDATORY)
	 */
	private Context context;
	
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
	 * If this annotation is a reply, this field holds
	 * the ID of the 'root' annotation to which this
	 * annotation is a reply to (OPTIONAL)
	 */
	private String isReplyTo = null;
	
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
	
	public Annotation(String objectURI, Context context, User creator, Date created, Date modified, MediaType mediatype) {
		this.objectURI = objectURI;
		this.context = context;
		this.creator = creator;
		this.created = created;
		this.modified = modified;
		this.mediatype = mediatype;
	}
	
	@JsonIgnore
	public boolean isValid() {
		return this.objectURI != null &&
			   this.context != null &&
			   this.creator != null &&
			   this.created != null &&
			   this.modified != null &&
			   this.mediatype != null;
	}
	
	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}
	
	public String getObjectURI() {
		return objectURI;
	}
	
	// Required for JSON mapping
	void setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}
	
	public Context getContext() {
		return context;
	}
	
	// Required for JSON mapping
	void setContext(Context context) {
		this.context = context;
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

	public String getIsReplyTo() {
		return isReplyTo;
	}

	public void setIsReplyTo(String parentId) {
		this.isReplyTo = parentId;
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
		
		if (!a.getContext().equals(this.getContext()))
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
		
		if (!equalsNullable(a.getID(), this.getID()))
			return false;
		
		if (!equalsNullable(a.getIsReplyTo(), this.getIsReplyTo()))
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
		return (id + 
				objectURI + 
				created.getTime() + 
				modified.getTime() + 
				creator.getUsername()).hashCode();
	}
	
}
