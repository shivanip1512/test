package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.DeleteScheduleMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCDeleteSchedule;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class DeleteScheduleSerializer extends
    ThriftInheritanceSerializer<DeleteScheduleMessage, BaseMessage, MCDeleteSchedule, Message> {

    public DeleteScheduleSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<DeleteScheduleMessage> getTargetMessageClass() {
        return DeleteScheduleMessage.class;
    }

    @Override
    protected MCDeleteSchedule createThrifEntityInstance(Message entityParent) {
        MCDeleteSchedule entity = new MCDeleteSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCDeleteSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected DeleteScheduleMessage createMessageInstance() {
        return new DeleteScheduleMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCDeleteSchedule entity, DeleteScheduleMessage msg) {
        msg.setScheduleId(entity.get_id());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DeleteScheduleMessage msg, MCDeleteSchedule entity) {
        // TODO long to int conversion. is it expected?
        entity.set_id((int) msg.getScheduleId());
    }

}
