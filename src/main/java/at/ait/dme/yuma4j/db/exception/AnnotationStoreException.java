package at.ait.dme.yuma4j.db.exception;

/**
 * This Exception indicates a problem with the annotation store.
 * 
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 */
public class AnnotationStoreException extends Exception {
	
	private static final long serialVersionUID = 3328621278141052963L;

	public AnnotationStoreException() {
		super();
	}

	public AnnotationStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationStoreException(String message) {
		super(message);
	}

	public AnnotationStoreException(Throwable cause) {
		super(cause);
	}
	
}
