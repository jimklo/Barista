package com.sri.learningregistry.couchdb.views;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.learningregistry.stdsalign.Utils;

import com.sri.learningregistry.couchdb.viewserver.interfaces.MapDoc;
import com.sri.learningregistry.couchdb.viewserver.views.BaseView;



public class StandardsAlignmentView extends BaseView implements MapDoc {
	
	private static final String[] asnPatterns = new String[] {
		"https?://asn.jesandco.org/.*",
		"https?://purl.org/ASN/.*"
	};
	
	public JSONArray map_doc(JSONObject doc) {
		JSONArray arr = new JSONArray();
		
		try {
			if (doc != null && doc.has("resource_locator") && doc.has("doc_type") && doc.getString("doc_type").equals("resource_data")) {
				
				String[] urls = Utils.getDCTConformsToPatterns(doc, asnPatterns);
				if (urls != null) {
					for (String asn:urls) {
						if (!asn.isEmpty())
							arr.put(emit(doc.getString("resource_locator"), asn));
					}
				}
			}
		} catch (JSONException e) {

		}
		
		return arr;
		
	}
}
