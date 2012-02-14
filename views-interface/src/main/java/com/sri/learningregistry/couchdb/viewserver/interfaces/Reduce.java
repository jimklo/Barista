package com.sri.learningregistry.couchdb.viewserver.interfaces;

import org.json.JSONArray;

public interface Reduce {
	/***
	 * 
	 * @param key_id_value when rereduce is false this is an array of [[key, id-of-doc], value], when rereduce is true this is an array of [value, value, value, ...]
	 * @param rereduce indicates if function should operate as a reduce or rereduce
	 * @return a single JSONObject, JSONArray, String, long or float as a reduced value
	 */
	public Object reduce(JSONArray key_id_value, boolean rereduce);
}
