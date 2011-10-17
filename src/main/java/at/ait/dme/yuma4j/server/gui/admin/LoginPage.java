package at.ait.dme.yuma4j.server.gui.admin;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.WebPage;

import at.ait.dme.yuma4j.server.gui.YUMAWebSession;
import at.ait.dme.yuma4j.server.gui.search.Search;

public class LoginPage extends WebPage {

	private PageParameters pageParams;
	
	public LoginPage() {
		this(null);
	}
	
	public LoginPage(PageParameters pageParams) {
		this.pageParams = pageParams;
		
		add(new SignInPanel("signInPanel"));
		add(new OpenIdSignInPanel("openIdSignInPanel", OpenIdAuthenticator.getInstance()));

		checkForOpenIdLogin();
	}
	
	private void checkForOpenIdLogin() {
		if (returnsFromOpenIdAuthentification()) {
			YUMAWebSession session = (YUMAWebSession) AuthenticatedWebSession.get(); 
			signInAndRedirectToHomePage(session);
		}
	}
	
	private boolean returnsFromOpenIdAuthentification() {
		if (pageParams != null && !pageParams.isEmpty()) {
			String isReturn = pageParams.getString("is_return");
		     return isReturn != null && isReturn.equals("true");
		}
		return false;
	}
	
	private void signInAndRedirectToHomePage(YUMAWebSession session) {
		try {
			session.signInOpenId(OpenIdAuthenticator.getInstance(), pageParams);
			if (session.isSignedIn()) {
				setResponsePage(Search.class);
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
