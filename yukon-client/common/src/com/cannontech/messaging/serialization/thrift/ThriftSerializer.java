package com.cannontech.messaging.serialization.thrift;

import org.apache.commons.lang.SerializationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TMemoryBuffer;

import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.serialization.MessageSerializer;

/**
 * @param <T> Type to serialize
 * @param <TE> Thrift Entity type of the generated file corresponding to type <T>
 */
public abstract class ThriftSerializer<T, TE extends TBase<TE, ?>> implements MessageSerializer<T>,
    ThriftTypeConverter<T, TE> {

    private String messageTypePrefix;
    private String messageType;

    protected ThriftSerializer(String messageType) {
        this.messageType = messageType;
    }

    @Override
    public final String getMessageType() {
        return getMessageTypePrefix() + messageType;
    }

    public String getMessageTypePrefix() {
        if (messageTypePrefix == null) {
            return "";
        }
        return messageTypePrefix;
    }

    public void setMessageTypePrefix(String prefix) {
        messageTypePrefix = prefix;
    }

    protected abstract T createMessageInstance();

    protected abstract TE createThrifEntityInstance();

    @Override
    public TE convertToThriftEntity(ThriftMessageFactory factory, T msg) {
        TE entity = createThrifEntityInstance();

        populateThriftEntityFromMessage(factory, msg, entity);

        return entity;
    }

    @Override
    public T convertToMessage(ThriftMessageFactory factory, TE entity) {
        T msg = createMessageInstance();

        populateMessageFromThriftEntity(factory, entity, msg);

        return msg;
    }

    protected abstract void populateMessageFromThriftEntity(ThriftMessageFactory factory, TE entity, T msg);

    protected abstract void populateThriftEntityFromMessage(ThriftMessageFactory factory, T msg, TE entity);

    @Override
    public byte[] serialize(MessageFactory factory, T msg) {
        TMemoryBuffer transport = ThriftMessageFactory.createDefaultMemoryBufferTransport();
        TProtocol protocol = ThriftMessageFactory.createDefaultProtocol(transport);
        byte[] result = null;

        TE entity = convertToThriftEntity((ThriftMessageFactory) factory, msg);

        try {
            entity.write(protocol);
            result = new byte[transport.length()];
            transport.readAll(result, 0, result.length);
        }
        catch (TException e) {
            // TODO log it
            // TODO should we throw?
            result = null;
        }
        return result;
    }

    @Override
    public T deserialize(MessageFactory factory, byte[] data) {
        TProtocol prot = ThriftMessageFactory.createDefaultProtocol(data);

        try {
            TE entity = createThrifEntityInstance();
            entity.read(prot);
            return convertToMessage((ThriftMessageFactory) factory, entity);
        }
        catch (TException e) {
            throw new SerializationException("Error while deserializing '" + messageType + "' data");
        }
    }
}
