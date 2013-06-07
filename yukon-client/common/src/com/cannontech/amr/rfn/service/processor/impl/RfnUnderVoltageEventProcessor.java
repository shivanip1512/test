package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.messaging.message.dispatch.PointDataMessage;

public class RfnUnderVoltageEventProcessor extends RfnEventConditionDataProcessorHelper implements RfnArchiveRequestProcessor {

    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event, List<? super PointDataMessage> pointDatas) {
        final Long threshold = (Long) getEventDataWithType(event, RfnConditionDataType.THRESHOLD_VALUE);
        final Long measured = (Long) getEventDataWithType(event, RfnConditionDataType.MEASURED_VALUE);

        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.UNDER_VOLTAGE_THRESHOLD, event.getTimeStamp(), threshold, PointQuality.Normal);
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.UNDER_VOLTAGE_MEASURED, event.getTimeStamp(), measured, PointQuality.Normal);
    }

    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.UNDER_VOLTAGE;
    }
}