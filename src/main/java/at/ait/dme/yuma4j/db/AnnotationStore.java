package at.ait.dme.yuma4j.db;

import java.util.List;
import java.util.Map;

import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;
import at.ait.dme.yuma4j.db.exception.AnnotationNotFoundException;
import at.ait.dme.yuma4j.db.exception.DeleteNotAllowedException;
import at.ait.dme.yuma4j.model.Annotation;

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
	 * Initializes the store
	 * @param initparams an optional map of (implementation-specific) init parameters
	 */
	public abstract void init(Map<Object, Object> initparams);
	
	/**
	 * Connect to the store
	 * @throws AnnotationStoreException if anything goes wrong
	 */
	public abstract void connect() throws AnnotationStoreException;
	
	/**
	 * Disconnect from the store
	 */
	public abstract void disconnect();
	
	/**
	 * Shut the store down 
	 */
	public abstract void shutdown();
	
	public abstract String createAnnotation(Annotation annotation) 
			throws AnnotationStoreException;

	public abstract void updateAnnotation(String annotationID, Annotation annotation)
		throws AnnotationStoreException, AnnotationNotFoundException;

	public abstract void deleteAnnotation(String annotationID)
		throws AnnotationStoreException, AnnotationNotFoundException, DeleteNotAllowedException;

	public abstract List<Annotation> listAnnotationsForObject(String objectURI)
		throws AnnotationStoreException;

	public abstract long countAnnotationsForObject(String objectURI)
		throws AnnotationStoreException; 
		
	public abstract List<Annotation> listAnnotationsForUser(String username)
		throws AnnotationStoreException;

	public abstract Annotation getAnnotation(String annotationID)
		throws AnnotationStoreException, AnnotationNotFoundException;
	
	public abstract List<Annotation> listRepliesToAnnotation(String annotationID)
		throws AnnotationStoreException, AnnotationNotFoundException;
	
	public abstract List<Annotation> getMostRecent(int n, boolean publicOnly)
		throws AnnotationStoreException;

	public abstract List<Annotation> findAnnotations(String query) 
		throws AnnotationStoreException;
	
}