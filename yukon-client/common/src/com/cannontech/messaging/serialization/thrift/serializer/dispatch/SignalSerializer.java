package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class SignalSerializer
    extends
    ThriftInheritanceSerializer<Signal, Message, com.cannontech.messaging.serialization.thrift.generated.Signal, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public SignalSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Signal> getTargetMessageClass() {
        return Signal.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Signal
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.Signal entity =
            new com.cannontech.messaging.serialization.thrift.generated.Signal();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.Signal entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected Signal createMessageInstance() {
        return new Signal();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.Signal entity,
                                        Signal msg) {
        msg.setAction(entity.get_additionalInfo());
        msg.setCategoryID(ConverterHelper.IntToUnsigned(entity.get_signalCategory()));
        msg.setCondition(entity.get_condition());
        msg.setDescription(entity.get_text());
        msg.setLogType(entity.get_logType());
        msg.setMillis(entity.get_signalMillis());
        msg.setPointID(entity.get_id());
        msg.setTags(entity.get_tags());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Signal msg,
                                        com.cannontech.messaging.serialization.thrift.generated.Signal entity) {
        entity.set_additionalInfo(msg.getAction());
        entity.set_condition(msg.getCondition());
        entity.set_id(msg.getPointID());
        entity.set_logType(msg.getLogType());
        entity.set_signalCategory(ConverterHelper.UnsignedToInt(msg.getCategoryID()));
        entity.set_signalMillis(ConverterHelper.UnsignedToInt(msg.getMillis()));
        entity.set_tags(msg.getTags());
        entity.set_text(msg.getDescription());

        // For the missing field
        entity.set_pointValue(0);
    }
}
