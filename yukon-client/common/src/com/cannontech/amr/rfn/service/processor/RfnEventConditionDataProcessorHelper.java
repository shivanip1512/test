package com.cannontech.amr.rfn.service.processor;

import org.joda.time.Instant;
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
    
    public static Long getLongEventData(RfnEvent event, RfnConditionDataType dataType) {
        return (Long) getEventDataWithType(event, dataType);
    }
    
    protected static ChannelData channelDataOf(RfnEvent event) {
        //  Manually create a ChannelData, since NM doesn't provide one yet (see YUK-14688).
        //    This code should eventually move to RfnMeterEventService::handleRfnEventStatusEvents to allow all events to handle ChannelData. 
        ChannelData channelData = new ChannelData();
        
        RfnConditionDataType dataType = RfnConditionDataType.UOM; 
        channelData.setUnitOfMeasure(
                (String) getEventDataWithType(event, dataType));
        
        channelData.setUnitOfMeasureModifiers(
                ((RfnUomModifierSet) getEventDataWithType(event, RfnConditionDataType.UOM_MODIFIERS)).getUomModifiers());
        
        channelData.setValue(
                (Double) getEventDataWithType(event, RfnConditionDataType.MEASURED_VALUE));
        
        channelData.setStatus(
                ChannelDataStatus.OK);
        
        return channelData;

    }
    
    protected static Instant instantOf(RfnEvent event) {
        return new Instant(event.getTimeStamp());
    }

}