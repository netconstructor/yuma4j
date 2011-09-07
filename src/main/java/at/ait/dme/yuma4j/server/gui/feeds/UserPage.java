package at.ait.dme.yuma4j.server.gui.feeds;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.server.config.ServerConfig;
import at.ait.dme.yuma4j.server.gui.BaseAnnotationListPage;
import at.ait.dme.yuma4j.server.gui.WicketApplication;

/**
 * A user's public feed page.
 * 
 * TODO the feed page is actually NOT public right now - also 
 * includes the private annotations!
 * 
 * @author Rainer Simon
 */
public class UserPage extends BaseAnnotationListPage {

	private Logger logger = Logger.getLogger(UserPage.class);
	
	public static final String PARAM_USERNAME = "username";
	
	private static final String TITLE = "YUMA Annotation Server - User ";
	private static final String HEADLINE = "'s public feed";
	private static final String FEEDS = "feeds/user/";
	
	private ServerConfig config = ServerConfig.getInstance(WicketApplication.getConfig());
	
	public UserPage(final PageParameters parameters) {
		String username = parameters.getString(PARAM_USERNAME);		
		
		setTitle(TITLE + username);
		setHeadline(username + HEADLINE);		
		setAnnotations(getAnnotationsByUser(username));
		setFeedURL(config.getServerBaseURL() + FEEDS + username);
	}
	
	private List<Annotation> getAnnotationsByUser(String username) {
		AnnotationStore db = null;
		List<Annotation> annotations = new ArrayList<Annotation>();
		
		try {
			db = config.getAnnotationStore();
			db.connect();
			annotations = db.listAnnotationsForUser(username);
		} catch (AnnotationStoreException e) {
			logger.fatal(e.getMessage());
		} finally {
			if(db != null) db.disconnect();
		}
		
		return annotations;
	}

}
