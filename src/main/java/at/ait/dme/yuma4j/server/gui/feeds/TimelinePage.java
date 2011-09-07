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
 * The public timeline.
 * 
 * @author Rainer Simon
 */
public class TimelinePage extends BaseAnnotationListPage {
	
	private Logger logger = Logger.getLogger(TimelinePage.class);
	
	private static final String TITLE = "YUMA Annotation Server - Public Timeline";
	private static final String HEADLINE = "public timeline";
	private static final String FEED_URL = "feeds/timeline";
	
	private ServerConfig config = ServerConfig.getInstance(WicketApplication.getConfig());
	
	public TimelinePage(final PageParameters parameters) {
		setTitle(TITLE);
		setHeadline(HEADLINE);
		setAnnotations(getMostRecent(20));
		setFeedURL(config.getServerBaseURL() + FEED_URL);
	}
    
	private List<Annotation> getMostRecent(int n) {
		AnnotationStore db = null;
		List<Annotation> mostRecent = new ArrayList<Annotation>();
		
		try {
			db = config.getAnnotationStore();
			db.connect();
			mostRecent = db.getMostRecent(n, true);
		} catch (AnnotationStoreException e) {
			logger.fatal(e.getMessage());
		} finally {
			if(db != null) db.disconnect();
		}
		
		return mostRecent;
	}

}
