package at.ait.dme.yuma4j.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;

/**
 * Configuration settings for the annotation server.
 * 
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 */
public class ServerConfig {

	private static ServerConfig instance = null;
	
	private static final String SERVER_BASE_URL = "server.base.url";
	private static final String ADMIN_USERNAME = "admin.username";
	private static final String ADMIN_PASSWORD = "admin.password";
	private static final String ANNOTATION_STORE_CLASS = "annotation.store.class";

	private String serverBaseURL;
	private String adminUsername;
	private String adminPassword;
	private AnnotationStore annotationStore;

	private ServerConfig() {
		Properties props = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream("server.properties");
		if (is == null)
			throw new RuntimeException("Fatal error: server.properties not found");

		try {
			props.load(is);
		} catch (IOException e) {
			throw new RuntimeException("Fatal error: could not load server.properties: " + e.getMessage());
		}
		
		serverBaseURL = props.getProperty(SERVER_BASE_URL);
		adminUsername = props.getProperty(ADMIN_USERNAME);
		adminPassword = props.getProperty(ADMIN_PASSWORD);
		
		if (serverBaseURL == null ||
			adminUsername == null ||
			adminPassword == null)
			
			throw new RuntimeException("Fatal error: server.properties is missing parameter(s)");
			
		String storeImpl = props.getProperty(ANNOTATION_STORE_CLASS);
		try {
			Class<?> annotationStoreImplClass = Class.forName(storeImpl);
			Object obj = annotationStoreImplClass.newInstance();
			
			if (obj instanceof AnnotationStore) {
				annotationStore = (AnnotationStore) obj;
			} else {
				throw new InstantiationException();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ServerConfig getInstance() {
		if (instance == null)
			instance = new ServerConfig();

		return instance;
	}
	
	public String getServerBaseURL() {
		return serverBaseURL;
	}
	
	public String getAdminUsername() {
		return adminUsername;
	}
	
	public String getAdminPassword() {
		return adminPassword;
	}
	
	public AnnotationStore getAnnotationStore() throws AnnotationStoreException {
		return annotationStore;
	}
	
}
