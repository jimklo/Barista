package com.sri.learningregistry.couchdb.viewserver.views;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sri.learningregistry.couchdb.viewserver.interfaces.MapDoc;

public class MockView extends BaseView implements MapDoc {

	public JSONArray map_doc(JSONObject obj) {
		JSONArray values = new JSONArray();
		return values;
	}

}
