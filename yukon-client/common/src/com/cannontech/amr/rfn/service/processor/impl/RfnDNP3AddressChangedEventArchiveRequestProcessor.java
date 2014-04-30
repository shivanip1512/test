package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public class RfnDNP3AddressChangedEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    private final static Logger log = YukonLogManager.getLogger(RfnDNP3AddressChangedEventArchiveRequestProcessor.class);
    
    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event, List<? super PointData> pointDatas) {
        log.info(device + " changed address"
                + " from: " + getEventDataWithType(event, RfnConditionDataType.OLD_DNP3_ADDRESS)
                + " to: " + getEventDataWithType(event, RfnConditionDataType.NEW_DNP3_ADDRESS));
        
        Integer newAddress = (Integer) getEventDataWithType(event, RfnConditionDataType.NEW_DNP3_ADDRESS);
        rfnMeterEventService.processAttributePointData(device, 
                                                       pointDatas, 
                                                       BuiltInAttribute.DNP3_ADDRESS_CHANGED, 
                                                       event.getTimeStamp(), 
                                                       newAddress);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.DNP3_ADDRESS_CHANGED;
    }
    
}
