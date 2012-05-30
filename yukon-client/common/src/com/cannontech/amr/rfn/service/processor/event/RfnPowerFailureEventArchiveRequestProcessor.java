package com.cannontech.amr.rfn.service.processor.event;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessorBase;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public class RfnPowerFailureEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessorBase {

    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event,
                                             List<? super PointData> pointDatas) {
        int rawClearedState = rfnMeterEventService.getRawClearedStateForEvent(event);
        rfnMeterEventService.processAttributePointData(device,
                                                       pointDatas,
                                                       BuiltInAttribute.POWER_FAIL_FLAG,
                                                       event.getTimeStamp(),
                                                       rawClearedState);
    }

    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.POWER_FAILURE;
    }

}