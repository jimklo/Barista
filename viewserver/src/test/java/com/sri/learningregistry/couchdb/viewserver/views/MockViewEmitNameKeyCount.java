package com.sri.learningregistry.couchdb.viewserver.views;

import java.text.MessageFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sri.learningregistry.couchdb.viewserver.interfaces.MapDoc;

public class MockViewEmitNameKeyCount extends BaseView implements MapDoc {

	public JSONArray map_doc(JSONObject obj) {
		JSONArray values = new JSONArray();
		
		try {
			values.put(emit(obj.getString("name"), new JSONObject(MessageFormat.format("'{'\"count\":{0}'}'", obj.length()))));
		} catch (Exception e) {
			log(e.getMessage());
		}
		
		return values;
	}

}
