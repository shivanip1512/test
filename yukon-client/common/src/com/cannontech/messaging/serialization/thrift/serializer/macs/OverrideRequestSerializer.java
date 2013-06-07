package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.OverrideRequestMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCOverrideRequest;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class OverrideRequestSerializer extends
    ThriftInheritanceSerializer<OverrideRequestMessage, BaseMessage, MCOverrideRequest, Message> {

    public OverrideRequestSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<OverrideRequestMessage> getTargetMessageClass() {
        return OverrideRequestMessage.class;
    }

    @Override
    protected MCOverrideRequest createThrifEntityInstance(Message entityParent) {
        MCOverrideRequest entity = new MCOverrideRequest();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCOverrideRequest entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected OverrideRequestMessage createMessageInstance() {
        return new OverrideRequestMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCOverrideRequest entity, OverrideRequestMessage msg) {
        msg.setAction(entity.get_action());
        msg.setSchedId(entity.get_id());
        msg.setStart(ConverterHelper.millisecToDate(entity.get_startTime()));
        msg.setStop(ConverterHelper.millisecToDate(entity.get_stopTime()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, OverrideRequestMessage msg, MCOverrideRequest entity) {
        entity.set_action(msg.getAction());
        // TODO long to int conversion. is it expected?
        entity.set_id((int) msg.getSchedId());
        entity.set_startTime(ConverterHelper.dateToMillisec(msg.getStart()));
        entity.set_stopTime(ConverterHelper.dateToMillisec(msg.getStop()));
    }
}
