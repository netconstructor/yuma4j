package at.ait.dme.yuma4j.db.impl.hibernate.entities;

import javax.persistence.Embeddable;

import at.ait.dme.yuma4j.Context;

@Embeddable
public class ContextEntity {
	
	private String uri;
	
	private String title = null;
	
	public ContextEntity() { }
	
	public ContextEntity(Context context) {
		this.uri = context.getUri();
		this.title = context.getTitle();
	}
	
	public Context toContext() {
		return new Context(uri, title);
	}

	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public String getURI() {
		return uri;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
}
