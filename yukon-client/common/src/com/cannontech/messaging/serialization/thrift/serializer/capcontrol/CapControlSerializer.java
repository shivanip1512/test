package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlMessage;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class CapControlSerializer
    extends
    ThriftInheritanceSerializer<CapControlMessage, Message, CCMessage, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public CapControlSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CapControlMessage> getTargetMessageClass() {
        return CapControlMessage.class;
    }

    @Override
    protected CCMessage
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        CCMessage entity = new CCMessage();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(CCMessage entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CapControlMessage createMessageInstance() {
        return new CapControlMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCMessage entity,
                                                   CapControlMessage msg) {
        // Nothing to do here
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CapControlMessage msg,
                                                   CCMessage entity) {
        // Nothing to do here
    }
}
