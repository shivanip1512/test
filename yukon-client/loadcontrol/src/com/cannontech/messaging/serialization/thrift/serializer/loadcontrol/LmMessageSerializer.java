package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.loadcontrol.LmMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMMessage;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class LmMessageSerializer extends ThriftInheritanceSerializer<LmMessage, BaseMessage, LMMessage, Message> {

    public LmMessageSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LmMessage> getTargetMessageClass() {
        return LmMessage.class;
    }

    @Override
    protected LMMessage createThrifEntityInstance(Message entityParent) {
        LMMessage entity = new LMMessage();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(LMMessage entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LmMessage createMessageInstance() {
        return new LmMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMMessage entity, LmMessage msg) {
        msg.setMessage(entity.get_message());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LmMessage msg, LMMessage entity) {
        entity.set_message(msg.getMessage());
    }
}
