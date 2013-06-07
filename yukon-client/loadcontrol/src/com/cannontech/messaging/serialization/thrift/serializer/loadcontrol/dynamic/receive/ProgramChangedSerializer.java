package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.dynamic.receive;

import com.cannontech.messaging.message.loadcontrol.dynamic.receive.ProgramChanged;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMDynamicProgramData;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ProgramChangedSerializer extends ThriftSerializer<ProgramChanged, LMDynamicProgramData> {

    public ProgramChangedSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<ProgramChanged> getTargetMessageClass() {
        return ProgramChanged.class;
    }

    @Override
    protected ProgramChanged createMessageInstance() {
        return new ProgramChanged();
    }

    @Override
    protected LMDynamicProgramData createThrifEntityInstance() {
        return new LMDynamicProgramData();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMDynamicProgramData entity, ProgramChanged msg) {
        msg.setPaoId(entity.get_paoId());
        msg.setDisableFlag(entity.is_disableFlag());
        msg.setCurrentGearNumber(entity.get_currentGearNumber());
        msg.setLastGroupControlled(entity.get_lastGroupControlled());
        msg.setProgramState(entity.get_programState());
        msg.setReductionTotal(entity.get_reductionTotal());
        msg.setDirectStartTime(ConverterHelper.millisecToCalendar(entity.get_directStartTime()));
        msg.setDirectStopTime(ConverterHelper.millisecToCalendar(entity.get_directStopTime()));
        msg.setNotifyActiveTime(ConverterHelper.millisecToCalendar(entity.get_notifyActiveTime()));
        msg.setNotifyInactiveTime(ConverterHelper.millisecToCalendar(entity.get_notifyInactiveTime()));
        msg.setStartedRampingOutTime(ConverterHelper.millisecToCalendar(entity.get_startedRampingOutTime()));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ProgramChanged msg, LMDynamicProgramData entity) {
        entity.set_paoId(msg.getPaoId());
        entity.set_disableFlag(msg.getDisableFlag());
        entity.set_currentGearNumber(msg.getCurrentGearNumber());
        entity.set_lastGroupControlled(msg.getLastGroupControlled());
        entity.set_programState(msg.getProgramState());
        entity.set_reductionTotal(msg.getReductionTotal());
        entity.set_directStartTime(ConverterHelper.calendarToMillisec(msg.getDirectStartTime()));
        entity.set_directStopTime(ConverterHelper.calendarToMillisec(msg.getDirectStopTime()));
        entity.set_notifyActiveTime(ConverterHelper.calendarToMillisec(msg.getNotifyActiveTime()));
        entity.set_notifyInactiveTime(ConverterHelper.calendarToMillisec(msg.getNotifyInactiveTime()));
        entity.set_startedRampingOutTime(ConverterHelper.calendarToMillisec(msg.getStartedRampingOutTime()));
    }
}
