package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.common.pao.PaoType;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMControlAreaItem;
import com.cannontech.messaging.serialization.thrift.util.ThriftConverterHelper;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ControlAreaItemSerializer extends ThriftSerializer<LMControlArea, LMControlAreaItem> {

    public ControlAreaItemSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMControlArea> getTargetMessageClass() {
        return LMControlArea.class;
    }

    @Override
    protected LMControlArea createMessageInstance() {
        return new LMControlArea();
    }

    @Override
    protected LMControlAreaItem createThriftEntityInstance() {
        return new LMControlAreaItem();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMControlAreaItem entity, LMControlArea msg) {
        ThriftConverterHelper helper = msgFactory.getConverterHelper();
        
        msg.setYukonID(entity.get_paoId());
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
                                                           LMControlAreaTrigger.class));
        msg.setLmProgramVector(helper.convertToMessageVector(entity.get_lmPrograms(), LMProgramBase.class));
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMControlArea msg, LMControlAreaItem entity) {
        /*
         * This saveGuts isn't implemented because we won't be sending full LMControlAreas to the Server
         */
        throw new UnsupportedOperationException("Message serialization not supported");
    }

}
