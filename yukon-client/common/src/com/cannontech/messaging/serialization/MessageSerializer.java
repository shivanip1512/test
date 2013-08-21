package com.cannontech.messaging.serialization;


public interface MessageSerializer<T> {

    public Class<T> getTargetMessageClass();

    public String getMessageType();

    public byte[] serialize(MessageFactory factory, T msg);

    public T deserialize(MessageFactory factory, byte[] data);
}
