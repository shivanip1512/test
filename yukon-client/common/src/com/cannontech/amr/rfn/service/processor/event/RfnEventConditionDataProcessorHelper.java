package com.cannontech.amr.rfn.service.processor.event;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.RfnMeterEventService;
import com.cannontech.common.rfn.model.InvalidEventMessageException;

public class RfnEventConditionDataProcessorHelper {

    @Autowired protected RfnMeterEventService rfnMeterEventService;

    public Object getEventDataWithType(RfnEvent event, RfnConditionDataType dataType) {
        if (event.getEventData() == null || event.getEventData().get(dataType) == null) {
            throw new InvalidEventMessageException("Missing " + dataType + " event data for " + event.getRfnIdentifier());
        }
        return event.getEventData().get(dataType);
    }

}