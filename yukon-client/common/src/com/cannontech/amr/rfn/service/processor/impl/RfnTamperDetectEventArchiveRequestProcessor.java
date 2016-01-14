package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public class RfnTamperDetectEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {

    @Override
    public void process(RfnDevice device, RfnEvent event,
                        List<? super PointData> pointDatas, Instant now) {
        int rawClearedState = rfnMeterEventService.getRawClearedStateForEvent(event);
        rfnMeterEventService.processAttributePointData(device,
                                                       pointDatas,
                                                       BuiltInAttribute.TAMPER_FLAG,
                                                       instantOf(event),
                                                       rawClearedState,
                                                       now);
    }

    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.TAMPER_DETECT;
    }

}