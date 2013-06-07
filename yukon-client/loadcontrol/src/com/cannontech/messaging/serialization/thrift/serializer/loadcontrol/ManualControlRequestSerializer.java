package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.messaging.message.loadcontrol.LmMessage;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.LMManualControlRequest;
import com.cannontech.messaging.serialization.thrift.generated.LMMessage;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ManualControlRequestSerializer extends
    ThriftInheritanceSerializer<ManualControlRequestMessage, LmMessage, LMManualControlRequest, LMMessage> {

    public ManualControlRequestSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ManualControlRequestMessage> getTargetMessageClass() {
        return ManualControlRequestMessage.class;
    }

    @Override
    protected LMManualControlRequest createThrifEntityInstance(LMMessage entityParent) {
        LMManualControlRequest entity = new LMManualControlRequest();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected LMMessage getThriftEntityParent(LMManualControlRequest entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ManualControlRequestMessage createMessageInstance() {
        return new ManualControlRequestMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMManualControlRequest entity, ManualControlRequestMessage msg) {
        msg.setCommand(entity.get_command());
        msg.setYukonId(entity.get_paoId());
        msg.setNotifyTime(ConverterHelper.millisecToCalendar(entity.get_notifyTime()));
        msg.setStartTime(ConverterHelper.millisecToCalendar(entity.get_startTime()));
        msg.setStopTime(ConverterHelper.millisecToCalendar(entity.get_stopTime()));
        msg.setStartGear(entity.get_startGear());
        msg.setStartPriority(entity.get_startPriority());
        msg.setAddditionalInfo(entity.get_additionalInfo());
        msg.setConstraintFlag(entity.get_constraintCmd());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ManualControlRequestMessage msg, LMManualControlRequest entity) {
        entity.set_command(msg.getCommand());
        entity.set_paoId(msg.getYukonId());
        entity.set_notifyTime(ConverterHelper.calendarToMillisec(msg.getNotifyTime()));
        entity.set_startTime(ConverterHelper.calendarToMillisec(msg.getStartTime()));
        entity.set_stopTime(ConverterHelper.calendarToMillisec(msg.getStopTime()));
        entity.set_startGear(msg.getStartGear());
        entity.set_startPriority(msg.getStartPriority());
        entity.set_additionalInfo(msg.getAddditionalInfo());
        entity.set_constraintCmd(msg.getConstraintFlag());
    }
}
