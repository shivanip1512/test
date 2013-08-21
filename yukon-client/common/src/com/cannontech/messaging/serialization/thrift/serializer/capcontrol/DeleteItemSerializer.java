package com.cannontech.messaging.serialization.thrift.serializer.capcontrol;

import com.cannontech.message.capcontrol.model.CapControlMessage;
import com.cannontech.message.capcontrol.model.DeleteItem;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.CCDeleteItem;
import com.cannontech.messaging.serialization.thrift.generated.CCMessage;

public class DeleteItemSerializer extends
    ThriftInheritanceSerializer<DeleteItem, CapControlMessage, CCDeleteItem, CCMessage> {

    public DeleteItemSerializer(String messageType, CapControlSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<DeleteItem> getTargetMessageClass() {
        return DeleteItem.class;
    }

    @Override
    protected CCDeleteItem createThriftEntityInstance(CCMessage entityParent) {
        CCDeleteItem entity = new CCDeleteItem();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected CCMessage getThriftEntityParent(CCDeleteItem entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected DeleteItem createMessageInstance() {
        return new DeleteItem();
    }

    @Override
    protected void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, CCDeleteItem entity, DeleteItem msg) {
        msg.setItemId(entity.get_itemId());
    }

    @Override
    protected void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DeleteItem msg, CCDeleteItem entity) {
        entity.set_itemId(msg.getItemId());
    }

}
