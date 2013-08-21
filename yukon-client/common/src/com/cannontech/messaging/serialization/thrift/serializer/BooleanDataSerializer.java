package com.cannontech.messaging.serialization.thrift.serializer;

import com.cannontech.message.util.CollectableBoolean;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.Bool;

public class BooleanDataSerializer extends ThriftSerializer<CollectableBoolean, Bool> {

    protected BooleanDataSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<CollectableBoolean> getTargetMessageClass() {
        return CollectableBoolean.class;
    }

    @Override
    protected CollectableBoolean createMessageInstance() {
        return new CollectableBoolean();
    }

    @Override
    protected Bool createThriftEntityInstance() {
        return new Bool();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Bool entity, CollectableBoolean msg) {
        msg.setValue(entity.is_value());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CollectableBoolean msg, Bool entity) {
        entity.set_value(msg.getValue());
    }
}
