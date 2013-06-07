package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.notif.ControlMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.generated.NotifLMControl;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ControlSerializer extends
    ThriftInheritanceSerializer<ControlMessage, BaseMessage, NotifLMControl, Message> {

    public ControlSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ControlMessage> getTargetMessageClass() {
        return ControlMessage.class;
    }

    @Override
    protected NotifLMControl createThrifEntityInstance(Message entityParent) {
        NotifLMControl entity = new NotifLMControl();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(NotifLMControl entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ControlMessage createMessageInstance() {
        return new ControlMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifLMControl entity, ControlMessage msg) {
        msg.setNotifGroupIds(ConverterHelper.toIntArray(entity.get_notifGroupIds()));
        msg.setNotifType(entity.get_notifType());
        msg.setProgramId(entity.get_programId());
        msg.setStartTime(ConverterHelper.millisecToDate(entity.get_startTime()));
        msg.setStopTime(ConverterHelper.millisecToDate(entity.get_stopTime()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ControlMessage msg, NotifLMControl entity) {
        entity.set_notifGroupIds(ConverterHelper.toList(msg.getNotifGroupIds()));
        entity.set_notifType(msg.getNotifType());
        entity.set_programId(msg.getProgramId());
        entity.set_startTime(ConverterHelper.dateToMillisec(msg.getStartTime()));
        entity.set_stopTime(ConverterHelper.dateToMillisec(msg.getStopTime()));
    }

}
