package com.sri.learningregistry.couchdb.views;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sri.learningregistry.couchdb.viewserver.interfaces.MapDoc;

import junit.framework.TestCase;

public class StandardsAlignmentViewTest extends TestCase {

	
	public void testStandardsAlignmentView() throws JSONException {
		final String json = "'{'\"resource_data\":\"{0}\", \"resource_locator\":\"{1}\", \"doc_type\":\"resource_data\"'}'";
    	
    	String payload = "<nsdl_dc:nsdl_dc xmlns:nsdl_dc=\\\"http://ns.nsdl.org/nsdl_dc_v1.02/\\\" xmlns:dc=\\\"http://purl.org/dc/elements/1.1/\\\" xmlns:dct=\\\"http://purl.org/dc/terms/\\\" xmlns:xsi=\\\"http://www.w3.org/2001/XMLSchema-instance\\\" xmlns=\\\"http://www.openarchives.org/OAI/2.0/\\\" schemaVersion=\\\"1.02.000\\\" xsi:schemaLocation=\\\"http://ns.nsdl.org/nsdl_dc_v1.02/ http://ns.nsdl.org/schemas/nsdl_dc/nsdl_dc_v1.02.xsd\\\">\\n  <dc:identifier xsi:type=\\\"dct:URI\\\">http://www.nationalgeographic.com/xpeditions/lessons/15/gk2/tornadowhat.html</dc:identifier>\\n  <dct:isPartOf xsi:type=\\\"dct:URI\\\">http://www.nationalgeographic.com/xpeditions/index.html</dct:isPartOf>\\n  <dct:conformsTo xsi:type=\\\"dct:URI\\\">http://purl.org/ASN/resources/S103E245</dct:conformsTo>\\n  <dct:conformsTo xsi:type=\\\"dct:URI\\\">http://purl.org/ASN/resources/S103E24D</dct:conformsTo>\\n  <dc:type xsi:type=\\\"dct:DCMIType\\\">InteractiveResource</dc:type>\\n  <dc:type xsi:type=\\\"nsdl_dc:NSDLType\\\">Instructional Material</dc:type>\\n  <dc:type xsi:type=\\\"nsdl_dc:NSDLType\\\">Lesson/Lesson Plan</dc:type>\\n  <dc:format xsi:type=\\\"dct:IMT\\\">text/html</dc:format>\\n  <dc:language xsi:type=\\\"dct:RFC3066\\\">en</dc:language>\\n  <dc:title>What is a Tornado?</dc:title>\\n  <dc:subject>Atmospheric science</dc:subject>\\n  <dc:subject>Natural hazards</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Science</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Earth science</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Physical sciences</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Meteorology</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Astronomy</dc:subject>\\n  <dc:subject xsi:type=\\\"nsdl_dc:GEM\\\">Space sciences</dc:subject>\\n  <dc:description>In this lesson plan students will learn the basics about how tornadoes are formed and when and where they are most likely to occur. They will learn that the United States is the country most vulnerable to tornadoes and why most tornadoes occur in Tornado Alley. Students will create a tornado in a bottle. Finally, they will draw pictures of tornadoes of different shapes and sizes and review basic safety information about what to do in case of a tornado.</dc:description>\\n  <dc:publisher>National Geographic Society</dc:publisher>\\n  <dc:rights>Copyright 2003 National Geographic Society. All rights reserved.</dc:rights>\\n  <dct:educationLevel xsi:type=\\\"nsdl_dc:NSDLEdLevel\\\">Elementary School</dct:educationLevel>\\n</nsdl_dc:nsdl_dc>";
    	String locator = "http://www.example.com/";
    	
    	List<String> asns = new ArrayList<String>();    	
    	asns.add("http://purl.org/ASN/resources/S103E245");
    	asns.add("http://purl.org/ASN/resources/S103E24D");

 
    	String resource_data = MessageFormat.format(json, payload, locator);
    	
    	MapDoc map = new StandardsAlignmentView();
    	JSONObject doc = new JSONObject(resource_data);
    	JSONArray res = map.map_doc(doc);
    	
    	assertNotNull("map_doc result must never be null", res);
    	assertEquals("expected 2 results",2, res.length());
    	
    	assertEquals("resource_locator does not match", locator, res.getJSONArray(0).getString(0));
    	assertTrue("expected asn not returned", asns.contains(res.getJSONArray(0).getString(1)));
    	
    	asns.remove(res.getJSONArray(0).getString(1));
    	
    	assertEquals("resource_locator does not match", locator, res.getJSONArray(1).getString(0));
    	assertTrue("expected asn not returned", asns.contains(res.getJSONArray(1).getString(1)));
    	
	}
}
