package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.messaging.message.dispatch.PointDataMessage;

public class RfnOutageBlinkEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event, List<? super PointDataMessage> pointDatas) {
        Long count = (Long) getEventDataWithType(event, RfnConditionDataType.COUNT);
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.RFN_BLINK_COUNT, event.getTimeStamp(), count);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.OUTAGE_BLINK;
    }
    
}