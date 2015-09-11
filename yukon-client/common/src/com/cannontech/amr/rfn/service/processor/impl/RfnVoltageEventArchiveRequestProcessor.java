package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public abstract class RfnVoltageEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {

    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event,
                                             List<? super PointData> pointDatas) {
        
        Double voltage = (Double) getEventDataWithType(event, RfnConditionDataType.MEASURED_VALUE);
        rfnMeterEventService.processAttributePointData(device,
                                                       pointDatas,
                                                       BuiltInAttribute.VOLTAGE,
                                                       event.getTimeStamp(),
                                                       voltage);
    }
}