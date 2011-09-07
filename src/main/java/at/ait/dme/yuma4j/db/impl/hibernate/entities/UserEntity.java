package at.ait.dme.yuma4j.db.impl.hibernate.entities;

import javax.persistence.Embeddable;

import at.ait.dme.yuma4j.User;

/**
 * A JPA database entity wrapper for the {@link User}.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
@Embeddable
public class UserEntity {

	private String username = null;
	
	private String gravatarHash = null;
	
	private String uri = null;
	
	public UserEntity() { }
	
	public UserEntity(User user) {
		this.setUsername(user.getUsername());
		this.setGravatarHash(user.getGravatarHash());
		this.setUri(user.getURI());
	}
	
	public User toUser() {
		User user = new User(username);
		user.setGravatarHash(gravatarHash);
		user.setURI(uri);
		return user;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setGravatarHash(String gravatarHash) {
		this.gravatarHash = gravatarHash;
	}

	public String getGravatarHash() {
		return gravatarHash;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	
}
