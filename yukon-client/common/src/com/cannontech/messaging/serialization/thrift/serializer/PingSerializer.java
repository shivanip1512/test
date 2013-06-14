package com.cannontech.messaging.serialization.thrift.serializer;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.PingMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;

public class PingSerializer extends ThriftInheritanceSerializer<PingMessage, BaseMessage, Message, Message> {

    public PingSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<PingMessage> getTargetMessageClass() {
        return PingMessage.class;
    }

    @Override
    protected Message createThrifEntityInstance(Message entityParent) {
        return entityParent;
    }

    @Override
    protected Message getThriftEntityParent(Message entity) {
        return entity;
    }

    @Override
    protected PingMessage createMessageInstance() {
        return new PingMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Message entity, PingMessage msg) {
        // Nothing to do here as Ping message is empty        
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, PingMessage msg, Message entity) {
        // Nothing to do here as Ping message is empty        
    }
}
