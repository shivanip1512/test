package com.cannontech.messaging.serialization;

public class SerializationResult {

    private byte[] payload;
    private String messageType;
    private boolean valid = false;
    private SerializationException exception;    

    public byte[] getPayload() {
        return payload;
    }

    void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setValid(boolean isResultValid) {
        valid = isResultValid;
    }

    public boolean isValid() {
        return valid;
    }

    public SerializationException getException() {
        return exception;
    }

    public void setException(SerializationException exception) {
        this.exception = exception;
    }
}
