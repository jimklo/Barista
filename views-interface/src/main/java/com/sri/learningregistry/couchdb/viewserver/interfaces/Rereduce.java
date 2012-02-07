package com.sri.learningregistry.couchdb.viewserver.interfaces;

import org.json.JSONArray;

public interface Rereduce {
	/***
	 * rereduce
	 * 
	 * When building a view, CouchDB will apply the reduce step directly to the output of the map step and the rereduce step to the output of a previous reduce step.
	 *
	 * CouchDB will send a list of values, with no keys or document ids, to the rereduce step.
	 * 
	 * CouchDB sends:
	 * 
	 * 		["rereduce",["function(k, v, r) { return sum(v); }"],[33,55,66]]
	 * 
	 * The view-server answers:
	 * 		
	 *  	[true, [154]]
	 *  
	 * 
	 * @param values an array of values that must be reduced to a single value for each rereduction function provided
	 * @return
	 */
	public Object rereduce(JSONArray values);
	
}
