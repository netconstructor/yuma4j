package at.ait.dme.yuma4j.db.impl.hibernate.entities;

import javax.persistence.Embeddable;

import at.ait.dme.yuma4j.model.User;

/**
 * A JPA database entity wrapper for the {@link User}.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
@Embeddable
public class UserEntity {

	private String username = null;
	
	private String userFullname = null;
	
	private String avatarURL= null;
	
	private String userURI = null;
	
	public UserEntity() { }
	
	public UserEntity(User user) {
		this.setUsername(user.getUsername());
		this.setName(user.getName());
		this.setAvatarURL(user.getAvatarURL());
		this.setUri(user.getURI());
	}
	
	public User toUser() {
		User user = new User(username);
		user.setName(userFullname);
		user.setAvatarURL(avatarURL);
		user.setURI(userURI);
		return user;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
	
	public void setName(String name) {
		this.userFullname = name;
	}
	
	public String getName() {
		return userFullname;
	}

	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}

	public String getAvatarURL() {
		return avatarURL;
	}

	public void setUri(String uri) {
		this.userURI = uri;
	}

	public String getUri() {
		return userURI;
	}
	
}
