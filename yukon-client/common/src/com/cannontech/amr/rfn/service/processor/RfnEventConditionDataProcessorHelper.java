package com.cannontech.amr.rfn.service.processor;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.message.event.RfnUomModifierSet;
import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.service.RfnMeterEventService;
import com.cannontech.common.rfn.model.InvalidEventMessageException;

public class RfnEventConditionDataProcessorHelper {

    @Autowired protected RfnMeterEventService rfnMeterEventService;

    public static Object getEventDataWithType(RfnEvent event, RfnConditionDataType dataType) {
        if (event.getEventData() == null || event.getEventData().get(dataType) == null) {
            throw new InvalidEventMessageException("Missing " + dataType + " event data for " + event.getRfnIdentifier());
        }
        return event.getEventData().get(dataType);
    }
    
    protected static ChannelData of(RfnEvent event) {
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
        
        return channelData;

    }

}