package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.message.macs.message.DeleteSchedule;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCDeleteSchedule;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class DeleteScheduleSerializer
    extends
    ThriftInheritanceSerializer<DeleteSchedule, Message, MCDeleteSchedule, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public DeleteScheduleSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<DeleteSchedule> getTargetMessageClass() {
        return DeleteSchedule.class;
    }

    @Override
    protected MCDeleteSchedule
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        MCDeleteSchedule entity = new MCDeleteSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(MCDeleteSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected DeleteSchedule createMessageInstance() {
        return new DeleteSchedule();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCDeleteSchedule entity,
                                                   DeleteSchedule msg) {
        msg.setScheduleId(entity.get_id());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DeleteSchedule msg,
                                                   MCDeleteSchedule entity) {
        entity.set_id((int) msg.getScheduleId()); // watch cast! (ported from Roguewave implementation)
    }

}
