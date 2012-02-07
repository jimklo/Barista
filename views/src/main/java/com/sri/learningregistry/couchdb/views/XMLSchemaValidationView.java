package com.sri.learningregistry.couchdb.views;


import org.dlese.dpc.xml.XMLValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sri.learningregistry.couchdb.viewserver.interfaces.MapDoc;
import com.sri.learningregistry.couchdb.viewserver.views.BaseView;

public class XMLSchemaValidationView extends BaseView implements MapDoc {

	public JSONArray map_doc(JSONObject obj) {
		JSONArray result = new JSONArray();
		
		try {
			if (obj != null && obj.has("doc_type") && obj.getString("doc_type").equals("resource_data") && 
					obj.has("payload_placement") && obj.getString("payload_placement").equals("inline")) {
				
				String errors = XMLValidator.validateString(obj.getString("resource_data"));
				
				if (errors == null) {
					result.put(emit(obj.getString("_id"), JSONObject.NULL));
				} else {
					log(errors);
				}
				
			}
		} catch (JSONException e) {
			log("In XMLSchemaValidationView: "+e.getMessage());
		}
		
		
		
		return result;
	}

}
