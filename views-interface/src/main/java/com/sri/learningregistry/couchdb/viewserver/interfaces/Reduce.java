package com.sri.learningregistry.couchdb.viewserver.interfaces;

import org.json.JSONArray;

public interface Reduce {
	/***
	 * 
	 * @param key_id_value an array of [[key, id-of-doc], value]
	 * @return
	 */
	public Object reduce(JSONArray key_id_value, boolean rereduce);
}
