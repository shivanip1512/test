package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlCommand;
import com.cannontech.message.capcontrol.model.ItemCommand;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCCommand;
import com.cannontech.messaging.serialization.thrift.generated.CCItemCommand;

public class ItemCommandSerializer extends
    ThriftInheritanceSerializer<ItemCommand, CapControlCommand, CCItemCommand, CCCommand> {

    public ItemCommandSerializer(String messageType, CommandSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ItemCommand> getTargetMessageClass() {
        return ItemCommand.class;
    }

    @Override
    protected CCItemCommand createThriftEntityInstance(CCCommand entityParent) {
        CCItemCommand entity = new CCItemCommand();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCCommand getThriftEntityParent(CCItemCommand entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ItemCommand createMessageInstance() {
        return new ItemCommand();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCItemCommand entity, ItemCommand msg) {
        msg.setItemId(entity.get_itemId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ItemCommand msg, CCItemCommand entity) {
        entity.set_itemId(msg.getItemId());
    }
}
