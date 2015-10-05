package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.service.RfnChannelDataConverter;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public abstract class RfnVoltageEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {

    @Autowired private RfnChannelDataConverter converter;

    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event,
                                             List<? super PointData> pointDatas) {

        ChannelData channelData = of(event);
                
        pointDatas.add(converter.convert(device, channelData, new Instant(event.getTimeStamp())));
    }
}