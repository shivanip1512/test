package com.cannontech.messaging.serialization.thrift.serializer;

import com.cannontech.messaging.message.BooleanData;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.Bool;

public class BooleanDataSerializer extends ThriftSerializer<BooleanData, Bool> {

    protected BooleanDataSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<BooleanData> getTargetMessageClass() {
        return BooleanData.class;
    }

    @Override
    protected BooleanData createMessageInstance() {
        return new BooleanData();
    }

    @Override
    protected Bool createThrifEntityInstance() {
        return new Bool();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Bool entity, BooleanData msg) {
        msg.setValue(entity.is_value());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, BooleanData msg, Bool entity) {
        entity.set_value(msg.getValue());
    }
}
