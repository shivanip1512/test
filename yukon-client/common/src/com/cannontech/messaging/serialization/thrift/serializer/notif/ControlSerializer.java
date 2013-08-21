package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.util.Message;
import com.cannontech.message.notif.NotifLMControlMsg;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;

import com.cannontech.messaging.serialization.thrift.generated.NotifLMControl;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ControlSerializer
    extends
    ThriftInheritanceSerializer<NotifLMControlMsg, Message, NotifLMControl, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public ControlSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<NotifLMControlMsg> getTargetMessageClass() {
        return NotifLMControlMsg.class;
    }

    @Override
    protected NotifLMControl
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifLMControl entity = new NotifLMControl();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifLMControl entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected NotifLMControlMsg createMessageInstance() {
        return new NotifLMControlMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, NotifLMControl entity,
                                                   NotifLMControlMsg msg) {
        msg.notifGroupIds = ConverterHelper.toIntArray(entity.get_notifGroupIds());
        msg.notifType = entity.get_notifType();
        msg.programId = entity.get_programId();
        msg.startTime = ConverterHelper.millisecToDate(entity.get_startTime());
        msg.stopTime = ConverterHelper.millisecToDate(entity.get_stopTime());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, NotifLMControlMsg msg,
                                                   NotifLMControl entity) {
        entity.set_notifGroupIds(ConverterHelper.toList(msg.notifGroupIds));
        entity.set_notifType(msg.notifType);
        entity.set_programId(msg.programId);
        entity.set_startTime(ConverterHelper.dateToMillisec(msg.startTime));
        entity.set_stopTime(ConverterHelper.dateToMillisec(msg.stopTime));
    }
}
