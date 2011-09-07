package at.ait.dme.yuma4j.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import at.ait.dme.yuma4j.db.AnnotationStore;
import at.ait.dme.yuma4j.db.exception.AnnotationStoreException;

/**
 * Configuration settings for the annotation storeage server.
 * 
 * @author Christian Sadilek <christian.sadilek@gmail.com>
 * @author Rainer Simon <rainer.simon@ait.ac.at> 
 */
public class ServerConfig {

	public static final String INIT_PARAM_PROPERTIES = "server.properties.file";
	
	private static final String DEFAULT_PROPERTIES_FILE = "server.properties";
	
	private static Map<String, ServerConfig> instances = new HashMap<String, ServerConfig>();
	
	private static final String SERVER_BASE_URL = "server.base.url";
	private static final String ADMIN_USERNAME = "admin.username";
	private static final String ADMIN_PASSWORD = "admin.password";
	private static final String ANNOTATION_STORE_CLASS = "annotation.store.class";

	private String serverBaseURL;
	private String adminUsername;
	private String adminPassword;
	private AnnotationStore annotationStore;

	private ServerConfig(String propertiesFilename) {
		Properties properties = new Properties();
		InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesFilename);
		if (is == null)
			throw new RuntimeException("Fatal error: " + propertiesFilename + " not found");

		try {
			properties.load(is);
		} catch (IOException e) {
			throw new RuntimeException("Fatal error: could not load " + propertiesFilename + ": " + e.getMessage());
		}

		serverBaseURL = properties.getProperty(SERVER_BASE_URL);
		adminUsername = properties.getProperty(ADMIN_USERNAME);
		adminPassword = properties.getProperty(ADMIN_PASSWORD);
		
		if (serverBaseURL == null ||
			adminUsername == null ||
			adminPassword == null)
			
			throw new RuntimeException("Fatal error: " + propertiesFilename + " is missing parameter(s)");
			
		String storeImpl = properties.getProperty(ANNOTATION_STORE_CLASS);
		try {
			Class<?> annotationStoreImplClass = Class.forName(storeImpl);
			Object obj = annotationStoreImplClass.newInstance();
			
			if (obj instanceof AnnotationStore) {
				annotationStore = (AnnotationStore) obj;
				annotationStore.init(properties);
			} else {
				throw new InstantiationException();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ServerConfig getInstance(String propertiesFilename) {
		if (propertiesFilename == null)
			propertiesFilename = DEFAULT_PROPERTIES_FILE; 
				
		if (instances.get(propertiesFilename) == null)
			instances.put(propertiesFilename, new ServerConfig(propertiesFilename));

		return instances.get(propertiesFilename);
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
