package com.sri.learningregistry.couchdb.viewserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sri.learningregistry.couchdb.viewserver.interfaces.MapDoc;
import com.sri.learningregistry.couchdb.viewserver.interfaces.Reduce;


public class ViewServer {

	private static ArrayList<MapDoc> map_funcs = new ArrayList<MapDoc>();
	
	public static void log(String message) {
		JSONArray array = new JSONArray();
		array.put("log");
		array.put(message);
		System.out.println(array.toString());
		System.out.flush();
	}
	
	private static void error_add_fun(String errorCode, String errorMessage) throws ViewServerException{
		throw new ViewServerException(errorCode, errorMessage);
	}
	
	private void validate(URL jar_url) throws ViewServerException {
		String jar_query = jar_url.getQuery();
		if (jar_query == null || !jar_query.matches("(^|\\?|&)rev=([^&]+)")) {
			ViewServer.error_add_fun("bad_rev", "rev parameter is required for jar and classpath URL");
		}
	}
	
	private URL getJarURL(String jar_doc, String rev) throws MalformedURLException{
		return new URL(jar_doc + "?rev=" + rev);
	}
	
	private Object getInstanceFromConfig(final String config) throws ViewServerException {
		Object instance = null;
		
		try {
			JSONObject obj = new JSONObject(config);
			URL jar_url;
			ArrayList<URL> clspath = new ArrayList<URL>();
			ClassLoader loader = null;
			if (!(obj.has("builtin") && obj.getBoolean("builtin"))) {
				String rev = null;
				if (obj.has("rev")) {
					rev = obj.getString("rev");				
				} 
				
				if (rev == null || rev.isEmpty()) {
					ViewServer.error_add_fun("bad_rev", "rev is required.");
				}
				
				if (obj.has("classpath")) {
					JSONArray obj_cp = obj.getJSONArray("classpath");
					for(int idx=0; idx<obj_cp.length(); idx++) {
						jar_url = this.getJarURL(obj_cp.getString(idx), rev);
						this.validate(jar_url);
						clspath.add(jar_url);
					}
					
				}
				
				if (clspath.size() == 0) {
					ViewServer.error_add_fun("bad_class", "You must specify the jar or classpath params with at least 1 jar URL with rev.");
				} else {
					loader = new URLClassLoader(clspath.toArray(new URL[]{}));
				}
			} else {
				log("WARNING: USING BUILTIN MAP FUNCTION - ONLY USE FOR TESTING!!!!");
				loader = this.getClass().getClassLoader();
			}
			
			String classname = obj.getString("classname");
			final Class<?> clazz = loader.loadClass(classname);
			Constructor<?> ct0 = clazz.getConstructor();
			instance = ct0.newInstance();
			if (instance != null) {
				log("Loaded class: " + instance.getClass().getName());
			} else {
				log("Unable to load class: "+ classname);
			}
		} catch (MalformedURLException e) {
			ViewServer.error_add_fun("bad_url", e.getClass().getName() +": "+ e.getMessage());
		} catch (SecurityException e) {
			ViewServer.error_add_fun("bad_class", e.getClass().getName() +": "+ e.getMessage());
		} catch (IllegalArgumentException e) {
			ViewServer.error_add_fun("bad_class", e.getClass().getName() +": "+ e.getMessage());
		} catch (JSONException e) {
			ViewServer.error_add_fun("bad_json", e.getClass().getName() +": "+ e.getMessage());
		}  catch (ClassNotFoundException e) {
			ViewServer.error_add_fun("bad_class", e.getClass().getName() +": "+ e.getMessage());
		} catch (NoSuchMethodException e) {
			ViewServer.error_add_fun("bad_class", e.getClass().getName() +": "+ e.getMessage());
		} catch (InstantiationException e) {
			ViewServer.error_add_fun("bad_class", e.getClass().getName() +": "+ e.getMessage());
		} catch (IllegalAccessException e) {
			ViewServer.error_add_fun("bad_class", e.getClass().getName() +": "+ e.getMessage());
		} catch (InvocationTargetException e) {
			ViewServer.error_add_fun("bad_class", e.getClass().getName() +": "+ e.getMessage());
		}
		return instance;
	}
	
	private String add_fun(final String config) throws ViewServerException {
	
		Object instance = getInstanceFromConfig(config);
		if (instance != null && instance instanceof MapDoc) {
			ViewServer.map_funcs.add((MapDoc)instance);
			log("Loaded dynamically created map class: " + instance.getClass().getName());
			return "true";
		}
		else {
			ViewServer.error_add_fun("bad_class", "Unable to load map class.");
		}
		// should never get here
		return "false";

	}
	
	public String reset() {
		ViewServer.map_funcs.clear();
		return "true";
	}
	
	public String map_doc(JSONObject doc) {
		JSONArray response = new JSONArray();
		for (MapDoc obj : ViewServer.map_funcs) {
			try {
				JSONArray mappings = obj.map_doc(doc);
				if (mappings == null) {
					mappings = new JSONArray();
				}
				response.put(mappings); 
			} catch (Exception e) {
				ViewServer.log(e.getMessage());
			}
			
		}
		return response.toString();
	}
	
	public String reduce(JSONArray funcs, JSONArray mapresults, boolean rereduce) {

		try {
			JSONArray result = new JSONArray();
			result.put(true);
			
			JSONArray reduction = new JSONArray();
			result.put(reduction);
			for (int i=0; i<funcs.length(); i++) {
				Object inst = getInstanceFromConfig(funcs.getString(i));
				if (inst instanceof Reduce) {
					reduction.put(((Reduce) inst).reduce(mapresults, rereduce));
				}
			}
			return result.toString();
		} catch (ViewServerException e) {

		} catch (JSONException e) {

		} 
		
		// should never get here except during error.
		return "[false, []]";
		
	}
	
	
	private static final ViewServer vs = new ViewServer();
	
	public static String process(String line) {
		String result = null;
		try {
			System.err.print(line + "\n");
			final JSONArray arr = new JSONArray(line);
			final Command command = Command.valueOf(arr.getString(0));
			
			switch(command) {
			case add_fun:
				result = vs.add_fun(arr.getString(1));
				break;
			case map_doc:
				result = vs.map_doc(arr.getJSONObject(1));
				break;
			case reduce:
				result = vs.reduce(arr.getJSONArray(1), arr.getJSONArray(2), false);
				break;
			case rereduce:
				result = vs.reduce(arr.getJSONArray(1), arr.getJSONArray(2), true);
				break;
			case reset:
				result = vs.reset();
				break;
			}
		} catch (ViewServerException e) {
			ViewServer.log(e.getMessage());
			result =  e.toJSON();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			ViewServer.log(e.getMessage());
		} 
		return result;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HeartbeatLogger beat = new HeartbeatLogger();
		
		try {
			
			long avg_time = -1, start, dur, max = 0, count=0;
			
			beat.start();
			
			final BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in, "UTF-8"));
			String line;
			ViewServer.log("jCouchView started");
			while ((line = reader.readLine()) != null) {
				try {
					if (count < Long.MAX_VALUE) {
						count++;
					} else {
						count = 1;
					}
					start = System.currentTimeMillis();
					
					
					String result = ViewServer.process(line);
					System.out.println(result);
					System.out.flush();
					
					
					dur = System.currentTimeMillis() - start;
					if (avg_time == -1) {
						avg_time = dur;
					} else {
						avg_time = ((avg_time * (count-1)) + dur)/count;
					}
					if (dur > max)
						max = dur;
					if (count % 10 == 0) {
						beat.addMessage(MessageFormat.format("Count: {3}  Last: {0} ms   Avg: {1} ms   Max: {2} ms", dur, avg_time, max, count));
					}
					
				} catch (Exception e) {
					ViewServer.log(e.getMessage());
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			beat.setDone();
		}

	}

}
