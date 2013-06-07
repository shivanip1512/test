package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.capcontrol.CapControlMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class CapControlSerializer extends
    ThriftInheritanceSerializer<CapControlMessage, BaseMessage, CCMessage, Message> {

    public CapControlSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CapControlMessage> getTargetMessageClass() {
        return CapControlMessage.class;
    }

    @Override
    protected CCMessage createThrifEntityInstance(Message entityParent) {
        CCMessage entity = new CCMessage();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(CCMessage entity) {
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
