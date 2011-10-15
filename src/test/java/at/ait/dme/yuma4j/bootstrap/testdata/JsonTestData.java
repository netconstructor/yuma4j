package at.ait.dme.yuma4j.bootstrap.testdata;

public class JsonTestData {
	
	public static final String ANNOTATION =
		"{ \"text\" : \"The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, capital of Portugal, " + 
		               "to the municipality of Almada on the left bank of the Tagus river. It was inaugurated on August 6, 1966 " +
		               "and a train platform was added in 1999.\", " +
		  "\"modified\" : 1224043200000 , "+
		  "\"created\" : 1224043200000 , "+
		  "\"creator\" :  { \"username\" : \"guest\" } , " +
		  "\"fragment\" : \"bbox(10,10,100,100)\" , " +
		  "\"mediatype\" : \"IMAGE\" , "+
		  "\"objectURI\" : \"http://dme.ait.ac.at/object/lissabon.jpg\" , " +
		  "\"contextURI\" : \"http://dme.ait.ac.at/object/lissabon.html\"" +
		"}";
	
	public static final String ANNOTATION_UPDATE =
		"{ \"text\" : \"The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, capital of Portugal, " +
					   "to the municipality of Almada on the left bank of the Tagus river. It was inaugurated on August 6, 1966 " +
		               "and a train platform was added in 1999. It is often compared to the Golden Gate Bridge in San Francisco, USA, " +
		               "due to their similarities and same construction company. With a total length of 2.277 m, it is the 19th largest " +
		               "suspension bridge in the world. The upper platform carries six car lanes, the lower platform two train tracks. " +
		               "Until 1974 the bridge was named Salazar Bridge.\" , " +
		  "\"modified\" : 1224043200000 , " +
          "\"created\" : 1224043200000 , " +
          "\"creator\" : { \"username\" : \"guest\" } , " +
		  "\"fragment\" : \"bbox(10,10,100,100)\" , " +
		  "\"mediatype\" : \"IMAGE\" , " +
		  "\"objectURI\" : \"http://dme.ait.ac.at/object/lissabon.jpg\" , " +
		  "\"contextURI\" : \"http://dme.ait.ac.at/object/lissabon.html\" , " +
		  "\"tags\" : [" +
		     "{ \"uri\" : \"http://rdf.freebase.com/rdf/lisbon\" , \"labels\":[{ \"lang\" : \"en\" , \"value\" : \"Lisbon\"}] }" +
		  "]}";
	
	public static final String INVALID_ANNOTATION_NO_OBJECT_URI =
		"{ \"modified\" : 1224043200000 , "+
		  "\"created\" : 1224043200000 , " +
		  "\"creator\" :  { \"username\" : \"guest\" } , " +
		  "\"fragment\" : \"bbox(10,10,100,100)\" , " +
		  "\"mediatype\" : \"IMAGE\" , " +
		  "\"contextURI\" : \"http://dme.ait.ac.at/object/lissabon.html\"" +
		"}";
	
	public static final String INVALID_ANNOTATION_NO_CONTEXT_URI =
		"{ \"modified\" : 1224043200000 , "+
		  "\"created\" : 1224043200000 , " +
          "\"creator\" :  { \"username\" : \"guest\" } , " +
          "\"fragment\" : \"bbox(10,10,100,100)\" , " +
          "\"mediatype\" : \"IMAGE\" , " +
		  "\"objectURI\" : \"http://dme.ait.ac.at/object/lissabon.jpg\"" +
        "}";
	
	public static final String INVALID_ANNOTATION_NO_CREATOR =
		"{ \"modified\" : 1224043200000 , "+
		  "\"created\" : 1224043200000 , "+
		  "\"fragment\" : \"bbox(10,10,100,100)\" , " +
		  "\"mediatype\" : \"IMAGE\" , "+
		  "\"objectURI\" : \"http://dme.ait.ac.at/object/lissabon.jpg\" , " +
		  "\"contextURI\" : \"http://dme.ait.ac.at/object/lissabon.html\"" +
		"}";
	
	public static final String INVALID_ANNOTATION_NO_CREATED =
		"{ \"modified\" : 1224043200000 , "+
		  "\"creator\" :  { \"username\" : \"guest\" } , " +
		  "\"fragment\" : \"bbox(10,10,100,100)\" , " +
		  "\"mediatype\" : \"IMAGE\" , "+
		  "\"objectURI\" : \"http://dme.ait.ac.at/object/lissabon.jpg\" , " +
		  "\"contextURI\" : \"http://dme.ait.ac.at/object/lissabon.html\"" +
		"}";
	
	public static final String INVALID_ANNOTATION_NO_MODIFIED =
		"{ \"created\" : 1224043200000 , "+
		  "\"creator\" :  { \"username\" : \"guest\" } , " +
		  "\"fragment\" : \"bbox(10,10,100,100)\" , " +
		  "\"mediatype\" : \"IMAGE\" , "+
		  "\"objectURI\" : \"http://dme.ait.ac.at/object/lissabon.jpg\" , " +
		  "\"contextURI\" : \"http://dme.ait.ac.at/object/lissabon.html\"" +
		"}";
	
	public static final String INVALID_ANNOTATION_NO_MEDIATYPE =
		"{ \"modified\" : 1224043200000 , "+
		  "\"created\" : 1224043200000 , "+
		  "\"creator\" :  { \"username\" : \"guest\" } , " +
		  "\"fragment\" : \"bbox(10,10,100,100)\" , " +
		  "\"objectURI\" : \"http://dme.ait.ac.at/object/lissabon.jpg\" , " +
		  "\"contextURI\" : \"http://dme.ait.ac.at/object/lissabon.html\"" +
		"}";

	private static final String ANNOTATION_REPLY =
		"{ \"parentID\" : \"@parent@\" , " +
		  "\"rootID\" : \"@root@\" , " +
          "\"text\" : \"Interesting!\" , " +
          "\"modified\" : 1224043200000 , " +
          "\"created\" : 1224043200000 , " +
          "\"creator\" : { \"username\" : \"rsimon\" } , " +
          "\"mediatype\" : \"IMAGE\" , " +
          "\"objectURI\" : \"http://dme.ait.ac.at/object/lissabon.jpg\" , " +
		  "\"contextURI\" : \"http://dme.ait.ac.at/object/lissabon.html\"" +
		"}";

	public static String reply(String root, String parent) {
		return ANNOTATION_REPLY
			.replace("@root@", root)
			.replace("@parent@", parent);
	}
	
}
