package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.MeterConfigurationStatus;
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

public class RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    private static final Logger log = YukonLogManager.getLogger(RfnRemoteMeterConfigurationFailureEventArchiveRequestProcessor.class);
    
    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        Instant eventInstant = instantOf(event);
        
        var meterConfigurationId = (String) getEventDataWithType(event, RfnConditionDataType.METER_CONFIGURATION_ID);
        var meterConfigurationStatus = (MeterConfigurationStatus) getEventDataWithType(event, RfnConditionDataType.METER_CONFIGURATION_STATUS); 
        
        // TODO - this will need to update the Remote Configuration status table
        log.info(" Remote Meter Configuration failed for device=" + device + ", meterConfigurationId=" + meterConfigurationId + ", status = " + meterConfigurationStatus);
        
        rfnMeterEventService.processAttributePointData(device, 
                                                       pointDatas, 
                                                       BuiltInAttribute.REMOTE_METER_CONFIGURATION_FAILURE, 
                                                       eventInstant, 
                                                       EventStatus.ACTIVE.getRawState(),
                                                       now);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.REMOTE_METER_CONFIGURATION_FAILURE;
    }
}
