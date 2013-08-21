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
    public void convertToThriftEntity(ThriftMessageFactory factory, T msg, TE entity) {
        super.convertToThriftEntity(factory, msg, entity);
        parentSerializer.convertToThriftEntity(factory, msg, getThriftEntityParent(entity));
    }

    @Override
    protected void convertToMessage(ThriftMessageFactory factory, TE entity, T msg) {
        super.convertToMessage(factory, entity, msg);
        parentSerializer.convertToMessage(factory, getThriftEntityParent(entity), msg);
    }

    @Override
    protected final TE createThriftEntityInstance() {
        return createThriftEntityInstance(parentSerializer.createThriftEntityInstance());
    }

    protected abstract TE createThriftEntityInstance(TEP entityParent);

    protected abstract TEP getThriftEntityParent(TE entity);
}
