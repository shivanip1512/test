package com.cannontech.messaging.serialization.thrift;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import com.cannontech.messaging.serialization.MessageFactory;
import com.cannontech.messaging.serialization.MessageSerializer;
import com.cannontech.messaging.serialization.SerializationException;

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

    protected abstract TE createThriftEntityInstance();

    @Override
    public TE convertToThriftEntity(ThriftMessageFactory factory, T msg) {
        TE entity = createThriftEntityInstance();

        convertToThriftEntity(factory, msg, entity);

        return entity;
    }

    protected void convertToThriftEntity(ThriftMessageFactory factory, T msg, TE entity) {
        populateThriftEntityFromMessage(factory, msg, entity);
    }

    @Override
    public T convertToMessage(ThriftMessageFactory factory, TE entity) {
        T msg = createMessageInstance();

        convertToMessage(factory, entity, msg);

        return msg;
    }

    protected void convertToMessage(ThriftMessageFactory factory, TE entity, T msg) {
        populateMessageFromThriftEntity(factory, entity, msg);
    }

    protected abstract void populateMessageFromThriftEntity(ThriftMessageFactory factory, TE entity, T msg);

    protected abstract void populateThriftEntityFromMessage(ThriftMessageFactory factory, T msg, TE entity);

    @Override
    public byte[] serialize(MessageFactory factory, T msg) {
        YukonTMemoryBuffer transport = ThriftMessageFactory.createDefaultMemoryBufferTransport();
        TProtocol protocol = ThriftMessageFactory.createDefaultProtocol(transport);
        byte[] result = null;

        TE entity = convertToThriftEntity((ThriftMessageFactory) factory, msg);

        try {
            entity.write(protocol);
            result = new byte[transport.length()];
            transport.readAll(result, 0, result.length);
        }
        catch (TException e) {
            throw new SerializationException(e);
        }
        return result;
    }

    @Override
    public T deserialize(MessageFactory factory, byte[] data) {
        TProtocol prot = ThriftMessageFactory.createDefaultProtocol(data);

        try {
            TE entity = createThriftEntityInstance();
            entity.read(prot);
            return convertToMessage((ThriftMessageFactory) factory, entity);
        }
        catch (TException e) {
            throw new SerializationException("Error while deserializing '" + messageType + "' data");
        }
    }
}
