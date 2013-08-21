package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.message.macs.message.OverrideRequest;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCOverrideRequest;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class OverrideRequestSerializer
    extends
    ThriftInheritanceSerializer<OverrideRequest, Message, MCOverrideRequest, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public OverrideRequestSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<OverrideRequest> getTargetMessageClass() {
        return OverrideRequest.class;
    }

    @Override
    protected MCOverrideRequest
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        MCOverrideRequest entity = new MCOverrideRequest();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(MCOverrideRequest entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected OverrideRequest createMessageInstance() {
        return new OverrideRequest();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCOverrideRequest entity,
                                                   OverrideRequest msg) {
        msg.setAction(entity.get_action());
        msg.setSchedId(entity.get_id());
        msg.setStart(ConverterHelper.millisecToDate(entity.get_startTime()));
        msg.setStop(ConverterHelper.millisecToDate(entity.get_stopTime()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, OverrideRequest msg,
                                                   MCOverrideRequest entity) {
        entity.set_action(msg.getAction());
        entity.set_id(ConverterHelper.UnsignedToInt(msg.getSchedId()));
        entity.set_startTime(ConverterHelper.dateToMillisec(msg.getStart()));
        entity.set_stopTime(ConverterHelper.dateToMillisec(msg.getStop()));
    }
}
