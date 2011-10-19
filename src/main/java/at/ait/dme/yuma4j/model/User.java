package at.ait.dme.yuma4j.model;


/**
 * The user.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class User extends AbstractModelPOJO {

	private static final long serialVersionUID = 6615570115330996726L;

	/**
	 * The (unique) username (MANDATORY)
	 */
	private String username = null;
	
	/**
	 * The user's 'real name' (OPTIONAL)
	 */
	private String name = null;
	
	/**
	 * The user's avatar URL (OPTIONAL) 
	 */
	private String avatarURL = null;
	
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
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getAvatarURL() {
		return avatarURL;
	}
	
	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
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
		
		if (!equalsNullable(this.name, user.name))
			return false;
		
		if (!equalsNullable(this.uri, user.uri))
			return false;
		
		if (!equalsNullable(this.avatarURL, user.avatarURL))
			return false;
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return this.username.hashCode();
	}
	
	@Override
	public String toString() {
		return username;
	}

}
