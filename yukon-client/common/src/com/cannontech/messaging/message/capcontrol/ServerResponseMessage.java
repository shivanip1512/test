package com.cannontech.messaging.message.capcontrol;

public class ServerResponseMessage extends CapControlMessage implements CommandResultParticipant {

    private int messageId;
    private String response;
    private ResponseType responseType;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
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
        return responseType == ResponseType.SUCCESS;
    }

    public boolean isTimeout() {
        return responseType == ResponseType.TIMEOUT;
    }
}
