package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.alarm.RfnAlarm;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.service.processor.RfnOutageLogEventConditionDataProcessorHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.InvalidEventMessageException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.message.dispatch.message.PointData;

public class RfnRestoreEventArchiveRequestProcessor extends RfnOutageLogEventConditionDataProcessorHelper {
    
    private final static Logger log = YukonLogManager.getLogger(RfnRestoreEventArchiveRequestProcessor.class);

    @Override
    public void process(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now) {
        
        Instant eventInstant = instantOf(event);
        PointQuality pointQuality = PointQuality.Normal;
        
        if (eventInstant.getMillis() == 0) {  
            //outage was too long, and firmware unable to know what time really is
            // adjust the eventTimestamp to "now" and estimated quality
            eventInstant = now;
            pointQuality = PointQuality.Estimated;
        } else { 
            // only process Outage Log when actual eventTimestamp known.
            processOutageLog(device, event, pointDatas, now, eventInstant, getRfnConditionType());
        }
        
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.OUTAGE_STATUS, eventInstant, OutageStatus.GOOD.getRawState(), pointQuality, now);
        
        try {
            rfnMeterEventService.processAttributePointData(device, 
                                                           pointDatas, 
                                                           BuiltInAttribute.RFN_OUTAGE_RESTORE_COUNT, 
                                                           eventInstant, 
                                                           getLongEventData(event, RfnConditionDataType.COUNT), 
                                                           pointQuality, 
                                                           now);
        } catch (InvalidEventMessageException ex) {
            if (event instanceof RfnAlarm) {
                log.trace(device + " restore alarm received with no COUNT, not sending RFN_OUTAGE_RESTORE_COUNT update");
            } else {
                throw ex;
            }
        }
    }

    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.RESTORE;
    }
}