package at.ait.dme.yuma4j.db.impl.hibernate.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.model.Annotation;
import at.ait.dme.yuma4j.model.MediaType;
import at.ait.dme.yuma4j.model.Scope;
import at.ait.dme.yuma4j.model.tags.SemanticTag;

/**
 * A JPA database entity wrapper for an {@link Annotation} object.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
@Entity
@NamedQueries({
	@NamedQuery(name = "annotationentity.find.for.object",
		query = "from AnnotationEntity a where a.objectURI = :objectURI"),

	@NamedQuery(name = "annotationentity.count.for.object",
		query = "select count(*) from AnnotationEntity a where a.objectURI = :objectURI"),

	@NamedQuery(name = "annotationentity.find.for.user",
		query = "from AnnotationEntity a where a.creator.username = :username"),
			
	@NamedQuery(name = "annotationentity.count.for.user",
		query = "select count(*) from AnnotationEntity a where a.creator.username = :username"),
					
	@NamedQuery(name = "annotationentity.find.thread",
		query = "from AnnotationEntity a where a.isReplyTo = :isReplyTo"),

	@NamedQuery(name = "annotationentity.mostrecent.public",
		query = "from AnnotationEntity a where a.scope = 'PUBLIC' order by a.modified desc"),

	@NamedQuery(name = "annotationentity.mostrecent.all",
		query = "from AnnotationEntity a order by a.modified desc"),

	@NamedQuery(name = "annotationentity.searchText",
		query = "from AnnotationEntity a where lower(a.text) like concat('%',:term,'%'))"),

	@NamedQuery(name = "annotationentity.searchTextAndTags",
		query = "select a from AnnotationEntity a " +
				" left join a.tags as tag " +
				" left join tag.labels as label " +
				"where (" +
				" (lower(a.text) like concat('%',:term,'%')) or " +
				" (lower(label.value) like concat('%',lower(:term),'%')) " +
				") ")
})
@Table(name = "annotations")
public class AnnotationEntity implements Serializable {

	private static final long serialVersionUID = 5448003870341885100L;
	
	@Id
	@GeneratedValue
	private Long id;
	
    @Column(length = 512)
	private String objectURI;
    
    @Column(length = 512)
    private ContextEntity context;
    
    @Column
	private UserEntity creator;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
	private Date created;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
	private Date modified;
	
	@Enumerated(EnumType.STRING)
	private MediaType mediatype;
	
	@Column
	private Long isReplyTo;
	
    @Column(length = 4096)		
	private String text;

    @Column(length = 8192)
	private String fragment;
	
	@Enumerated(EnumType.STRING)
	private Scope scope;
	
	@OneToMany(mappedBy = "parent",	targetEntity = SemanticTagEntity.class,	cascade = CascadeType.ALL)
	private List<SemanticTagEntity> tags = new ArrayList<SemanticTagEntity>();

	public AnnotationEntity() { }
	
	public AnnotationEntity(Annotation a)  {
		this.objectURI = a.getObjectURI();
		this.context = new ContextEntity(a.getContext());
		this.creator = new UserEntity(a.getCreator());
		this.created = a.getCreated();
		this.modified = a.getModified();
		this.mediatype = a.getMediatype();
		
		if (a.getIsReplyTo() != null && !a.getIsReplyTo().isEmpty())
			this.isReplyTo = Long.parseLong(a.getIsReplyTo());
		
		this.text = a.getText();
		this.fragment = a.getFragment();
		this.scope = a.getScope();
		
		for (SemanticTag t : a.getTags()) {
			this.tags.add(new SemanticTagEntity(this, t));
		}
	}

	public Annotation toAnnotation() throws AnnotationStoreException {
		Annotation a = 
			new Annotation(objectURI, context.toContext(), creator.toUser(),	created, modified, mediatype);
		
		a.setID(Long.toString(id));
		
		if (isReplyTo != null)
			a.setIsReplyTo(Long.toString(isReplyTo));
		
		a.setText(text);
		a.setFragment(fragment);
		
		for (SemanticTagEntity t : tags) {
			a.addTag(t.toSemanticTag());
		}
		
		return a;
	}
	
	public void setID(Long id) {
		this.id = id;
	}

	public Long getID() {
		return id;
	}
	
	public void setObjectURI(String objectURI) {
		this.objectURI = objectURI;
	}

	public String getObjectURI() {
		return objectURI;
	}
	
	public void setContext(ContextEntity context) {
		this.context = context;
	}
	
	public ContextEntity getContext() {
		return context;
	}
	
	public void setCreator(UserEntity creator) {
		this.creator = creator;
	}

	public UserEntity getCreator() {
		return creator;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getModified() {
		return modified;
	}
	
	public void setMediatype(MediaType mediatype) {
		this.mediatype = mediatype;
	}

	public MediaType getMediatype() {
		return mediatype;
	}

	public void setIsReplyTo(Long replyToID) {
		this.isReplyTo = replyToID;
	}

	public Long getIsReplyTo() {
		return isReplyTo;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	public String getFragment() {
		return fragment;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public Scope getScope() {
		return scope;
	}
	
	public void setTags(List<SemanticTagEntity> tags) {
		this.tags = tags;
	}
	
	public List<SemanticTagEntity> getTags() {
		return tags;
	}

}
