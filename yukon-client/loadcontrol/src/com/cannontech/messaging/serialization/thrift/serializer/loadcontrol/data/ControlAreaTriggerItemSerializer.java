package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.data;

import com.cannontech.dr.controlarea.model.TriggerType;
import com.cannontech.messaging.message.loadcontrol.data.ControlAreaTriggerItem;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class ControlAreaTriggerItemSerializer
    extends
    ThriftSerializer<ControlAreaTriggerItem, com.cannontech.messaging.serialization.thrift.generated.LMControlAreaTrigger> {

    public ControlAreaTriggerItemSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<ControlAreaTriggerItem> getTargetMessageClass() {
        return ControlAreaTriggerItem.class;
    }

    @Override
    protected ControlAreaTriggerItem createMessageInstance() {
        return new ControlAreaTriggerItem();
    }

    @Override
    protected com.cannontech.messaging.serialization.thrift.generated.LMControlAreaTrigger createThrifEntityInstance() {
        return new com.cannontech.messaging.serialization.thrift.generated.LMControlAreaTrigger();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, 
        com.cannontech.messaging.serialization.thrift.generated.LMControlAreaTrigger entity, ControlAreaTriggerItem msg) {
        msg.setYukonId(entity.get_paoId());
        msg.setTriggerNumber(entity.get_triggerNumber());
        msg.setTriggerType(TriggerType.getForDbString(entity.get_triggerType()));
        msg.setPointId(entity.get_pointId());
        msg.setPointValue(entity.get_pointValue());
        msg.setLastPointValueTimeStamp(ConverterHelper.millisecToDate(entity.get_lastPointValueTimestamp()));
        msg.setNormalState(entity.get_normalState());
        msg.setThreshold(entity.get_threshold());
        msg.setProjectionType(entity.get_projectionType());
        msg.setProjectionPoints(entity.get_projectionPoints());
        msg.setProjectAheadDuration(entity.get_projectAheadDuration());
        msg.setThresholdKickPercent(entity.get_thresholdKickPercent());
        msg.setMinRestoreOffset(entity.get_minRestoreOffset());
        msg.setPeakPointId(entity.get_peakPointId());
        msg.setPeakPointValue(entity.get_peakPointValue());
        msg.setLastPeakPointValueTimeStamp(ConverterHelper.millisecToDate(entity.get_lastPeakPointValueTimestamp()));
        msg.setProjectedPointValue(entity.get_projectedPointValue());
    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, ControlAreaTriggerItem msg,
        com.cannontech.messaging.serialization.thrift.generated.LMControlAreaTrigger entity) {
        /*
         * This isn't implemented because we won't be sending full LMControlAreas to the Server
         */
    }

}
