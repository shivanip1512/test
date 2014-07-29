package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class LmMessageSerializer
    extends
    ThriftInheritanceSerializer<LMMessage, Message, com.cannontech.messaging.serialization.thrift.generated.LMMessage, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public LmMessageSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMMessage> getTargetMessageClass() {
        return LMMessage.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMMessage
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMMessage entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMMessage();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMMessage entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMMessage createMessageInstance() {
        return new LMMessage();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMMessage entity,
                                        LMMessage msg) {
        msg.setMessage(entity.get_message());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMMessage msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMMessage entity) {
        entity.set_message(msg.getMessage());
    }
}
