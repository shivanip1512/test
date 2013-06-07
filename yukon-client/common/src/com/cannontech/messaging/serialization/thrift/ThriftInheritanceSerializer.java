package com.cannontech.messaging.serialization.thrift;

import org.apache.thrift.TBase;

/**
 * @param <T> Type to serialize
 * @param <TE> Thrift Entity type
 * @param <TEP> Thrift Entity Parent type
 */
public abstract class ThriftInheritanceSerializer<T extends TP, TP, TE extends TBase<TE, ?>, TEP extends TBase<TEP, ?>>
    extends ThriftSerializer<T, TE> {

    protected ThriftSerializer<TP, TEP> parentSerializer;

    public ThriftInheritanceSerializer(String messageType, ThriftSerializer<TP, TEP> parentSerializer) {
        super(messageType);
        this.parentSerializer = parentSerializer;
    }

    @Override
    public TE convertToThriftEntity(ThriftMessageFactory factory, T msg) {
        TE entity = createThrifEntityInstance();

        parentSerializer.populateThriftEntityFromMessage(factory, msg, getThriftEntityParent(entity));
        populateThriftEntityFromMessage(factory, msg, entity);

        return entity;
    }

    @Override
    public T convertToMessage(ThriftMessageFactory factory, TE entity) {
        T msg = createMessageInstance();

        parentSerializer.populateMessageFromThriftEntity(factory, getThriftEntityParent(entity), msg);
        populateMessageFromThriftEntity(factory, entity, msg);

        return msg;
    }

    @Override
    protected final TE createThrifEntityInstance() {
        return createThrifEntityInstance(parentSerializer.createThrifEntityInstance());
    }
  
    protected abstract TE createThrifEntityInstance(TEP entityParent);

    protected abstract TEP getThriftEntityParent(TE entity);
}
