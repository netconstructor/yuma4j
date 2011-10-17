package at.ait.dme.yuma4j.server.gui.admin;

import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

@SuppressWarnings("serial")
class OpenIdSignInPanel extends Panel {

	private OpenIdAuthenticator openIdAuthenticator;
	private TextField<String> openId;
	
	private class OpenIdForm extends StatelessForm<Void>
	{
		public OpenIdForm(String id) {
			super(id);
			
			openId = new TextField<String>("openid", new Model<String>("")); 
			add(openId);
		}

		@Override
		protected void onSubmit() {
			try {
				openIdAuthenticator.performAuthentication(openId.getInput());
			} 
			catch (Exception e) {
				// TODO: integrate with wicket form feedback mechanism
				System.out.println("login failed");
			}
		}
	}
	
	public OpenIdSignInPanel(String id, OpenIdAuthenticator openIdAuthenticator) {
		super(id);
		this.openIdAuthenticator = openIdAuthenticator;
		
		add(new OpenIdForm("openIdSignInForm"));
	}
	
}
