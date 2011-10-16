package at.ait.dme.yuma4j.server.gui.feeds;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.protocol.http.servlet.AbortWithHttpStatusException;

import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.model.Annotation;
import at.ait.dme.yuma4j.server.config.ServerConfig;
import at.ait.dme.yuma4j.server.gui.BaseAnnotationListPage;
import at.ait.dme.yuma4j.server.gui.WicketApplication;

public class RepliesPage extends BaseAnnotationListPage {
	
	private Logger logger = Logger.getLogger(RepliesPage.class);
	
	public static final String PARAM_PARENT_ID = "parentId";
	
	private static final String TITLE = "YUMA Annotation Server - Replies to ";
	private static final String HEADLINE = "Replies to ";
	private static final String FEEDS = "feeds/replies/";
	
	private ServerConfig config = ServerConfig.getInstance(WicketApplication.getConfig());
	
	public RepliesPage(final PageParameters parameters) {
		Annotation parent = 
			getParentAnnotation(parameters.getString(PARAM_PARENT_ID));		
		
		if (parent == null) 
			throw new AbortWithHttpStatusException(404, true);

		setTitle(TITLE + "'" + parent.getID() + "'");
		setHeadline(HEADLINE + "'" + parent.getID() + "'");		
		setAnnotations(getReplies(parent.getID()));
		setFeedURL(config.getServerBaseURL() + FEEDS + parent.getID());
	}
	
	private Annotation getParentAnnotation(String id) {
		AnnotationStore db = null;
		try {
			db = config.getAnnotationStore();
			db.connect();
			return db.getAnnotation(id);
		} catch (AnnotationStoreException e) {
			logger.fatal(e.getMessage());
		} catch (AnnotationNotFoundException e) {
			logger.warn(e.getMessage());
		} finally {
			if (db != null)
				db.disconnect();
		}
		return null;
	}
	
	private List<Annotation> getReplies(String id) {
		AnnotationStore db = null;
		try {
			db = config.getAnnotationStore();
			db.connect();
			return db.listRepliesToAnnotation(id).asFlatList();
		} catch (AnnotationStoreException e) {
			logger.fatal(e.getMessage());
		} catch (AnnotationNotFoundException e) {
			// Should never happen
			logger.fatal(e.getMessage());
		} finally {
			if (db != null)
				db.disconnect();
		}
		return new ArrayList<Annotation>();
	}

}
