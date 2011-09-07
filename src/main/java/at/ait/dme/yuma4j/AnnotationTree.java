package at.ait.dme.yuma4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the entire tree structure of annotations
 * that exist for an annotated object. The tree consists of
 * root annotations (the annotations that directly reference the
 * annotated object) and replies (annotations that reference another
 * annotation).
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class AnnotationTree {
		
	/**
	 * Root annotations in this tree
	 */
	private ArrayList<Annotation> rootAnnotations = new ArrayList<Annotation>();
	
	/**
	 * Replies in this tree (mapped by parent ID)
	 */
	private HashMap<String, ArrayList<Annotation>> replies = new HashMap<String, ArrayList<Annotation>>();
	
	public AnnotationTree(List<Annotation> annotations) {
		for (Annotation a : annotations) {
			if ((a.getParentID() == null) || a.getParentID().isEmpty()) {
				// Add to root annotation list
				rootAnnotations.add(a);
			} else {
				// Add to appropriate reply list
				ArrayList<Annotation> replyList = replies.get(a.getParentID());
				if (replyList == null) 
					replyList = new ArrayList<Annotation>();
				replyList.add(a);
				
				replies.put(a.getParentID(), replyList);
			}
		}
	}
	
	public List<Annotation> getRootAnnotations() {
		return rootAnnotations;
	}
	
	/**
	 * Returns the child annotations (i.e. direct replies) for the 
	 * given annotation ID.
	 * @param annotationId the annotation ID
	 * @return the child annotations 
	 */
	public List<Annotation> getChildren(String annotationId) {
		List<Annotation> list = replies.get(annotationId);
		if (list == null)
			return new ArrayList<Annotation>();
		
		return list;
	}
	
	/**
	 * Returns a flat list of all annotations in this tree, sorted by
	 * creation date.
	 * @return the list of annotations
	 */
	public List<Annotation> asFlatList() {
		List<Annotation> flatList = new ArrayList<Annotation>(rootAnnotations);
		for(String key : replies.keySet()) {
			flatList.addAll(replies.get(key));
		}

		Collections.sort(flatList, new Comparator<Annotation>() {
			@Override
			public int compare(Annotation a, Annotation b) {
				if (a.getCreated().getTime() > b.getCreated().getTime())
						return 1;
				
				return -1;
			}
		});
		
		return flatList;
	}

}
