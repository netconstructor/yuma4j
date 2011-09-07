package at.ait.dme.yuma4j.db.exception;

/**
 * This Exception indicates that an annotation could not be found.
 * 
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 */
public class AnnotationNotFoundException extends Exception {	
	
	private static final long serialVersionUID = 6008105430121710654L;

	public AnnotationNotFoundException() {
		super();
	}

	public AnnotationNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public AnnotationNotFoundException(String message) {
		super(message);
	}

	public AnnotationNotFoundException(Throwable cause) {
		super(cause);
	}
	
}
