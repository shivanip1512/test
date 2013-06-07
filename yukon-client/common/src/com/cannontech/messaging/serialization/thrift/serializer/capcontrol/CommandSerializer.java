package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.capcontrol.CommandMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCommand;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class CommandSerializer extends ThriftInheritanceSerializer<CommandMessage, BaseMessage, CCCommand, Message> {

    public CommandSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CommandMessage> getTargetMessageClass() {
        return CommandMessage.class;
    }

    @Override
    protected CCCommand createThrifEntityInstance(Message entityParent) {
        CCCommand entity = new CCCommand();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(CCCommand entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CommandMessage createMessageInstance() {
        return new CommandMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCCommand entity, CommandMessage msg) {
        msg.setMessageId(entity.get_messageId());
        msg.setCommandId(entity.get_commandId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CommandMessage msg, CCCommand entity) {
        entity.set_messageId(msg.getMessageId());
        entity.set_commandId(msg.getCommandId());
    }
}
