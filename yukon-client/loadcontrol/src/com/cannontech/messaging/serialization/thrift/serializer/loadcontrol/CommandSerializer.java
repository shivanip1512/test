package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;



public class CommandSerializer extends ThriftInheritanceSerializer<LMCommand, LMMessage, com.cannontech.messaging.serialization.thrift.generated.LMCommand, com.cannontech.messaging.serialization.thrift.generated.LMMessage> {

    public CommandSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMCommand> getTargetMessageClass() {
        return LMCommand.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMCommand createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMMessage entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMCommand entity = new com.cannontech.messaging.serialization.thrift.generated.LMCommand();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMMessage getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMCommand entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMCommand createMessageInstance() {
        return new LMCommand();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, com.cannontech.messaging.serialization.thrift.generated.LMCommand entity, LMCommand msg) {
        msg.setCommand(entity.get_command());
        msg.setYukonID(entity.get_paoId());
        msg.setNumber(entity.get_number());
        msg.setValue(entity.get_value());
        msg.setCount(entity.get_count());
        msg.setAuxid(entity.get_auxId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMCommand msg, com.cannontech.messaging.serialization.thrift.generated.LMCommand entity) {
        entity.set_command(msg.getCommand());
        entity.set_paoId(msg.getYukonID());
        entity.set_number(msg.getNumber());
        entity.set_value(msg.getValue());
        entity.set_count(msg.getCount());
        entity.set_auxId(msg.getAuxid());
    }
}
