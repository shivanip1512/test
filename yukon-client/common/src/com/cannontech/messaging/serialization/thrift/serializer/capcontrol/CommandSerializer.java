package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCommand;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class CommandSerializer extends ThriftInheritanceSerializer<CapControlCommand, Message, CCCommand, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public CommandSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CapControlCommand> getTargetMessageClass() {
        return CapControlCommand.class;
    }

    @Override
    protected CCCommand createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        CCCommand entity = new CCCommand();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message getThriftEntityParent(CCCommand entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CapControlCommand createMessageInstance() {
        return new CapControlCommand();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCCommand entity, CapControlCommand msg) {
        msg.setMessageId(entity.get_messageId());
        msg.setCommandId(entity.get_commandId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CapControlCommand msg, CCCommand entity) {
        entity.set_messageId(msg.getMessageId());
        entity.set_commandId(msg.getCommandId());
    }
}
