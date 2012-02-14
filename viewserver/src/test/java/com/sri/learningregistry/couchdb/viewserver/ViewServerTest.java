package com.sri.learningregistry.couchdb.viewserver;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewServerTest extends TestCase {
	
	
	private ByteArrayOutputStream vsOut	= new ByteArrayOutputStream();
	
	
	public ViewServerTest(String testName) {
		super(testName);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		TeeStream ps = new TeeStream(System.out, new PrintStream(vsOut));
		System.setOut(ps);
		
	}
	
	public static Test suite() {
		return new TestSuite(ViewServerTest.class);
	}

	
	public void testReset()
    {
	
		String res = ViewServer.process("[\"reset\"]\n");
					
		assertEquals("true", res);
					        
    }
	
	
	public void testAddFunInvalid()
    {
		String res = ViewServer.process("[\"add_fun\", \"{\\\"classpath\\\":[],\\\"rev\\\":\\\"\\\",\\\"class\\\":\\\"\\\"}\"]\n");
		try {
			JSONObject obj = new JSONObject(res);
			assert(obj.has("error"));
			assertNotNull(obj.getString("error"));
			assert(obj.has("reason"));
			assertNotNull(obj.getString("reason"));
		} catch (JSONException e) {
			assertNull("invalid response, not JSON.", 1);
		}
        
    }
	
	
	public void testAddFunBadRev()
    {
		String res = ViewServer.process("[\"add_fun\", \"{\\\"classpath\\\":[\\\"http://127.0.0.1:5984/java/classpath/views.jar\\\"],\\\"rev\\\":\\\"\\\",\\\"class\\\":\\\"com.sri.learningregistry.couchdb.views.SampleView\\\"}\"]\n");
		try {
			JSONObject obj = new JSONObject(res);
			assert(obj.has("error"));
			assertEquals("bad_rev", obj.getString("error"));
			assert(obj.has("reason"));
			assertNotNull(obj.getString("reason"));
		} catch (JSONException e) {
			assertNull("invalid response, not JSON.", 1);
		}
        
    }
	
	public void testAddFun()
	{
		String res = ViewServer.process("[\"add_fun\", \"{\\\"builtin\\\":true,\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.viewserver.views.MockView\\\"}\"]\n");
	
		assertEquals("true", res);
	}
	
	
	public void testAddFunFive()
	{
		for (int i=0; i<5; i++) {
			String res = ViewServer.process("[\"add_fun\", \"{\\\"builtin\\\":true,\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.viewserver.views.MockView\\\"}\"]\n");
		
			assertEquals("true", res);
		}
	}
	
	public void testMapDoc()
	{
		
		testReset();
		
		testAddFun();
		
		String res = ViewServer.process("[\"map_doc\", {\"_id\":\"mock\",\"_rev\":\"1-deadbeef\"}]\n");
		
		assertEquals("[[]]", res);
		
			
	}
	
	public void testMapDocFive()
	{
		
		testReset();
		
		testAddFunFive();
		
		String res = ViewServer.process("[\"map_doc\", {\"_id\":\"mock\",\"_rev\":\"1-deadbeef\"}]\n");
		
		assertEquals("[[],[],[],[],[]]", res);
		
		
	}
	
	public void testAddFun2()
	{
		String res = ViewServer.process("[\"add_fun\", \"{\\\"builtin\\\":true,\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.viewserver.views.MockViewEmitNameKeyCount\\\"}\"]\n");
	
		assertEquals("true", res);
	}
	
	public void testAddFun2Six()
	{
		for (int i=0; i<3; i++) {
			String res = ViewServer.process("[\"add_fun\", \"{\\\"builtin\\\":true,\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.viewserver.views.MockView\\\"}\"]\n");
			assertEquals("true", res);
			
			res = ViewServer.process("[\"add_fun\", \"{\\\"builtin\\\":true,\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.viewserver.views.MockViewEmitNameKeyCount\\\"}\"]\n");
			assertEquals("true", res);
		}
	}
	
	public void testMapDoc2()
	{
		
		testReset();
		
		testAddFun2();
		
		String res = ViewServer.process("[\"map_doc\", {\"_id\":\"mock\",\"_rev\":\"1-deadbeef\",\"name\":\"qwerty\"}]\n");
		
		assertEquals("[[[\"qwerty\",{\"count\":3}]]]", res);
		
			
	}
	
	public void testMapDoc2Six()
	{
		
		testReset();
		
		testAddFun2Six();
		
		String res = ViewServer.process("[\"map_doc\", {\"_id\":\"mock\",\"_rev\":\"1-deadbeef\",\"name\":\"qwerty\"}]\n");
		
		assertEquals("[[],[[\"qwerty\",{\"count\":3}]],[],[[\"qwerty\",{\"count\":3}]],[],[[\"qwerty\",{\"count\":3}]]]", res);
		
			
	}
	
	public void testReduce() {
		String res = ViewServer.process("[\"reduce\", [\"{\\\"builtin\\\":true,\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.viewserver.views.MockReduceView\\\"}\"], [[[\"key\", \"id\"], \"value\"],[[\"key\", \"id\"], \"value\"],[[\"key\", \"id\"], \"value\"],[[\"key\", \"id\"], \"value\"],[[\"key\", \"id\"], \"value\"]]]\n");

		assertEquals("[true,[5]]",res);
	}
	
	public void testReReduce() {
		String res = ViewServer.process("[\"rereduce\", [\"{\\\"builtin\\\":true,\\\"classname\\\":\\\"com.sri.learningregistry.couchdb.viewserver.views.MockReduceView\\\"}\"], [2,2,2,2,2]]\n");
		
		assertEquals("[true,[10]]",res);
	}

}
