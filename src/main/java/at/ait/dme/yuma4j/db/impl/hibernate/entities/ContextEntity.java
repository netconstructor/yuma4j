package at.ait.dme.yuma4j.db.impl.hibernate.entities;

import javax.persistence.Embeddable;

import at.ait.dme.yuma4j.model.Context;

@Embeddable
public class ContextEntity {
	
	private String contextURI;
	
	private String contextTitle = null;
	
	public ContextEntity() { }
	
	public ContextEntity(Context context) {
		this.contextURI = context.getUri();
		this.contextTitle = context.getTitle();
	}
	
	public Context toContext() {
		return new Context(contextURI, contextTitle);
	}

	public void setURI(String uri) {
		this.contextURI = uri;
	}
	
	public String getURI() {
		return contextURI;
	}
	
	public void setTitle(String title) {
		this.contextTitle = title;
	}
	
	public String getTitle() {
		return contextTitle;
	}
	
}
