package at.ait.dme.yuma4j.server.gui;

import org.apache.wicket.PageParameters;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.MessageException;

import at.ait.dme.yuma4j.server.config.ServerConfig;
import at.ait.dme.yuma4j.server.gui.admin.OpenIdAuthenticator;

public class YUMAWebSession extends AuthenticatedWebSession {

	private static final long serialVersionUID = -6703698595455093437L;
	
	public YUMAWebSession(Request request) {
        super(request);
    }
	
	public void signInOpenID(OpenIdAuthenticator authenticator, PageParameters pageParameters) 
		throws MessageException, DiscoveryException, AssociationException, ConsumerException {
		
		signIn(authenticator.authenticationSuccess(pageParameters));
	}
		
    @Override
    public boolean authenticate(final String username, final String password) {
    	ServerConfig config = ServerConfig.getInstance(WicketApplication.getConfig());
        return username.equals(config.getAdminUsername()) && password.equals(config.getAdminPassword());
    }
    
    @Override
    public Roles getRoles() {
        if (isSignedIn())
            return new Roles(Roles.ADMIN);

        return null;
    }
    
}