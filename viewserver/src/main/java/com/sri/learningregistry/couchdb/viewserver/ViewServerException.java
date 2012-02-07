package com.sri.learningregistry.couchdb.viewserver;

import org.json.JSONException;
import org.json.JSONObject;

public class ViewServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2090424719716151196L;

	private final String code;
	
	public String getCode() {
		return code;
	}

	public ViewServerException(String message) {
		super(message);
		code = "err";
	}
	
	public ViewServerException(String code, String message){
		super(message);
		this.code = code;
	}
	
	public String toJSON() {
		JSONObject errObj = new JSONObject();
		try {
			try {
				errObj.put("error", this.getCode() != null ? this.getCode() : JSONObject.NULL);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				errObj.put("error", "unknown");
			}
			try {
				errObj.put("reason", this.getMessage() != null ? this.getMessage() : JSONObject.NULL);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				errObj.put("reason", "unknown");
			}
		} catch (JSONException e) {
			ViewServer.log(e.getMessage());
		}
		String msg = errObj.toString();
		return msg;
	}
}
