package at.ait.dme.yuma4j.db.impl.hibernate.entities;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import at.ait.dme.yuma4j.PlainLiteral;
import at.ait.dme.yuma4j.SemanticTag;

/**
 * A JPA database entity wrapper for a {@link SemanticTag} object.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
@Entity
@Table(name = "tags")
public class SemanticTagEntity implements Serializable {

	private static final long serialVersionUID = -7648256413169945758L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "annotations_id")
	private AnnotationEntity parent;
	
	@Column
	private String uri;
	
	@OneToMany(mappedBy = "parent", targetEntity = PlainLiteralEntity.class, cascade = CascadeType.ALL)
	private List<PlainLiteralEntity> labels = new ArrayList<PlainLiteralEntity>();

	@OneToMany(mappedBy = "parent",	targetEntity = PlainLiteralEntity.class, cascade = CascadeType.ALL)
	private List<PlainLiteralEntity> descriptions = new ArrayList<PlainLiteralEntity>();
	
	public SemanticTagEntity() { }
	
	public SemanticTagEntity(AnnotationEntity parent, SemanticTag t) {
		this.setParent(parent);
		this.setUri(t.getURI().toString());
		
		for (PlainLiteral l : t.getLabels()) {
			labels.add(new PlainLiteralEntity(this, l));
		}
		
		for (PlainLiteral d : t.getDescriptions()) {
			descriptions.add(new PlainLiteralEntity(this, d));
		}
	}
	
	public SemanticTag toSemanticTag()  {
		try {
			SemanticTag t = new SemanticTag(new URI(uri), labels.get(0).toPlainLiteral());
			
			for (int i=1; i<labels.size(); i++) {
				t.addLabel(labels.get(i).toPlainLiteral());
			}
			
			for (PlainLiteralEntity e : descriptions) {
				t.addDescription(e.toPlainLiteral());
			}
			return t;
		} catch (URISyntaxException e) {
			// Should never happen
			throw new RuntimeException(e);
		}
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public void setParent(AnnotationEntity parent) {
		this.parent = parent;
	}

	public AnnotationEntity getParent() {
		return parent;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setLabels(List<PlainLiteralEntity> labels) {
		this.labels = labels;
	}

	public List<PlainLiteralEntity> getLabels() {
		return labels;
	}

	public void setDescriptions(List<PlainLiteralEntity> descriptions) {
		this.descriptions = descriptions;
	}

	public List<PlainLiteralEntity> getDescriptions() {
		return descriptions;
	}

}
