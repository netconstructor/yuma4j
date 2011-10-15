package at.ait.dme.yuma4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import junit.framework.Assert;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma4j.Annotation;
import at.ait.dme.yuma4j.MediaType;
import at.ait.dme.yuma4j.PlainLiteral;
import at.ait.dme.yuma4j.Scope;
import at.ait.dme.yuma4j.SemanticTag;
import at.ait.dme.yuma4j.bootstrap.testdata.JsonTestData;

public class AnnotationTest {
	
	private static ObjectMapper jsonMapper = new ObjectMapper();
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
	
	@BeforeClass
	public static void setUp() throws Exception {		
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Test
	public void testAnnotationEquality() throws JsonParseException, IOException, ParseException, URISyntaxException {
		Annotation a = jsonMapper.readValue(JsonTestData.ANNOTATION, Annotation.class);
		Assert.assertTrue(a.isValid());
		Assert.assertEquals("http://dme.ait.ac.at/object/lissabon.jpg", a.getObjectURI());
		Assert.assertEquals("guest", a.getCreator().getUsername());
		Assert.assertEquals(dateFormat.parse("2008-10-15T04:00:00Z"), a.getCreated());
		Assert.assertEquals(dateFormat.parse("2008-10-15T04:00:00Z"), a.getModified());
		Assert.assertEquals(MediaType.IMAGE, a.getMediatype());
		Assert.assertEquals(null, a.getRootID());
		Assert.assertEquals(null, a.getParentID());
		Assert.assertEquals("The 25 de Abril Bridge is a suspension bridge connecting the city of " +
				"Lisbon, capital of Portugal, to the municipality of Almada on the left " +
				"bank of the Tagus river. It was inaugurated on August 6, 1966 and a train " +
				"platform was added in 1999.", a.getText());
		Assert.assertEquals("fragment", "bbox(10,10,100,100)", a.getFragment());
		Assert.assertEquals(Scope.PUBLIC, a.getScope());
		
		Annotation b = jsonMapper.readValue(JsonTestData.ANNOTATION, Annotation.class);
		Assert.assertTrue(b.isValid());
		Assert.assertEquals(a, b);
		
		b.addTag(new SemanticTag(new URI("http://rdf.freebase.com/rdf/lisbon"), new PlainLiteral("Lisbon")));
		System.out.println(jsonMapper.writeValueAsString(b));
		Assert.assertTrue(b.isValid());
		Assert.assertFalse(a.equals(b));
		
		a.addTag(new SemanticTag(new URI("http://rdf.freebase.com/rdf/lisbon"), new PlainLiteral("Lisbon")));
		Assert.assertEquals(a, b);
	}
	
	@Test
	public void testInvalidAnnotations() throws JsonParseException, IOException {
		Annotation a = jsonMapper.readValue(JsonTestData.INVALID_ANNOTATION_NO_OBJECT_URI, Annotation.class);
		Assert.assertFalse(a.isValid());
		
		a = jsonMapper.readValue(JsonTestData.INVALID_ANNOTATION_NO_CONTEXT_URI, Annotation.class);
		Assert.assertFalse(a.isValid());

		a = jsonMapper.readValue(JsonTestData.INVALID_ANNOTATION_NO_CREATOR, Annotation.class);
		Assert.assertFalse(a.isValid());

		a = jsonMapper.readValue(JsonTestData.INVALID_ANNOTATION_NO_CREATED, Annotation.class);
		Assert.assertFalse(a.isValid());

		a = jsonMapper.readValue(JsonTestData.INVALID_ANNOTATION_NO_MODIFIED, Annotation.class);
		Assert.assertFalse(a.isValid());

		a = jsonMapper.readValue(JsonTestData.INVALID_ANNOTATION_NO_MEDIATYPE, Annotation.class);
		Assert.assertFalse(a.isValid());
	}

}
