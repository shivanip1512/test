package com.cannontech.messaging.serialization.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TMemoryBuffer;

import com.cannontech.messaging.serialization.SerializationException;

public abstract class SimpleThriftSerializer {

    /**
     * @param <T> Type to serialize
     */
    public <T extends TBase<T, ?>> byte[] serialize(T msg) {
        TMemoryBuffer transport = ThriftMessageFactory.createDefaultMemoryBufferTransport();
        TProtocol protocol = ThriftMessageFactory.createDefaultProtocol(transport);
        byte[] result = null;

        try {
            msg.write(protocol);
            result = new byte[transport.length()];
            transport.readAll(result, 0, result.length);
        }
        catch (TException e) {
            throw new SerializationException(e);
        }
        return result;
    }

    public <T extends TBase<T, ?>> void deserialize(byte[] data, T msg) {
        TProtocol prot = ThriftMessageFactory.createDefaultProtocol(data);

        try {
            msg.read(prot);
        }
        catch (TException e) {
            throw new SerializationException("Error while deserializing message for '" + msg.getClass().getTypeName() + "'");
        }
    }
}
