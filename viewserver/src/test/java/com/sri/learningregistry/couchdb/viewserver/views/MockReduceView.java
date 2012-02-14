package com.sri.learningregistry.couchdb.viewserver.views;

import org.json.JSONArray;

import com.sri.learningregistry.couchdb.viewserver.interfaces.Reduce;

public class MockReduceView extends MockView implements Reduce {
	
	
	/***
	 * Performs a simple count
	 * 
	 * @param key_id_value when rereduce is false this is an array of [[key, id-of-doc], value], when rereduce is true this is an array of [value, value, value, ...]
	 * @param rereduce indicates if function should operate as a reduce or rereduce
	 * @return
	 */
	public Object reduce(JSONArray key_id_value, boolean rereduce) {

		if (!rereduce) {
			return key_id_value.length();
		} else {
			long sum = 0;
			for (int i=0; i<key_id_value.length(); i++) {
				try {
					sum += key_id_value.getLong(i);
				} catch (Exception e) {
					log(e.getMessage());
				}
			}
			return sum;
		}
	}

}
