package org.learningregistry.stdsalign;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.learningregistry.stdsalign.ns.DCNamespaceContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Utils {
	
	
	/***
	 * Returns true if doc has resource_data and resource_data is a non-empty String
	 * @param doc
	 * @return
	 */
	public static boolean hasResourceDataAsString(JSONObject doc) {
		boolean result;
		
		try {
			result = doc != null && doc.has("resource_data") && !doc.isNull("resource_data") && doc.get("resource_data") instanceof String && !doc.getString("resource_data").isEmpty();
		} catch (JSONException e) {
			result = false;
		}
		
		return result;
	}
	
	/***
	 * Returns true if doc has resource_data that is a JSON map, false otherwise.
	 * @param doc
	 * @return
	 */
	public static boolean hasResourceDataAsJSONObject(JSONObject doc) {
		boolean result;
		
		try {
			result = doc != null && doc.has("resource_data") && !doc.isNull("resource_data") && doc.get("resource_data") instanceof JSONObject;
		} catch (JSONException e) {
			result = false;
		}
		
		return result;
	}
	
	/***
	 * Returns true if doc has resource_data that is a JSON map encapsulated in a String, false otherwise.
	 * @param doc
	 * @return
	 */
	public static boolean hasResourceDataAsJSONString(JSONObject doc) {
		boolean result;
		
		try {
			if (doc != null && doc.has("resource_data") && !doc.isNull("resource_data") && doc.get("resource_data") instanceof String) {
				new JSONObject(doc.getString("resource_data"));
				result = true;
			} else {
				result = false;
			}
		} catch (JSONException e) {
			result = false;
		}
		
		return result;
	}
	
	/***
	 * Returns the resource data as a JSON object if possible, null otherwise.
	 * @param doc
	 * @return
	 */
	public static JSONObject getResourceDataAsJSON(JSONObject doc) {
		JSONObject result;
		
		try {
			if (hasResourceDataAsJSONObject(doc)) {
				result = doc.getJSONObject("resource_data");
			} else if (hasResourceDataAsJSONString(doc)) {
				result = new JSONObject(doc.getString("resource_data"));
			} else {
				result = null;
			}
		} catch (JSONException e) {
			result = null;
		}
		
		return result;
	}
	
	/***
	 * Returns String[] of text elements within dct:conformsTo elements matching any of the provided regex patterns, null otherwise.
	 * @param doc
	 * @param patterns
	 * @return
	 */
	public static String[] getDCTConformsToPatterns(JSONObject doc, String[] patterns) {
		String[] result = null;
		
		if (hasResourceDataAsString(doc)) {
		
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				
				Document resource_data = builder.parse(new ByteArrayInputStream(doc.getString("resource_data").getBytes("UTF-8")));
				
				XPathFactory xfactory = XPathFactory.newInstance();
				XPath xpath = xfactory.newXPath();
				xpath.setNamespaceContext(new DCNamespaceContext());
				XPathExpression expr = xpath.compile("//dct:conformsTo/text()");
				
				NodeList nodes = (NodeList)expr.evaluate(resource_data, XPathConstants.NODESET);
				List<String> goodStuff = new ArrayList<String>();
				for (int i=0; i<nodes.getLength(); i++) {
					for (String pat : patterns) {
						if (nodes.item(i).getNodeValue().matches(pat)) {
							goodStuff.add(nodes.item(i).getNodeValue());
						}
					}
				}
				
				if (goodStuff.size()>0) {
					result = goodStuff.toArray(new String[goodStuff.size()]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
		
		return result;
	}
	
	/***
	 * Treat obj as JSONObject, JSONArray, or String check to see if contents match any of the supplied pattern list. Returns a list of matching elements.
	 * @param obj
	 * @param patterns
	 * @return
	 * @throws JSONException
	 */
	private static ArrayList<String> findPatternInObject(Object obj, String[] patterns) throws JSONException {
		ArrayList<String> values = new ArrayList<String>();
		
		if (obj instanceof JSONObject) {
			JSONObject json = (JSONObject)obj;
			Iterator keyIter = json.keys();
			while(keyIter.hasNext()) {
				Object anonKey = keyIter.next();
				values.addAll(findPatternInObject(json.get(anonKey.toString()), patterns));
			}
			
		} else if (obj instanceof JSONArray) {
			JSONArray list = (JSONArray)obj;
			for (int i=0; i<list.length(); i++) {
				values.addAll(findPatternInObject(list.get(i), patterns));
			}
			
		} else if (obj instanceof String) {
			String str = (String)obj;
			for (String pattern: patterns) {
				if (str.matches(pattern)) {
					if (!values.contains(str)) {
						values.add(str);
					}
					break;
				}
			}
		}
		
		return values;
	}
	
	
	public static String[] findPatternInJSON(JSONObject doc, String[] patterns) {
		String[] results = null;
	
		JSONObject resource_data = null;
		
		try {
			if (hasResourceDataAsJSONObject(doc)) {
				resource_data = doc.getJSONObject("resource_data");
			} else if (hasResourceDataAsJSONString(doc)) {
				resource_data = new JSONObject(doc.getString("resource_data"));
			}
			
			if (resource_data != null) {
				ArrayList<String> matches = findPatternInObject(resource_data, patterns);
				
				if (matches.size() > 0) {
					results = matches.toArray(new String[matches.size()]);
				}
			}
		} catch (JSONException e) {
			// discard, do nothing
		}
		
		
		return results;
	}
	
	
	
}
