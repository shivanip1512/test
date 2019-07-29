package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.dynamic.receive;

import com.cannontech.loadcontrol.dynamic.receive.LMProgramChanged;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMDynamicProgramData;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ProgramChangedSerializer extends ThriftSerializer<LMProgramChanged, LMDynamicProgramData> {

    public ProgramChangedSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMProgramChanged> getTargetMessageClass() {
        return LMProgramChanged.class;
    }

    @Override
    protected LMProgramChanged createMessageInstance() {
        return new LMProgramChanged();
    }

    @Override
    protected LMDynamicProgramData createThriftEntityInstance() {
        return new LMDynamicProgramData();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMDynamicProgramData entity,
                                                   LMProgramChanged msg) {
        msg.setPaoID(entity.get_paoId());
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
        msg.setOriginSource(entity.get_originSource());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMProgramChanged msg,
                                                   LMDynamicProgramData entity) {
        entity.set_paoId(msg.getPaoID());
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
        entity.set_originSource(msg.getOriginSource());
    }
}
