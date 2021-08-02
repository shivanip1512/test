package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.events.loggers.RfnDeviceEventLogService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public class CellularApnChangedEventProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {

    @Autowired private RfnDeviceEventLogService rfnDeviceEventLogService;

    @Override
    public void process(RfnDevice device, RfnEvent event,
            List<? super PointData> pointDatas, Instant now) {
        var apn = (String) getEventDataWithType(event, RfnConditionDataType.APN);
        rfnDeviceEventLogService.apnChanged(device.getName(), device.getRfnIdentifier(), apn);
    }

    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.CELLULAR_APN_CHANGED;
    }
}