package at.ait.dme.yuma4j;

import junit.framework.Assert;

import org.junit.Test;

import at.ait.dme.yuma4j.User;

public class UserTest {

	/**
	 * Tests equality between different users. Nothing more to test here, really.
	 */
	@Test
	public void testUserEquality() {
		User user = new User("guest");
		User same = new User("guest");
		User other = new User("other");
		
		Assert.assertEquals(user, same);
		Assert.assertFalse(user.equals(other));
		
		user.setGravatarHash("f9879d71855b5ff21e4963273a886bfc");
		user.setURI("http://www.myserver.com/foaf.rdf#guest");
		Assert.assertFalse(user.equals(same));
		
		same.setGravatarHash("f9879d71855b5ff21e4963273a886bfc");
		same.setURI("http://www.myserver.com/foaf.rdf#guest");
		Assert.assertTrue(user.equals(same));		
	}

}
