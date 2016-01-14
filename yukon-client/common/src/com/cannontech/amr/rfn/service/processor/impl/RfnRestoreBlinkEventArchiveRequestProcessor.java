package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public class RfnRestoreBlinkEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        rfnMeterEventService.processAttributePointData(device, 
                                                       pointDatas, 
                                                       BuiltInAttribute.RFN_BLINK_RESTORE_COUNT, 
                                                       instantOf(event), 
                                                       getLongEventData(event, RfnConditionDataType.COUNT),
                                                       now);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.RESTORE_BLINK;
    }
    
}