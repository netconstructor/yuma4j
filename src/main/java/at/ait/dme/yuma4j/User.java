package at.ait.dme.yuma4j;

/**
 * The user.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class User extends AbstractModelPOJO {

	/**
	 * The user screen name (MANDATORY)
	 */
	private String username = null;
	
	/**
	 * The user's Gravatar hash (OPTIONAL) 
	 */
	private String gravatarHash = null;
	
	/**
	 * The user's personal (e.g. FOAF) URI (OPTIONAL)
	 */
	private String uri = null;
	
	// Required for JSON mapping
	User() { }
	
	public User(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
	
	// Required for JSON mapping
	void setUsername(String username) {
		this.username = username;
	}

	public String getGravatarHash() {
		return gravatarHash;
	}
	
	public void setGravatarHash(String gravatarHash) {
		this.gravatarHash = gravatarHash;
	}

	public String getURI() {
		return uri;
	}
	
	public void setURI(String uri) {
		this.uri = uri;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof User))
			return false;
		
		User user = (User) other;
		
		if (!this.username.equals(user.username))
			return false;
		
		if (!equalsNullable(this.uri, user.uri))
			return false;
		
		if (!equalsNullable(this.gravatarHash, user.gravatarHash))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.username.hashCode();
	}

}
