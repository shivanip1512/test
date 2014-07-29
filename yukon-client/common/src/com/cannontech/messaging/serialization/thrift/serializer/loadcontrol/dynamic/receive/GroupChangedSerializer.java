package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.dynamic.receive;

import com.cannontech.loadcontrol.dynamic.receive.LMGroupChanged;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMDynamicGroupData;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class GroupChangedSerializer extends ThriftSerializer<LMGroupChanged, LMDynamicGroupData> {

    public GroupChangedSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMGroupChanged> getTargetMessageClass() {
        return LMGroupChanged.class;
    }

    @Override
    protected LMGroupChanged createMessageInstance() {
        return new LMGroupChanged();
    }

    @Override
    protected LMDynamicGroupData createThriftEntityInstance() {
        return new LMDynamicGroupData();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMDynamicGroupData entity,
                                                   LMGroupChanged msg) {
        msg.setPaoID(entity.get_paoId());
        msg.setDisableFlag(entity.is_disableFlag());
        msg.setGroupControlState(entity.get_groupControlState());
        msg.setCurrentHoursDaily(entity.get_currentHoursDaily());
        msg.setCurrentHoursMonthly(entity.get_currentHoursMonthly());
        msg.setCurrentHoursSeasonal(entity.get_currentHoursSeasonal());
        msg.setCurrentHoursAnnually(entity.get_currentHoursAnnually());
        msg.setLastControlSent(ConverterHelper.millisecToCalendar(entity.get_lastControlSent()));
        msg.setControlStartTime(ConverterHelper.millisecToCalendar(entity.get_controlStartTime()));
        msg.setControlCompleteTime(ConverterHelper.millisecToCalendar(entity.get_controlCompleteTime()));
        msg.setNextControlTime(ConverterHelper.millisecToCalendar(entity.get_nextControlTime()));
        msg.setInternalState(entity.get_internalState());
        msg.setDailyOps(entity.get_dailyOps());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMGroupChanged msg,
                                                   LMDynamicGroupData entity) {
        entity.set_paoId(msg.getPaoID());
        entity.set_disableFlag(msg.getDisableFlag());
        entity.set_groupControlState(msg.getGroupControlState());
        entity.set_currentHoursDaily(msg.getCurrentHoursDaily());
        entity.set_currentHoursMonthly(msg.getCurrentHoursMonthly());
        entity.set_currentHoursSeasonal(msg.getCurrentHoursSeasonal());
        entity.set_currentHoursAnnually(msg.getCurrentHoursAnnually());
        entity.set_lastControlSent(ConverterHelper.calendarToMillisec(msg.getLastControlSent()));
        entity.set_controlStartTime(ConverterHelper.calendarToMillisec(msg.getControlStartTime()));
        entity.set_controlCompleteTime(ConverterHelper.calendarToMillisec(msg.getControlCompleteTime()));
        entity.set_nextControlTime(ConverterHelper.calendarToMillisec(msg.getNextControlTime()));
        entity.set_internalState(msg.getInternalState());
        entity.set_dailyOps(msg.getDailyOps());
    }

}
