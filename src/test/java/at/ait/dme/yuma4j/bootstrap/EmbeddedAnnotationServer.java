package at.ait.dme.yuma4j.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.logging.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class EmbeddedAnnotationServer {
	
	private static final int SERVER_PORT = 8081;
	
	private static final String CONTEXT_PATH = "/yuma4j-server";
	
	private static Server server = null;
	
	private static Logger log = Logger.getLogger(EmbeddedAnnotationServer.class);

	public static void main(String[] args) throws Exception {
		start(null);
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
	
	public static void stop() throws Exception {
		server.stop();
		server.join();
	}
	
}
