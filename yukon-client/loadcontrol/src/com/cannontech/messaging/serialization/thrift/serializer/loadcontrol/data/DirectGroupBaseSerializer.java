package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.common.pao.PaoType;
import com.cannontech.messaging.message.loadcontrol.data.DirectGroupBase;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMGroupBase;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class DirectGroupBaseSerializer extends ThriftSerializer<DirectGroupBase, LMGroupBase> {

    public DirectGroupBaseSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<DirectGroupBase> getTargetMessageClass() {
        return DirectGroupBase.class;
    }

    @Override
    protected DirectGroupBase createMessageInstance() {
        return null;
    }

    @Override
    protected LMGroupBase createThrifEntityInstance() {
        return new LMGroupBase();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMGroupBase entity, DirectGroupBase msg) {
        msg.setYukonId(entity.get_paoId());

        // msg.setXXX(entity.get_paoCategory()) // No longer used. Replaced by PaoType.paoCategory
        // msg.setYYY(entity.get_paoClass()); // No longer used. Replaced by PaoType.paoCategory

        msg.setYukonName(entity.get_paoName());
        msg.setYukonType(PaoType.getForDbString(entity.get_paoTypeString()));
        msg.setYukonDescription(entity.get_paoDescription());
        msg.setDisableFlag(entity.is_disableFlag());
        msg.setGroupOrder(entity.get_groupOrder());
        msg.setKwCapacity(entity.get_kwCapacity());
        msg.setChildOrder(entity.get_childOrder());
        msg.setAlarmInhibit(entity.is_alarmInhibit());
        msg.setControlInhibit(entity.is_controlInhibit());
        msg.setGroupControlState(entity.get_groupControlState());
        msg.setCurrentHoursDaily(entity.get_currentHoursDaily());
        msg.setCurrentHoursMonthly(entity.get_currentHoursMonthly());
        msg.setCurrentHoursSeasonal(entity.get_currentHoursSeasonal());
        msg.setCurrentHoursAnnually(entity.get_currentHoursAnnually());
        msg.setLastControlSent(ConverterHelper.millisecToCalendar(entity.get_lastControlSent()));
        msg.setControlStartTime(ConverterHelper.millisecToDate(entity.get_controlStartTime()));
        msg.setControlCompleteTime(ConverterHelper.millisecToDate(entity.get_controlCompleteTime()));
        msg.setNextControlTime(ConverterHelper.millisecToDate(entity.get_nextControlTime()));
        msg.setInternalState(entity.get_internalState());
        msg.setDailyOps(entity.get_dailyOps());
        msg.setLastStopTimeSent(ConverterHelper.millisecToDate(entity.get_lastStopTimeSent()));

    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, DirectGroupBase msg, LMGroupBase entity) {
        /*
         * This isn't implemented because we won't be sending full LMControlAreas to the Server
         */
    }
}
