package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.common.pao.PaoType;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaTriggerItem;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMControlAreaItem;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ControlAreaItemSerializer extends ThriftSerializer<ControlAreaItem, LMControlAreaItem> {

    public ControlAreaItemSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<ControlAreaItem> getTargetMessageClass() {
        return ControlAreaItem.class;
    }

    @Override
    protected ControlAreaItem createMessageInstance() {
        return new ControlAreaItem();
    }

    @Override
    protected LMControlAreaItem createThrifEntityInstance() {
        return new LMControlAreaItem();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMControlAreaItem entity, ControlAreaItem msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setYukonId(entity.get_paoId());
        // yukonCategory No longer used
        // yukonClass No longer used
        msg.setYukonName(entity.get_paoName());
        msg.setYukonType(PaoType.getForDbString(entity.get_paoTypeString()));
        msg.setYukonDescription(entity.get_paoDescription());
        msg.setDisableFlag(entity.is_disableFlag());
        msg.setDefOperationalState(entity.get_defOperationalState());
        msg.setControlInterval(entity.get_controlInterval());
        msg.setMinResponseTime(entity.get_minResponseTime());
        msg.setDefDailyStartTime((int) entity.get_defDailyStartTime());
        msg.setDefDailyStopTime((int) entity.get_defDailyStopTime());
        msg.setRequireAllTriggersActiveFlag(entity.is_requireAllTriggersActiveFlag());
        msg.setNextCheckTime(ConverterHelper.millisecToCalendar(entity.get_nextCheckTime()));
        msg.setNewPointDataReceivedFlag(entity.is_newPointDataReceivedFlag());
        msg.setUpdatedFlag(entity.is_updatedFlag());
        msg.setControlAreaStatusPointId(entity.get_controlAreaStatusPointId());
        msg.setControlAreaState(entity.get_controlAreaState());
        msg.setCurrentPriority(entity.get_currentPriority());
        msg.setCurrentDailyStartTime((int) entity.get_currentDailyStartTime());
        msg.setCurrentDailyStopTime((int) entity.get_currentDailyStopTime());
        msg.setTriggerVector(helper.convertToMessageVector(entity.get_lmControlAreaTriggers(),
            ControlAreaTriggerItem.class));
        msg.setProgramVector(helper.convertToMessageVector(entity.get_lmPrograms(), Program.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ControlAreaItem msg, LMControlAreaItem entity) {
        /*
         * This saveGuts isn't implemented because we won't be sending full LMControlAreas to the Server
         */
    }

}
