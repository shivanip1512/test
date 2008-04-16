package com.cannontech.message.capcontrol;

import com.cannontech.yukon.cbc.CapControlMessage;

public class CapControlServerResponse extends CapControlMessage{
	
	/* This will needs to match the C++ ID's */
	public static final int COMMANDREFUSED_ID = 1;
	
	private String response;
	private CapControlResponseType responseType;
	
	public CapControlServerResponse() {}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public CapControlResponseType getResponseType() {
		return responseType;
	}

	public void setResponseType(CapControlResponseType responseType) {
		this.responseType = responseType;
	}
	
}
