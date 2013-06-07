package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.CommandMessage;
import com.cannontech.messaging.message.capcontrol.ItemCommandMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCommand;
import com.cannontech.messaging.serialization.thrift.generated.CCItemCommand;

public class ItemCommandSerializer extends
    ThriftInheritanceSerializer<ItemCommandMessage, CommandMessage, CCItemCommand, CCCommand> {

    public ItemCommandSerializer(String messageType, CommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ItemCommandMessage> getTargetMessageClass() {
        return ItemCommandMessage.class;
    }

    @Override
    protected CCItemCommand createThrifEntityInstance(CCCommand entityParent) {
        CCItemCommand entity = new CCItemCommand();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCCommand getThriftEntityParent(CCItemCommand entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ItemCommandMessage createMessageInstance() {
        return new ItemCommandMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCItemCommand entity, ItemCommandMessage msg) {
        msg.setItemId(entity.get_itemId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ItemCommandMessage msg, CCItemCommand entity) {
        entity.set_itemId(msg.getItemId());
    }
}
