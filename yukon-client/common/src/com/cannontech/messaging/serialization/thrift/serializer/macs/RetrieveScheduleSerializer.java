package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.message.macs.message.RetrieveSchedule;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCRetrieveSchedule;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;

public class RetrieveScheduleSerializer
    extends
    ThriftInheritanceSerializer<RetrieveSchedule, Message, MCRetrieveSchedule, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public RetrieveScheduleSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<RetrieveSchedule> getTargetMessageClass() {
        return RetrieveSchedule.class;
    }

    @Override
    protected MCRetrieveSchedule
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        MCRetrieveSchedule entity = new MCRetrieveSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(MCRetrieveSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected RetrieveSchedule createMessageInstance() {
        return new RetrieveSchedule();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCRetrieveSchedule entity,
                                                   RetrieveSchedule msg) {
        msg.setScheduleId(entity.get_id());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, RetrieveSchedule msg,
                                                   MCRetrieveSchedule entity) {
        entity.set_id((int) msg.getScheduleId());
    }
}
