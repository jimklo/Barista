package com.sri.learningregistry.couchdb.views;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sri.learningregistry.couchdb.viewserver.interfaces.MapDoc;
import com.sri.learningregistry.couchdb.viewserver.views.BaseView;



public class SampleView extends BaseView implements MapDoc {

	public JSONArray map_doc(JSONObject obj) {
		JSONArray arr = new JSONArray();
		
		try {
			if (obj != null && obj.has("resource_locator") && obj.has("doc_type") && obj.getString("doc_type").equals("resource_data")) {
				arr.put(emit(JSONObject.NULL, obj.getString("resource_locator")));
			}
		} catch (JSONException e) {
			log("In SampleView: "+e.getMessage());
		}
		
		return arr;
		
	}
}
