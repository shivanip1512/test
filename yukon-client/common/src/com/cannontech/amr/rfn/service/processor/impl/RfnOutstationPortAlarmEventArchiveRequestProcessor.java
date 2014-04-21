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

public class RfnOutstationPortAlarmEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    private final static Logger log = YukonLogManager.getLogger(RfnOutstationPortAlarmEventArchiveRequestProcessor.class);
    
    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event, List<? super PointData> pointDatas) {
        log.info(device.toString() 
                 + " port type: " + getEventDataWithType(event, RfnConditionDataType.PORT_TYPE)
                 + " locked for " + getEventDataWithType(event, RfnConditionDataType.PORT_LOCKED_MINUTES) + "minutes");
        
        Long lockedMinutes = (Long) getEventDataWithType(event, RfnConditionDataType.PORT_LOCKED_MINUTES);
        rfnMeterEventService.processAttributePointData(device, 
                                                       pointDatas, 
                                                       BuiltInAttribute.OUTSTATION_DNP3_SERCOMM_LOCKED, 
                                                       event.getTimeStamp(), 
                                                       lockedMinutes);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.OUTSTATION_DNP3_SERCOMM_LOCKED;
    }
    
}
