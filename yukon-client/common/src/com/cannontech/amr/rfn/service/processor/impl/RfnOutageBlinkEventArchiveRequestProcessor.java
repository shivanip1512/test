package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public class RfnOutageBlinkEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    public static final Logger log = YukonLogManager.getLogger(RfnOutageBlinkEventArchiveRequestProcessor.class);
    
    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        log.debug("Outage Blink event received for archiving");
        rfnMeterEventService.processAttributePointData(device, 
                                                       pointDatas, 
                                                       BuiltInAttribute.RFN_BLINK_COUNT, 
                                                       instantOf(event), 
                                                       getLongEventData(event, RfnConditionDataType.COUNT),
                                                       now);

        // The OUTAGE_BLINK event may contain RfnConditionDataType.EVENT_END_TIME (time outage ended), but we do not use it at present.
        // The outage log for blinks is created by RfnRestoreBlinkEventArchiveRequestProcessor. 
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.OUTAGE_BLINK;
    }
    
}