package com.cannontech.messaging.serialization.thrift.serializer.dispatch;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.dispatch.SignalMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.Signal;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class SignalSerializer extends ThriftInheritanceSerializer<SignalMessage, BaseMessage, Signal, Message> {

    public SignalSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<SignalMessage> getTargetMessageClass() {
        return SignalMessage.class;
    }

    @Override
    protected Signal createThrifEntityInstance(Message entityParent) {
        Signal entity = new Signal();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(Signal entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected SignalMessage createMessageInstance() {
        return new SignalMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Signal entity, SignalMessage msg) {
        msg.setAction(entity.get_additionalInfo());
        msg.setCategoryId(entity.get_signalCategory());
        msg.setCondition(entity.get_condition());
        msg.setDescription(entity.get_text());
        msg.setLogType(entity.get_logType());
        msg.setMillis(entity.get_signalMillis());
        msg.setPointId(entity.get_id());
        msg.setTags(entity.get_tags());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, SignalMessage msg, Signal entity) {
        entity.set_additionalInfo(msg.getAction());
        entity.set_condition(msg.getCondition());
        entity.set_id(msg.getPointId());
        entity.set_logType(msg.getLogType());
        entity.set_pointValue(0);

        // TODO long to in conversion. is it expected?
        entity.set_signalCategory((int) msg.getCategoryId());
        entity.set_signalMillis((int) msg.getMillis());
        entity.set_tags(msg.getTags());
        entity.set_text(msg.getDescription());
    }
}
