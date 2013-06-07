package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.messaging.message.loadcontrol.CommandMessage;
import com.cannontech.messaging.message.loadcontrol.LmMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMCommand;
import com.cannontech.messaging.serialization.thrift.generated.LMMessage;

public class CommandSerializer extends ThriftInheritanceSerializer<CommandMessage, LmMessage, LMCommand, LMMessage> {

    public CommandSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<CommandMessage> getTargetMessageClass() {
        return CommandMessage.class;
    }

    @Override
    protected LMCommand createThrifEntityInstance(LMMessage entityParent) {
        LMCommand entity = new LMCommand();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMMessage getThriftEntityParent(LMCommand entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected CommandMessage createMessageInstance() {
        return new CommandMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMCommand entity, CommandMessage msg) {
        msg.setCommand(entity.get_command());
        msg.setYukonId(entity.get_paoId());
        msg.setNumber(entity.get_number());
        msg.setValue(entity.get_value());
        msg.setCount(entity.get_count());
        msg.setAuxid(entity.get_auxId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, CommandMessage msg, LMCommand entity) {
        entity.set_command(msg.getCommand());
        entity.set_paoId(msg.getNumber());
        entity.set_number(msg.getNumber());
        entity.set_value(msg.getValue());
        entity.set_count(msg.getCount());
        entity.set_auxId(msg.getAuxid());
    }
}
