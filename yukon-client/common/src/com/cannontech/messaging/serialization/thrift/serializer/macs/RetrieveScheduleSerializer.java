package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.RetrieveScheduleMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCRetrieveSchedule;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;

public class RetrieveScheduleSerializer extends
    ThriftInheritanceSerializer<RetrieveScheduleMessage, BaseMessage, MCRetrieveSchedule, Message> {

    public RetrieveScheduleSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<RetrieveScheduleMessage> getTargetMessageClass() {
        return RetrieveScheduleMessage.class;
    }

    @Override
    protected MCRetrieveSchedule createThrifEntityInstance(Message entityParent) {
        MCRetrieveSchedule entity = new MCRetrieveSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCRetrieveSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected RetrieveScheduleMessage createMessageInstance() {
        return new RetrieveScheduleMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCRetrieveSchedule entity, RetrieveScheduleMessage msg) {
        msg.setScheduleId(entity.get_id());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, RetrieveScheduleMessage msg, MCRetrieveSchedule entity) {
        // TODO long to int conversion. Is it expected?
        entity.set_id((int) msg.getScheduleId());
    }
}
