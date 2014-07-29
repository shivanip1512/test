package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.dynamic.receive;

import com.cannontech.loadcontrol.dynamic.receive.LMControlAreaChanged;
import com.cannontech.loadcontrol.dynamic.receive.LMTriggerChanged;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMDynamicControlAreaData;
import com.cannontech.messaging.serialization.thrift.generated.LMDynamicTriggerData;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ControlAreaChangedSerializer extends ThriftSerializer<LMControlAreaChanged, LMDynamicControlAreaData> {

    public ControlAreaChangedSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMControlAreaChanged> getTargetMessageClass() {
        return LMControlAreaChanged.class;
    }

    @Override
    protected LMControlAreaChanged createMessageInstance() {
        return new LMControlAreaChanged();
    }

    @Override
    protected LMDynamicControlAreaData createThriftEntityInstance() {
        return new LMDynamicControlAreaData();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMDynamicControlAreaData entity,
                                                   LMControlAreaChanged msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        msg.setPaoID(entity.get_paoId());
        msg.setDisableFlag(ConverterHelper.intToBool(entity.get_disableFlag()));
        msg.setNextCheckTime(ConverterHelper.millisecToCalendar(entity.get_nextCheckTime()));
        msg.setControlAreaState(entity.get_controlAreaState());
        msg.setCurrentPriority(entity.get_currentPriority());
        msg.setCurrentDailyStartTime(entity.get_currentDailyStartTime());
        msg.setCurrentDailyStopTime(entity.get_currentDailyStopTime());
        msg.setTriggers(helper.convertToMessageList(entity.get_triggers(), LMTriggerChanged.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMControlAreaChanged msg,
                                                   LMDynamicControlAreaData entity) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();

        entity.set_paoId(msg.getPaoID());
        entity.set_disableFlag(ConverterHelper.boolToInt(msg.getDisableFlag()));
        entity.set_nextCheckTime(ConverterHelper.calendarToMillisec(msg.getNextCheckTime()));
        entity.set_controlAreaState(msg.getControlAreaState());
        entity.set_currentPriority(msg.getCurrentPriority());
        entity.set_currentDailyStartTime(msg.getCurrentDailyStartTime());
        entity.set_currentDailyStopTime(msg.getCurrentDailyStopTime());
        entity.set_triggers(helper.convertToEntityList(msg.getTriggers(), LMDynamicTriggerData.class));
    }
}
