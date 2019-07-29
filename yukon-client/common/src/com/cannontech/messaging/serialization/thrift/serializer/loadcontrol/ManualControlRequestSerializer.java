package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol;

import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.messages.LMMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ManualControlRequestSerializer
    extends
    ThriftInheritanceSerializer<LMManualControlRequest, LMMessage, com.cannontech.messaging.serialization.thrift.generated.LMManualControlRequest, com.cannontech.messaging.serialization.thrift.generated.LMMessage> {

    public ManualControlRequestSerializer(String messageType, LmMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<LMManualControlRequest> getTargetMessageClass() {
        return LMManualControlRequest.class;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMManualControlRequest
        createThriftEntityInstance(com.cannontech.messaging.serialization.thrift.generated.LMMessage entityParent) {
        com.cannontech.messaging.serialization.thrift.generated.LMManualControlRequest entity =
            new com.cannontech.messaging.serialization.thrift.generated.LMManualControlRequest();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMMessage
        getThriftEntityParent(com.cannontech.messaging.serialization.thrift.generated.LMManualControlRequest entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected LMManualControlRequest createMessageInstance() {
        return new LMManualControlRequest();
    }

    @Override
    protected
        void
        populateMessageFromThriftEntity(ThriftMessageFactory msgFactory,
                                        com.cannontech.messaging.serialization.thrift.generated.LMManualControlRequest entity,
                                        LMManualControlRequest msg) {
        msg.setCommand(entity.get_command());
        msg.setYukonID(entity.get_paoId());
        msg.setNotifyTime(ConverterHelper.millisecToCalendar(entity.get_notifyTime()));
        msg.setStartTime(ConverterHelper.millisecToCalendar(entity.get_startTime()));
        msg.setStopTime(ConverterHelper.millisecToCalendar(entity.get_stopTime()));
        msg.setStartGear(entity.get_startGear());
        msg.setStartPriority(entity.get_startPriority());
        msg.setAddditionalInfo(entity.get_additionalInfo());
        msg.setConstraintFlag(entity.get_constraintCmd());
        msg.setOriginSource(entity.get_originSource());
    }

    @Override
    protected
        void
        populateThriftEntityFromMessage(ThriftMessageFactory msgFactory,
                                        LMManualControlRequest msg,
                                        com.cannontech.messaging.serialization.thrift.generated.LMManualControlRequest entity) {
        entity.set_command(msg.getCommand());
        entity.set_paoId(msg.getYukonID());
        entity.set_notifyTime(ConverterHelper.calendarToMillisec(msg.getNotifyTime()));
        entity.set_startTime(ConverterHelper.calendarToMillisec(msg.getStartTime()));
        entity.set_stopTime(ConverterHelper.calendarToMillisec(msg.getStopTime()));
        entity.set_startGear(msg.getStartGear());
        entity.set_startPriority(msg.getStartPriority());
        entity.set_additionalInfo(msg.getAddditionalInfo());
        entity.set_constraintCmd(msg.getConstraintFlag());
        entity.set_originSource(msg.getOriginSource());
    }
}
