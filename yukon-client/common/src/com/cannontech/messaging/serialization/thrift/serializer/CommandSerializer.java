package com.cannontech.messaging.serialization.thrift.serializer;

import com.cannontech.messaging.message.CommandMessage;
import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Command;
import com.cannontech.messaging.serialization.thrift.generated.Message;

public class CommandSerializer extends ThriftInheritanceSerializer<CommandMessage, BaseMessage, Command, Message> {

    public CommandSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CommandMessage> getTargetMessageClass() {
        return CommandMessage.class;
    }

    @Override
    public CommandMessage createMessageInstance() {
        return new CommandMessage();
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CommandMessage msg, Command entity) {
        entity.set_opArgList(msg.getOpArgList());
        entity.set_operation(msg.getOperation());
        entity.set_opString(msg.getOpString());
        entity.set_opArgCount(msg.getOpArgList().size()); // not used
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, Command entity, CommandMessage msg) {
        msg.setOpArgList(entity.get_opArgList());
        msg.setOperation(entity.get_operation());
        msg.setOpString(entity.get_opString());
    }

    @Override
    protected Message getThriftEntityParent(Command entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected Command createThrifEntityInstance(Message entityParent) {
        Command entity = new Command();
        entity.set_baseMessage(entityParent);
        return entity;
    }
}
