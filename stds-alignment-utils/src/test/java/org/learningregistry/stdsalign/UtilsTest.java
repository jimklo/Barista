package org.learningregistry.stdsalign;

import java.text.MessageFormat;

import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class UtilsTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UtilsTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( UtilsTest.class );
    }


    public void testHasResourceDataAsString() throws JSONException
    {
        assertFalse("JSON has no resource_data", Utils.hasResourceDataAsString(new JSONObject()));
        assertFalse("JSON has resource_data as map", Utils.hasResourceDataAsString(new JSONObject("{\"resource_data\":{}}")));
        assertFalse("JSON has resource_data as array", Utils.hasResourceDataAsString(new JSONObject("{\"resource_data\":[]}")));
        assertFalse("JSON has resource_data as empty String", Utils.hasResourceDataAsString(new JSONObject("{\"resource_data\":\"\"}")));
        assertFalse("JSON has resource_data as null", Utils.hasResourceDataAsString(new JSONObject("{\"resource_data\":null}")));
        
        assertTrue("JSON has resource_data is non-empty String", Utils.hasResourceDataAsString(new JSONObject("{\"resource_data\":\"non-empty\"}")));
    }
    
    
    public void testGetDCTConformsToPatterns() throws JSONException {
    	
    	final String json = "'{'\"resource_data\":\"{0}\"'}'";
    	
    	String data = "<nsdl_dc:nsdl_dc xmlns:nsdl_dc=\\\"http://ns.nsdl.org/nsdl_dc_v1.02/\\\" xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:dct=\\\"http://purl.org/dc/terms/\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xmlns=\\\"http://www.openarchives.org/OAI/2.0/\\\" schemaVersion=\\\"1.02.000\\\" xsi:schemaLocation=\\\"http://ns.nsdl.org/nsdl_dc_v1.02/ http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.02.xsd\\\">\\n  <dc:identifier xsi:type=\\\"dct:URI\\\">http://www.nationalgeographic.com/xpeditions/lessons/15/gk2/tornadowhat.html</dc:identifier>\\n  <dct:isPartOf xsi:type=\\\"dct:URI\\\">http://www.nationalgeographic.com/xpeditions/index.html</dct:isPartOf>\\n  <dct:conformsTo xsi:type=\\\"dct:URI\\\">http://purl.org/ASN/resources/S103E245</dct:conformsTo>\\n  <dct:conformsTo xsi:type=\\\"dct:URI\\\">http://purl.org/ASN/resources/S103E24D</dct:conformsTo>\\n  <dc:type xsi:type=\\\"dct:DCMIType\\\">InteractiveResource</dc:type>\\n  <dc:type xsi:type=\\\"nsdl_dc:NSDLType\\\">Instructional Material</dc:type>\\n  <dc:type xsi:type=\\\"nsdl_dc:NSDLType\\\">Lesson/Lesson Plan</dc:type>\\n  <dc:format xsi:type=\\\"dct:IMT\\\">text/html</dc:format>\\n  <dc:language xsi:type=\\\"dct:RFC3066\\\">en</dc:language>\\n  <dc:title>What is a Tornado?</dc:title>\\n  <dc:subject>Atmospheric science</dc:subject>\\n  <dc:subject>Natural hazards</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Science</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Earth science</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Physical sciences</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Meteorology</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Astronomy</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Space sciences</dc:subject>\\n  <dc:description>In this lesson plan students will learn the basics about how tornadoes are formed and when and where they are most likely to occur. They will learn that the United States is the country most vulnerable to tornadoes and why most tornadoes occur in Tornado Alley. Students will create a tornado in a bottle. Finally, they will draw pictures of tornadoes of different shapes and sizes and review basic safety information about what to do in case of a tornado.</dc:description>\\n  <dc:publisher>National Geographic Society</dc:publisher>\\n  <dc:rights>Copyright 2003 National Geographic Society. All rights reserved.</dc:rights>\\n  <dct:educationLevel xsi:type=\\\"nsdl_dc:NSDLEdLevel\\\">Elementary School</dct:educationLevel>\\n</nsdl_dc:nsdl_dc>";
    	
    	String[] patterns = {
    			"https?://purl.org/ASN/.*"
    	};
    	
 
    	String resource_data = MessageFormat.format(json, data); 
    	
    	String[] res = Utils.getDCTConformsToPatterns(new JSONObject(resource_data), patterns);
    	assertNotNull("resource_data contains matching patterns but got no result.",res);
    	
    	for (String r : res) {
    		boolean pass = false;
    		for (String p : patterns) {
    			if (r.matches(p)) {
    				pass = true;
    				break;
    			}
    		}
    		assertTrue(MessageFormat.format("resource_data got match '{0}' does not match any expected pattern.", r), pass);
    	}
    	
    	String[] antipatterns = {
    			"https?://purl.com/ASN/.*"
    	};
    	
    	assertNull("resource_data got a match when it should have had no matches.", Utils.getDCTConformsToPatterns(new JSONObject(resource_data), antipatterns));
    	
    }
    
    public void testHasResourceDataAsJSONObject() throws JSONException {
    	assertTrue("resource_data is JSON object but reported as not", Utils.hasResourceDataAsJSONObject(new JSONObject( "{\"resource_data\":{}}")));
    	assertFalse("resource_data is not JSON object but reported as JSON object", Utils.hasResourceDataAsJSONObject(new JSONObject( "{\"resource_data\":\"\"}")));
    }
    
    public void testHasResourceDataAsJSONString() throws JSONException {
    	assertTrue("resource_data has JSON String but reported as not", Utils.hasResourceDataAsJSONString(new JSONObject( "{\"resource_data\":\"{}\"}")));
    	assertFalse("resource_data is not JSON String but reported as JSON String", Utils.hasResourceDataAsJSONString(new JSONObject( "{\"resource_data\":\"\"}")));
    	assertFalse("resource_data is JSON Object but reported as JSON String", Utils.hasResourceDataAsJSONString(new JSONObject( "{\"resource_data\":{}}")));
    }
    
    public void testGetResourceDataAsJSON() throws JSONException {
    	assertNotNull("resource_data is JSON Object but returned null", Utils.getResourceDataAsJSON(new JSONObject("{\"resource_data\":{}}")));
    	assertNotNull("resource_data is JSON String but returned null", Utils.getResourceDataAsJSON(new JSONObject("{\"resource_data\":\"{}\"}")));
    	assertNull("resource_data is JSON Array but returned non-null", Utils.getResourceDataAsJSON(new JSONObject("{\"resource_data\":[]}")));
    	assertNull("resource_data is Empty String but returned non-null", Utils.getResourceDataAsJSON(new JSONObject("{\"resource_data\":\"\"}")));
    }
    
    
    public void testFindPatternInJSON() throws JSONException {
    	
    	final JSONObject alignment_paradata = new JSONObject("{\n\"activity\": {\n\"actor\": {\n\"description\": [\n\"9\",\n\"10\",\n\"English Language Arts\"\n],\n\"objectType\": \"educator\"\n},\n\"verb\": {\n\"action\": \"matched\",\n\"date\": \"2011-11-07\",\n\"context\": {\n\"id\": \"http://www.myboe.org/go/resource/7238\",\n\"description\": \"Brokers of Expertise resource management page\",\n\"objectType\": \"LMS\"\n}\n},\n\"object\": {\n\"id\": \"http://www.readwritethink.org/lessons/lesson_view.asp?id=131\"\n},\n\"related\": [\n{\n\"objectType\": \"academic standard\",\n\"id\": \"http://purl.org/ASN/resources/S101282A\",\n\"content\": \"Select a focus when writing.\"\n}\n],\n\"content\": \"A resource found at http://www.myboe.org/go/resource/7238 was matched to the academic content standard with ID http://purl.org/ASN/resources/S101282A by an educator of multiple grades and English Language Arts on November 7, 2011\"\n}\n}");
    	final JSONObject non_align_paradata = new JSONObject("{\n\"activity\": {\n\"actor\": {\n\"description\": [\n\"9\",\n\"10\",\n\"English Language Arts\"\n],\n\"objectType\": \"educator\"\n},\n\"verb\": {\n\"action\": \"matched\",\n\"date\": \"2011-11-07\",\n\"context\": {\n\"id\": \"http://www.myboe.org/go/resource/7238\",\n\"description\": \"Brokers of Expertise resource management page\",\n\"objectType\": \"LMS\"\n}\n},\n\"object\": {\n\"id\": \"http://www.readwritethink.org/lessons/lesson_view.asp?id=131\"\n},\n\"related\": [\n{\n\"objectType\": \"academic standard\",\n\"id\": \"http://purl.com/NOTASN/resources/S101282A\",\n\"content\": \"Select a focus when writing.\"\n}\n],\n\"content\": \"A resource found at http://www.myboe.org/go/resource/7238 was matched to the academic content standard with ID http://purl.com/NOTASN/resources/S101282A by an educator of multiple grades and English Language Arts on November 7, 2011\"\n}\n}");
    	
    	JSONObject json = new JSONObject();
    	
    	final String[] patterns = {
    		"https?://purl.org/ASN/.*"
    	};
    	
    	json.put("resource_data", alignment_paradata);
    	String[] results =  Utils.findPatternInJSON(json, patterns);
    	assertNotNull("results should not be null", results);
    	assertEquals("there should be only 1 result", 1, results.length);
    	assertEquals("url should match", "http://purl.org/ASN/resources/S101282A", results[0]);
    	

    	json.put("resource_data", alignment_paradata.toString());
    	results =  Utils.findPatternInJSON(json, patterns);
    	assertNotNull("results should not be null", results);
    	assertEquals("there should be only 1 result", 1, results.length);
    	assertEquals("url should match", "http://purl.org/ASN/resources/S101282A", results[0]);
    	

    	json.put("resource_data", non_align_paradata);
    	results =  Utils.findPatternInJSON(json, patterns);
    	assertNull("results should be null", results);
    	

    	json.put("resource_data", non_align_paradata.toString());
    	results =  Utils.findPatternInJSON(json, patterns);
    	assertNull("results should be null", results);

    	
    }

    
}
