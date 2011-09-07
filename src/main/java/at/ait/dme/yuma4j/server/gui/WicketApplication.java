package at.ait.dme.yuma4j.server.gui;

import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.target.coding.MixedParamUrlCodingStrategy;

import at.ait.dme.yuma4j.server.config.ServerConfig;
import at.ait.dme.yuma4j.server.gui.admin.Dashboard;
import at.ait.dme.yuma4j.server.gui.admin.LoginPage;
import at.ait.dme.yuma4j.server.gui.admin.LogoutPage;
import at.ait.dme.yuma4j.server.gui.feeds.ObjectPage;
import at.ait.dme.yuma4j.server.gui.feeds.RepliesPage;
import at.ait.dme.yuma4j.server.gui.feeds.TimelinePage;
import at.ait.dme.yuma4j.server.gui.feeds.UserPage;
import at.ait.dme.yuma4j.server.gui.search.Search;

/**
 * Application object for your web application. If you want to run 
 * this application without deploying, run the Start class.
 * 
 * @see at.ait.dme.yuma4j.bootstrap.EmbeddedAnnotationServer#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication {    
    
	public WicketApplication() {
		this.mountBookmarkablePage("timeline", TimelinePage.class);
		
		// User feed pages
		this.mount(new MixedParamUrlCodingStrategy(
				"user", 
				UserPage.class,
				new String[]{UserPage.PARAM_USERNAME}
		));

		// Object feed pages
		this.mount(new MixedParamUrlCodingStrategy(
				"object", 
				ObjectPage.class,
				new String[]{ObjectPage.PARAM_OBJECT}
		));
		
		// Replies feed pages
		this.mount(new MixedParamUrlCodingStrategy(
				"replies", 
				RepliesPage.class,
				new String[]{RepliesPage.PARAM_PARENT_ID}
		));

		// Admin area
		this.mountBookmarkablePage("admin", Dashboard.class);
		this.mountBookmarkablePage("admin/login", LoginPage.class);
		this.mountBookmarkablePage("admin/logout", LogoutPage.class);
	}
	
    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass() {
        return YUMAWebSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }
	
    @Override
	public Class<Search> getHomePage() {
		return Search.class;
	}
    
    public static String getConfig() {
    	return ((WicketApplication) WicketApplication.get()).getInitParameter(ServerConfig.INIT_PARAM_PROPERTIES);
    }

}
