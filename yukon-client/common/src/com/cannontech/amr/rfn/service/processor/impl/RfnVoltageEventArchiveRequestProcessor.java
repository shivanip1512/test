package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.message.event.RfnUomModifierSet;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
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

        //  Manually create a ChannelData, since NM doesn't provide one yet (see YUK-14688).
        //    This code should eventually move to RfnMeterEventService::handleRfnEventStatusEvents to allow all events to handle ChannelData. 
        ChannelData channelData = new ChannelData();
        
        channelData.setUnitOfMeasure(
                (String) getEventDataWithType(event, RfnConditionDataType.UOM));
        
        channelData.setUnitOfMeasureModifiers(
                ((RfnUomModifierSet) getEventDataWithType(event, RfnConditionDataType.UOM_MODIFIERS)).getUomModifiers());
        
        channelData.setValue(
                (Double) getEventDataWithType(event, RfnConditionDataType.MEASURED_VALUE));
        
        channelData.setStatus(
                ChannelDataStatus.OK);
                
        pointDatas.add(
                converter.convertSingleChannelData(device, channelData, new Instant(event.getTimeStamp()))
                         .getPointData());
    }
}