package com.cannontech.messaging.serialization.thrift;


/**
 * @param <T> Type to serialize
 */
public interface ThriftByteDeserializer<T> {
    public T fromBytes(byte[] msgBytes);
}
