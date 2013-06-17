package com.cannontech.message.capcontrol.model;


public class CapControlServerResponse extends CapControlMessage implements CommandResultParticipant {
	
	private int messageId; 
	
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

    @Override
    public int getMessageId() {
        return messageId;
    }

    @Override
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public boolean isSuccess() {
        return responseType == CapControlResponseType.SUCCESS;
    }
    
    public boolean isTimeout() {
        return responseType == CapControlResponseType.TIMEOUT;
    }

}