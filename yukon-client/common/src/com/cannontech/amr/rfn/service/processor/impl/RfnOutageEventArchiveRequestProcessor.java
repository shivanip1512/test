package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.alarm.RfnAlarm;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.RfnDataValidator;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.InvalidEventMessageException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.message.dispatch.message.PointData;

public class RfnOutageEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    private final static Logger log = YukonLogManager.getLogger(RfnOutageEventArchiveRequestProcessor.class);

    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {

        Instant eventInstant = instantOf(event);
        PointQuality quality = PointQuality.Normal;
        
        if(!RfnDataValidator.isTimestampValid(eventInstant, now)) {
            //  Bad timestamp - but since this is an alarm (likely occurring now), try sending current time with an Estimated quality
            if(event instanceof RfnAlarm) {
                log.warn(device + " invalid timestamp " + eventInstant + " for alarm " + event.toString() + ", sending current time as estimate");
                eventInstant = now;
                quality = PointQuality.Estimated;
            }
        }
        
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.OUTAGE_STATUS, eventInstant, OutageStatus.BAD.getRawState(), quality, now);

        try {
            rfnMeterEventService.processAttributePointData(device, 
                                                           pointDatas, 
                                                           BuiltInAttribute.RFN_OUTAGE_COUNT, 
                                                           eventInstant, 
                                                           getLongEventData(event, RfnConditionDataType.COUNT), 
                                                           quality, 
                                                           now);
        } catch (InvalidEventMessageException ex) {
            if (event instanceof RfnAlarm) {
                log.warn("Invalid Event Message device:" + device + " event:" + event + " pointDatas:" + pointDatas
                    + ". Outage alarm received with no COUNT, not sending RFN_OUTAGE_COUNT update", ex);
            } else {
                log.error("Invalid Event Message device:" + device + " event:" + event + " pointDatas:" + pointDatas,
                    ex);
                throw ex;
            }
        }
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.OUTAGE;
    }
    
}
