package com.cannontech.messaging.serialization.thrift;

/**
 * @param <T> Type to serialize
 */
public interface ThriftByteSerializer<T> {
    public byte[] toBytes(T requestPayload);
}
