package com.sri.learningregistry.couchdb.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sri.learningregistry.couchdb.viewserver.interfaces.MapDoc;
import com.sri.learningregistry.couchdb.viewserver.views.BaseView;



public class ResourceDataByDateView extends BaseView implements MapDoc {

	private static Pattern millisPat = Pattern.compile("\\.[0-9]+Z", Pattern.CASE_INSENSITIVE);
	
	
	public JSONArray map_doc(JSONObject obj) {
		JSONArray arr = new JSONArray();
		try {
			if (obj != null && obj.has("doc_type") && obj.getString("doc_type").equals("resource_data") && obj.has("node_timestamp")) {
				String ts = obj.getString("node_timestamp");
				Matcher matcher = millisPat.matcher(ts);
				arr.put(emit(matcher.replaceAll(""), JSONObject.NULL));
			}
		} catch (JSONException e) {
			log("In SampleView: "+e.getMessage());
		}
		
		return arr;
		
	}
}
