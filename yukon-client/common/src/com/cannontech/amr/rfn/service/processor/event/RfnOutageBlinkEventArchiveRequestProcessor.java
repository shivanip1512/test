package com.cannontech.amr.rfn.service.processor.event;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessorBase;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.message.dispatch.message.PointData;

public class RfnOutageBlinkEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessorBase {
    
    @Override
    public <T extends RfnEvent> void process(RfnMeter meter, T event, List<? super PointData> pointDatas) {
        Long count = (Long) getEventDataWithType(event, RfnConditionDataType.COUNT);
        rfnMeterEventService.processAttributePointData(meter, pointDatas, BuiltInAttribute.RFN_BLINK_COUNT, event.getTimeStamp(), count);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.OUTAGE_BLINK;
    }
    
}