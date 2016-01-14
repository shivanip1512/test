package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.db.point.stategroup.EventStatus;
import com.cannontech.message.dispatch.message.PointData;

public class RfnDNP3AddressChangedEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    private final static Logger log = YukonLogManager.getLogger(RfnDNP3AddressChangedEventArchiveRequestProcessor.class);
    
    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        Instant eventInstant = instantOf(event);
        
        log.info(device + " changed address"
                + " from: " + getEventDataWithType(event, RfnConditionDataType.OLD_DNP3_ADDRESS)
                + " to: " + getEventDataWithType(event, RfnConditionDataType.NEW_DNP3_ADDRESS)
                + " at " + eventInstant);
        
        rfnMeterEventService.processAttributePointData(device, 
                                                       pointDatas, 
                                                       BuiltInAttribute.DNP3_ADDRESS_CHANGED, 
                                                       eventInstant, 
                                                       EventStatus.ACTIVE.getRawState(),
                                                       now);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.DNP3_ADDRESS_CHANGED;
    }
    
}
