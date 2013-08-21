package com.cannontech.messaging.serialization;

public class SerializationResult {

    private byte[] messagePayload;
    private Object messageObject;
    private String messageType;
    private Class<?> messageClass;
    private boolean valid = false;
    private SerializationException exception;    

    public byte[] getMessagePayload() {
        return messagePayload;
    }

    void setMessagePayload(byte[] messagePayload) {
        this.messagePayload = messagePayload;
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

    public Object getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Object messageObject) {
        this.messageObject = messageObject;        
    }

    public Class<?> getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(Class<?> messageClass) {
        this.messageClass = messageClass;
    }
}
