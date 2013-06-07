package com.cannontech.messaging.serialization.thrift.serializer.macs;

import com.cannontech.messaging.message.BaseMessage;
import com.cannontech.messaging.message.macs.ScheduleMessage;
import com.cannontech.messaging.serialization.thrift.ThriftInheritanceSerializer;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.generated.MCSchedule;
import com.cannontech.messaging.serialization.thrift.generated.Message;
import com.cannontech.messaging.serialization.thrift.serializer.BaseMessageSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ScheduleSerializer extends ThriftInheritanceSerializer<ScheduleMessage, BaseMessage, MCSchedule, Message> {

    public ScheduleSerializer(String messageType, BaseMessageSerializer parentSerializer) {
        super(messageType, parentSerializer);
    }

    @Override
    public Class<ScheduleMessage> getTargetMessageClass() {
        return ScheduleMessage.class;
    }

    @Override
    protected MCSchedule createThrifEntityInstance(Message entityParent) {
        MCSchedule entity = new MCSchedule();
        entity.set_baseMessage(entityParent);
        return entity;
    }

    @Override
    protected Message getThriftEntityParent(MCSchedule entity) {
        return entity.get_baseMessage();
    }

    @Override
    protected ScheduleMessage createMessageInstance() {
        return new ScheduleMessage();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, MCSchedule entity, ScheduleMessage msg) {
        msg.setCategoryName(entity.get_categoryName());
        msg.setCurrentState(entity.get_currentState());
        msg.setDuration(entity.get_duration());
        msg.setHolidayScheduleId(entity.get_holidayScheduleId());
        msg.setId(entity.get_scheduleId());
        msg.setLastRunStatus(entity.get_lastRunStatus());
        msg.setLastRunTime(ConverterHelper.millisecToDate(entity.get_lastRunTime()));
        msg.setManualStartTime(ConverterHelper.millisecToDate(entity.get_manualStartTime()));
        msg.setManualStopTime(ConverterHelper.millisecToDate(entity.get_manualStopTime()));
        msg.setNextRunTime(ConverterHelper.millisecToDate(entity.get_currentStartTime()));
        msg.setNextStopTime(ConverterHelper.millisecToDate(entity.get_currentStopTime()));
        msg.setRepeatInterval(entity.get_repeatInterval());
        msg.setScheduleName(entity.get_scheduleName());
        msg.setScriptFileName(entity.get_scheduleType());
        msg.setStartCommand(entity.get_startCommand());
        msg.setStartDay(entity.get_startDay());
        msg.setStartMonth(entity.get_startMonth());
        msg.setStartPolicy(entity.get_startPolicy());
        msg.setStartTime(entity.get_startTime());
        msg.setStartYear(entity.get_startYear());
        msg.setStopCommand(entity.get_stopCommand());
        msg.setStopPolicy(entity.get_stopPolicy());
        msg.setStopTime(entity.get_stopTime());
        msg.setTargetPAObjectId(entity.get_targetPaoId());
        msg.setTemplateType(entity.get_templateType());
        msg.setType(entity.get_scheduleType());
        msg.setValidWeekDays(entity.get_validWeekDays());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ScheduleMessage msg, MCSchedule entity) {
        entity.set_categoryName(msg.getCategoryName());
        entity.set_commandFile(msg.getScriptFileName());
        entity.set_currentStartTime(ConverterHelper.dateToMillisec(msg.getNextRunTime()));
        entity.set_currentState(msg.getCategoryName());
        entity.set_currentStopTime(ConverterHelper.dateToMillisec(msg.getNextStopTime()));
        entity.set_duration(msg.getDuration());
        entity.set_holidayScheduleId(msg.getHolidayScheduleId());
        entity.set_lastRunStatus(msg.getLastRunStatus());
        entity.set_lastRunTime(ConverterHelper.dateToMillisec(msg.getLastRunTime()));
        entity.set_manualStartTime(ConverterHelper.dateToMillisec(msg.getManualStartTime()));
        entity.set_manualStopTime(ConverterHelper.dateToMillisec(msg.getManualStopTime()));
        entity.set_repeatInterval(msg.getRepeatInterval());
        entity.set_scheduleId(msg.getId());
        entity.set_scheduleName(msg.getScheduleName());
        entity.set_scheduleType(msg.getType());
        entity.set_startCommand(msg.getStartCommand());
        entity.set_startDay(msg.getStartDay());
        entity.set_startMonth(msg.getStartMonth());
        entity.set_startPolicy(msg.getStartPolicy());
        entity.set_startTime(msg.getStartTime());
        entity.set_startYear(msg.getStartYear());
        entity.set_stopCommand(msg.getStopCommand());
        entity.set_stopPolicy(msg.getStopPolicy());
        entity.set_stopTime(msg.getStopTime());
        entity.set_targetPaoId(msg.getTargetPAObjectId());
        entity.set_templateType(msg.getTemplateType());
        entity.set_validWeekDays(msg.getValidWeekDays());
    }

}
