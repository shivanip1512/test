package com.cannontech.messaging.serialization.thrift.serializer.loadcontrol.dynamic.receive;

import com.cannontech.loadcontrol.dynamic.receive.LMTriggerChanged;
import com.cannontech.messaging.serialization.thrift.ThriftMessageFactory;
import com.cannontech.messaging.serialization.thrift.ThriftSerializer;
import com.cannontech.messaging.serialization.thrift.generated.LMDynamicTriggerData;
import com.cannontech.messaging.serialization.util.ConverterHelper;

public class TriggerChangedSerializer extends ThriftSerializer<LMTriggerChanged, LMDynamicTriggerData> {

    public TriggerChangedSerializer(String messageType) {
        super(messageType);
    }

    @Override
    public Class<LMTriggerChanged> getTargetMessageClass() {
        return LMTriggerChanged.class;
    }

    @Override
    protected LMTriggerChanged createMessageInstance() {
        return new LMTriggerChanged();
    }

    @Override
    protected LMDynamicTriggerData createThriftEntityInstance() {
        return new LMDynamicTriggerData();
    }

    @Override
    protected void populateMessageFromThriftEntity(ThriftMessageFactory msgFactory, LMDynamicTriggerData entity,
                                                   LMTriggerChanged msg) {

        msg.setPaoID(entity.get_paoId());
        msg.setTriggerNumber(entity.get_triggerNumber());
        msg.setPointValue(entity.get_pointValue());
        msg.setLastPointValueTimestamp(ConverterHelper.millisecToCalendar(entity.get_lastPointValueTimestamp()));
        msg.setNormalState(entity.get_normalState());
        msg.setThreshold(entity.get_threshold());
        msg.setPeakPointValue(entity.get_peakPointValue());
        msg.setLastPeakPointValueTimestamp(ConverterHelper.millisecToCalendar(entity.get_lastPeakPointValueTimestamp()));
        msg.setProjectedPointValue(entity.get_projectedPointValue());

    }

    @Override
    protected void populateThriftEntityFromMessage(ThriftMessageFactory msgFactory, LMTriggerChanged msg,
                                                   LMDynamicTriggerData entity) {
        entity.set_paoId(msg.getPaoID());
        entity.set_triggerNumber(msg.getTriggerNumber());
        entity.set_pointValue(msg.getPointValue());
        entity.set_lastPointValueTimestamp(ConverterHelper.calendarToMillisec(msg.getLastPointValueTimestamp()));
        entity.set_normalState(msg.getNormalState());
        entity.set_threshold(msg.getThreshold());
        entity.set_peakPointValue(msg.getPeakPointValue());
        entity
            .set_lastPeakPointValueTimestamp(ConverterHelper.calendarToMillisec(msg.getLastPeakPointValueTimestamp()));
        entity.set_projectedPointValue(msg.getProjectedPointValue());
    }
}
