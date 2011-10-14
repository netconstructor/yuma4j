package at.ait.dme.yuma4j.server.gui.admin;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.WebPage;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.message.MessageException;

import at.ait.dme.yuma4j.server.gui.YUMAWebSession;

public class LoginPage extends WebPage {

	private OpenIdAuthenticator openIdAuthenticator;
	
	public LoginPage() {
		this(null);
	}
	
	public LoginPage(PageParameters pageParams) {
		openIdAuthenticator = new OpenIdAuthenticator();
		
		add(new SignInPanel("signInPanel"));
		add(new OpenIdSignInPanel("openIdSignInPanel", openIdAuthenticator));
		
		if (pageParams != null && !pageParams.isEmpty()) {
			String isReturn = pageParams.getString("is_return");
		    if (isReturn != null && isReturn.equals("true")) {
		    	try {
					((YUMAWebSession) AuthenticatedWebSession.get()).signInOpenId(openIdAuthenticator, pageParams);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
	}
	
}
