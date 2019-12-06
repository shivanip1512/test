package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnRestoreEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public class RfnRestoreBlinkEventArchiveRequestProcessor extends RfnRestoreEventConditionDataProcessorHelper {
    
    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        var eventInstant = instantOf(event);
        
        rfnMeterEventService.processAttributePointData(device, 
                                                       pointDatas, 
                                                       BuiltInAttribute.RFN_BLINK_RESTORE_COUNT, 
                                                       eventInstant, 
                                                       getLongEventData(event, RfnConditionDataType.COUNT),
                                                       now);
        
        processOutageLog(device, event, pointDatas, now, eventInstant);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.RESTORE_BLINK;
    }
    
}