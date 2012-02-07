package com.sri.learningregistry.couchdb.viewserver.views;

import org.json.JSONArray;

public class BaseView {
	public JSONArray emit(Object key, Object value) {
		JSONArray arr = new JSONArray();
		arr.put(key);
		arr.put(value);
		return arr;
	}
	
	public void log(String message) {
		JSONArray array = new JSONArray();
		array.put("log");
		array.put(message);
		System.out.println(array.toString());
		System.out.flush();
	}
}
