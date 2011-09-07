package at.ait.dme.yuma4j.server.gui.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.server.config.ServerConfig;
import at.ait.dme.yuma4j.server.gui.BaseAnnotationListPage;

/**
 * The search result page.
 * 
 * @author Rainer Simon
 */
public class Results extends BaseAnnotationListPage {
	
	private static final String TITLE = "YUMA Annotation Server - Search";
	private static final String ANNOTATION_FOUND = " Annotation Found";
	private static final String ANNOTATIONS_FOUND = " Annotations Found";

    public Results(final PageParameters parameters) {
    	setTitle(TITLE);

    	try {
	    	List<Annotation> searchResults = 
	    		findAnnotations((String) parameters.get(Search.QUERY_PARAM));
	    	
	    	int numResults = searchResults.size();
	    	if (numResults == 1) {
	    		setHeadline(Integer.toString(searchResults.size()) + ANNOTATION_FOUND);
	    	} else {
	    		setHeadline(Integer.toString(searchResults.size()) + ANNOTATIONS_FOUND);	    		
	    	}
	    	
			setAnnotations(searchResults);
    	} catch (AnnotationStoreException e) {
    		setHeadline("Sorry: " + e.getMessage());
    		setAnnotations(new ArrayList<Annotation>());
    	}
		setFeedURL(null);
    }
    
	private List<Annotation> findAnnotations(String query) throws AnnotationStoreException {
		AnnotationStore db = null;
		List<Annotation> searchResults = new ArrayList<Annotation>();
		
		try {
			db = ServerConfig.getInstance().getAnnotationStore();
			db.connect();
			searchResults = db.findAnnotations(query);
		} finally {
			if(db != null) db.disconnect();
		}
		
		return searchResults;
	}

}
