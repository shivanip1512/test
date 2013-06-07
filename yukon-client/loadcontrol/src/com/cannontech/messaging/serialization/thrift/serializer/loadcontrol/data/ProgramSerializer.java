package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.common.pao.PaoType;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.loadcontrol.data.ProgramControlWindow;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramBase;
import com.cannontech.messaging.serialization.thrift.generated.LMProgramControlWindow;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ProgramSerializer extends ThriftSerializer<Program, LMProgramBase> {

    protected ProgramSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<Program> getTargetMessageClass() {
        return Program.class;
    }

    @Override
    protected Program createMessageInstance() {
        throw new RuntimeException("The abstract class \""+ Program.class.getName() + "\" can not be instanciated.");
    }

    @Override
    protected LMProgramBase createThrifEntityInstance() {
        return new LMProgramBase();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory factory, LMProgramBase entity, Program msg) {
        ThriftConverterHelper helper = factory.getConverterHelper();
        
        msg.setAvailableWeekDays(entity.get_availableWeekdays());
        msg.setControlType(entity.get_controlType());
        msg.setControlWindowVector(helper.convertToMessageVector(entity.get_lmProgramControlWindows(), ProgramControlWindow.class));
        msg.setDisableFlag(entity.is_disableFlag());
        msg.setLastControlSent(ConverterHelper.millisecToCalendar(entity.get_lastControlSent()));
        msg.setManualControlReceivedFlag(entity.is_manualControlReceivedFlag());
        msg.setMaxHoursAnnually(entity.get_maxHoursAnnually());
        msg.setMaxHoursDaily(entity.get_maxHoursDaily());        
        msg.setMaxHoursMonthly(entity.get_maxHoursMonthly());
        msg.setMaxHoursSeasonal(entity.get_maxHoursSeasonal());
        msg.setMinActivateTime(entity.get_minActivateTime());
        msg.setMinResponseTime(entity.get_minRestartTime());
        msg.setProgramStatus(entity.get_programState());
        msg.setProgramStatusPointId(entity.get_programStatusPointId());
        msg.setReductionAnalogPointId(entity.get_reductionAnalogPointId());
        msg.setReductionTotal(entity.get_reductionTotal());
        msg.setStartedControlling(ConverterHelper.millisecToCalendar(entity.get_startedControlling()));
        msg.setStartPriority(entity.get_startPriority());
        msg.setStoppedControlling(ConverterHelper.millisecToCalendar(entity.get_lastControlSent()));
        msg.setStopPriority(entity.get_stopPriority());
        msg.setYukonDescription(entity.get_paoDescription());
        msg.setYukonId(entity.get_paoId());
        msg.setYukonName(entity.get_paoName());
        msg.setYukonType(PaoType.getForDbString(entity.get_paoTypeString()));        
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory factory, Program msg, LMProgramBase entity) {
        ThriftConverterHelper helper = factory.getConverterHelper();
        
        entity.set_availableWeekdays(msg.getAvailableWeekDays());
        entity.set_controlType(msg.getControlType());
        entity.set_disableFlag(msg.getDisableFlag());
        entity.set_lastControlSent(ConverterHelper.calendarToMillisec(msg.getStoppedControlling()));
        entity.set_lmProgramControlWindows(helper.convertToEntityList(msg.getControlWindowVector(), LMProgramControlWindow.class));
        entity.set_manualControlReceivedFlag(msg.getManualControlReceivedFlag());
        entity.set_maxHoursAnnually(msg.getMaxHoursAnnually());
        entity.set_maxHoursDaily(msg.getMaxHoursDaily());
        entity.set_maxHoursMonthly(msg.getMaxHoursMonthly());
        entity.set_maxHoursSeasonal(msg.getMaxHoursSeasonal());
        entity.set_minActivateTime(msg.getMinActivateTime());
        entity.set_minRestartTime(msg.getMinResponseTime());
        
        entity.set_paoDescription(msg.getYukonDescription());
        entity.set_paoId(msg.getYukonId());
        entity.set_paoName(msg.getYukonName());
        entity.set_paoTypeString(msg.getYukonType().getDbString());
        entity.set_programState(msg.getProgramStatus());
        entity.set_programStatusPointId(msg.getProgramStatusPointId());
        entity.set_reductionAnalogPointId(msg.getReductionAnalogPointId());
        entity.set_reductionTotal(msg.getReductionTotal());
        entity.set_startedControlling(ConverterHelper.calendarToMillisec(msg.getStartedControlling()));
        entity.set_startPriority(msg.getStartPriority());
        entity.set_stopPriority(msg.getStopPriority());


        // Unused field
        entity.set_paoCategory("");
        entity.set_paoClass("");
    }

}
