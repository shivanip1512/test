package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.messaging.message.capcontrol.CapControlMessage;
import com.cannontech.messaging.message.capcontrol.DeleteItemMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCDeleteItem;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;

public class DeleteItemSerializer extends
    ThriftInheritanceSerializer<DeleteItemMessage, CapControlMessage, CCDeleteItem, CCMessage> {

    public DeleteItemSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<DeleteItemMessage> getTargetMessageClass() {
        return DeleteItemMessage.class;
    }

    @Override
    protected CCDeleteItem createThrifEntityInstance(CCMessage entityParent) {
        CCDeleteItem entity = new CCDeleteItem();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCDeleteItem entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected DeleteItemMessage createMessageInstance() {
        return new DeleteItemMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCDeleteItem entity, DeleteItemMessage msg) {
        msg.setItemId(entity.get_itemId());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DeleteItemMessage msg, CCDeleteItem entity) {
        entity.set_itemId(msg.getItemId());
    }

}
