package at.ait.dme.yuma4j.bootstrap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.logging.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import at.ait.dme.yuma4j.bootstrap.testdata.JsonTestData;

public class EmbeddedAnnotationServer {
	
	private static final int SERVER_PORT = 8081;
	
	private static final String CONTEXT_PATH = "/yuma4j-server";
	
	private static Server server = null;
	
	private static Logger log = Logger.getLogger(EmbeddedAnnotationServer.class);

	public static void main(String[] args) throws Exception {
		start(null);
		
		if (args.length > 0) {
			if (args[0].toLowerCase().equals("-testdata"))
				insertTestData();
		}
	}
	
	public static String getApplicationURL() {
		return "http://localhost:" + SERVER_PORT + CONTEXT_PATH;
	}
	
	public static void start(String propertiesFile) throws Exception {
		if (server == null) {
			server = new Server();
			SocketConnector connector = new SocketConnector();
			connector.setMaxIdleTime(1000 * 60 * 60);
			connector.setSoLingerTime(-1);
			connector.setPort(SERVER_PORT);
			server.setConnectors(new Connector[] { connector });
	
			WebAppContext context = new WebAppContext();
			context.setServer(server);
			context.setContextPath(CONTEXT_PATH);
			context.setWar("src/main/webapp");
			
			if (propertiesFile != null) {
				Map<String, String> initParams = new HashMap<String, String>();
				initParams.put("server.properties.file", propertiesFile);
				context.setInitParams(initParams);
			}
			
			server.addHandler(context);
			
			log.info("Starting Annotation Server in embedded Jetty at " + getApplicationURL());
			server.start();
			log.info("Annotation Server available at " + getApplicationURL());
		}
	}
	
	private static void insertTestData() throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
					
		StringEntity createEntity = new StringEntity(JsonTestData.ANNOTATION, "UTF-8");
		createEntity.setContentType("application/json");
		HttpPost createMethod = new HttpPost(getApplicationURL() + "/api/annotation");		
		createMethod.setEntity(createEntity);
		httpClient.execute(createMethod);
		
		log.info("Inserted testdata: " + JsonTestData.ANNOTATION);
	}
	
	public static void stop() throws Exception {
		server.stop();
		server.join();
	}
	
}
