package at.ait.dme.yuma4j.model;


public class Context extends AbstractModelPOJO {

	private static final long serialVersionUID = -7792675239649041690L;

	/**
	 * The URI (MANDATORY)
	 */
	private String uri;
	
	/**
	 * The title (OPTIONAL)
	 */
	private String title = null;
	
	// Required for JSON mapping
	public Context() { }
	
	public Context(String uri) {
		this.uri = uri;
	}
	
	public Context(String uri, String title) {
		this.uri = uri;
		this.title = title;
	}

	public String getUri() {
		return uri;
	}

	// Required for JSON mapping
	void setUri(String uri) {
		this.uri = uri;
	}

	public String getTitle() {
		return title;
	}

	// Required for JSON mapping
	void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Context))
			return false;

		Context c = (Context) other;

		if (!c.getUri().equals(this.getUri()))
			return false;

		if (!equalsNullable(this.getTitle(), c.getTitle()))
			return false;

		return true;
	}
	
	@Override
	public int hashCode() {
		return uri.hashCode();
	}
	
}
