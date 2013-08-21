package com.cannontech.messaging.serialization.thrift.serializer.notif;

import com.cannontech.message.notif.ProgramActionMsg;
import com.cannontech.message.util.Message;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.NotifProgramAction;
import com.cannontech.messaging.serialization.thrift.serializer.MessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ProgramActionSerializer
    extends
    ThriftInheritanceSerializer<ProgramActionMsg, Message, NotifProgramAction, com.cannontech.messaging.serialization.thrift.generated.Message> {

    public ProgramActionSerializer(String messageType, MessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ProgramActionMsg> getTargetMessageClass() {
        return ProgramActionMsg.class;
    }

    @Override
    protected NotifProgramAction
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.Message entityParent) {
        NotifProgramAction entity = new NotifProgramAction();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.Message
        getThriftEntityParent(NotifProgramAction entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ProgramActionMsg createMessageInstance() {
        return new ProgramActionMsg();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, NotifProgramAction entity,
                                                   ProgramActionMsg msg) {
        msg.action = entity.get_action();
        msg.customerIds = ConverterHelper.toIntArray(entity.get_customerIds());
        msg.eventDisplayName = entity.get_eventDisplayName();
        msg.notificationTime = ConverterHelper.millisecToDate(entity.get_notificationTime());
        msg.programId = entity.get_programId();
        msg.startTime = ConverterHelper.millisecToDate(entity.get_startTime());
        msg.stopTime = ConverterHelper.millisecToDate(entity.get_stopTime());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, ProgramActionMsg msg,
                                                   NotifProgramAction entity) {
        entity.set_action(msg.action);
        entity.set_customerIds(ConverterHelper.toList(msg.customerIds));
        entity.set_eventDisplayName(msg.eventDisplayName);
        entity.set_notificationTime(ConverterHelper.dateToMillisec(msg.notificationTime));
        entity.set_programId(msg.programId);
        entity.set_startTime(ConverterHelper.dateToMillisec(msg.startTime));
        entity.set_stopTime(ConverterHelper.dateToMillisec(msg.stopTime));
    }
}
