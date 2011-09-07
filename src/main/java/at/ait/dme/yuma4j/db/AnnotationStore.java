package at.ait.dme.yuma4j.db;

import java.util.List;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.AnnotationTree;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationHasReplyException;
import at.ait.dme.yuma4j.db.exception.AnnotationModifiedException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;

/**
 * Base class for annotation databases.
 * <p>
 * Note on concurrent modifications: We use an optimistic locking strategy.
 * <p>
 * Since only the author of an annotation is allowed to update and delete we do
 * not have to handle concurrent modifications. If any client allowed users to
 * have more than one active session, updates could be lost but we do not
 * consider this to be a problem since the most recent update would be effective.
 * <p>
 * What we do have to handle is annotation creation with interfering/concurrent
 * updates/removals. If a user creates an annotation with a reference to
 * another annotation it has to be ensured that the referenced annotation has
 * not been changed in the time between the last read (see {@link AnnotationModifiedException}). 
 * Further, on update and delete it has to be ensured that the corresponding annotation 
 * is still unreferenced (see {@link AnnotationHasReplyException}). A referenced
 * annotation cannot be updated or deleted.
 *
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public abstract class AnnotationStore {	
	
	/**
	 * Connect to the store
	 * @throws AnnotationStoreException if anything goes wrong
	 */
	public abstract void connect();
	
	/**
	 * Disconnect from the store
	 */
	public abstract void disconnect();
	
	/**
	 * Shut the store down 
	 */
	public abstract void shutdown();
	
	/**
	 * Create a new annotation
	 * @param annotation the annotation
	 * @return the ID of the new annotation
	 * @throws AnnotationStoreException if anything goes wrong
	 * @throws AnnotationModifiedException if the parent annotation was modified in the mean time
	 */
	public abstract String createAnnotation(Annotation annotation) 
			throws AnnotationStoreException, AnnotationModifiedException;

	/**
	 * Update an annotation
	 * @param annotationID the ID of the annotation
	 * @param annotation the annotation
	 * @return the (new) annotation ID after the update (may change depending on store implementation)
	 * @throws AnnotationStoreException if anything goes wrong
	 * @throws AnnotationNotFoundException if there is no annotation with the given ID
	 * @throws AnnotationHasReplyException if this annotation has already been replied to
	 */
	public abstract String updateAnnotation(String annotationID, Annotation annotation)
		throws AnnotationStoreException, AnnotationNotFoundException, AnnotationHasReplyException;

	/**
	 * Delete an annotation
	 * @param annotationID the annotation ID
	 * @throws AnnotationStoreException if anything goes wrong
	 * @throws AnnotationNotFoundException if the annotation does not exist in the store
	 * @throws AnnotationHasReplyException if this annotation has already been replied to
	 */
	public abstract void deleteAnnotation(String annotationID)
		throws AnnotationStoreException, AnnotationNotFoundException, AnnotationHasReplyException;

	/**
	 * Returns all annotations for a given object
	 * @param objectURI the object URI
	 * @return the annotation tree for the object
	 * @throws AnnotationStoreException if anything goes wrong
	 */
	public abstract AnnotationTree listAnnotationsForObject(String objectURI)
		throws AnnotationStoreException;

	/**
	 * Returns the number of annotations for the given object
	 * @param objectURI the object URI
	 * @return the number of annotations for this object
	 * @throws AnnotationStoreException if anything goes wrong
	 */
	public abstract long countAnnotationsForObject(String objectURI)
		throws AnnotationStoreException; 
		
	/**
	 * Returns the annotations for the given user
	 * @param username the user name
	 * @return the annotations
	 * @throws AnnotationStoreException if anything goes wrong
	 */
	public abstract List<Annotation> listAnnotationsForUser(String username)
		throws AnnotationStoreException;

	/**
	 * Retrieve an annotation by its ID
	 * @param annotationID the annotation ID
	 * @return the annotation
	 * @throws AnnotationStoreException if anything goes wrong
	 * @throws AnnotationNotFoundException if the annotation was not found in the store
	 */
	public abstract Annotation getAnnotation(String annotationID)
		throws AnnotationStoreException, AnnotationNotFoundException;
	
	/**
	 * Returns the replies to the annotation with the specified ID
	 * @param annotationID the annotation ID
	 * @return the replies
	 * @throws AnnotationStoreException if anything goes wrong
	 * @throws AnnotationNotFoundException if the annotation was not found in the store
	 */
	public abstract AnnotationTree listRepliesToAnnotation(String annotationID)
		throws AnnotationStoreException, AnnotationNotFoundException;
	
	/**
	 * Returns the number of replies to the annotation with the specified ID
	 * @param annotationID the annotation ID
	 * @return the number of replies
	 * @throws AnnotationStoreException if anything goes wrong
	 * @throws AnnotationNotFoundException if the annotation was not found in the store
	 */
	public abstract long countRepliesToAnnotation(String annotationID)
		throws AnnotationStoreException, AnnotationNotFoundException;

	/**
	 * Retrieves the N most recent annotations from the store
	 * @param n the number of annotations to retrieve
	 * @param publicOnly if true, only annotations with public scope will be returned
	 * @return the annotations
	 * @throws AnnotationStoreException if anything goes wrong
	 */
	public abstract List<Annotation> getMostRecent(int n, boolean publicOnly)
		throws AnnotationStoreException;

	/**
	 * Perform full-text search on the annotations in the store
	 * @param query the search query
	 * @return the result list
	 * @throws AnnotationStoreException if anything goes wrong
	 */
	public abstract List<Annotation> findAnnotations(String query) 
		throws AnnotationStoreException;
	
}