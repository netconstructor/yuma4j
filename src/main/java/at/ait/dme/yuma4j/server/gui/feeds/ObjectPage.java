package at.ait.dme.yuma4j.server.gui.feeds;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.model.Annotation;
import at.ait.dme.yuma4j.server.config.ServerConfig;
import at.ait.dme.yuma4j.server.gui.BaseAnnotationListPage;
import at.ait.dme.yuma4j.server.gui.WicketApplication;

public class ObjectPage extends BaseAnnotationListPage {
	
	private Logger logger = Logger.getLogger(ObjectPage.class);
	
	private static final String UTF8 = "UTF-8";
	
	public static final String PARAM_OBJECT = "object";
	
	private static final String TITLE = "YUMA Annotation Server - ";
	private static final String HEADLINE = "Annotations for ";
	private static final String FEEDS = "feeds/object/";
	
	private ServerConfig config = ServerConfig.getInstance(WicketApplication.getConfig());
	
	public ObjectPage(final PageParameters parameters) {
		try {
			String objectId = URLDecoder.decode(parameters.getString(PARAM_OBJECT), UTF8);		
			String screenName = objectId;

			setTitle(TITLE + screenName);
			setHeadline(HEADLINE + "'" + screenName + "'" );		
			setAnnotations(getAnnotationsForObject(objectId));

			setFeedURL(config.getServerBaseURL() + FEEDS 
					+ URLEncoder.encode(objectId, UTF8).replace("%", "%25"));
		} catch (UnsupportedEncodingException e) {
			// Should never ever happen
			logger.fatal(e.getMessage());
		}
	}
	
	private List<Annotation> getAnnotationsForObject(String objectId) {
		AnnotationStore db = null;		
		try {
			db = config.getAnnotationStore();
			db.connect();
			return db.listAnnotationsForObject(objectId).asFlatList();
		} catch (AnnotationStoreException e) {
			logger.fatal(e.getMessage());
		} finally {
			if(db != null) db.disconnect();
		}
		return new ArrayList<Annotation>();
	}

}
