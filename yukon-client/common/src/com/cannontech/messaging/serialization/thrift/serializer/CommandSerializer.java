package com.cannontech.messaging.serialization.thrift.serializer;

import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

public class CommandSerializer
    extends
    ThriftInheritanceSerializer<Command, Message, com.cannontech.messaging.serialization.thrift.generated.Command, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public CommandSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<Command> getTargetMessageClass() {
        return Command.class;
    }

    @Override
    public Command createMessageInstance() {
        return new Command();
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, Command msg,
                                        com.cannontech.messaging.serialization.thrift.generated.Command entity) {
        entity.set_opArgList(msg.getOpArgList());
        entity.set_operation(msg.getOperation());
        entity.set_opString(msg.getOpString());
        entity.set_opArgCount(msg.getOpArgList().size()); // not used
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.Command entity,
                                        Command msg) {
        msg.setOpArgList(entity.get_opArgList());
        msg.setOperation(entity.get_operation());
        msg.setOpString(entity.get_opString());
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.Command entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Command
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.Command entity =
            new com.cannontech.messaging.serialization.thrift.generated.Command();
        entity.set_baseMessage(entityParent);
        return entity;
    }
}
