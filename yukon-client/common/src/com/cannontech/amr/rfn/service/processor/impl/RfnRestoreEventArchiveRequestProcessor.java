package com.cannontech.amr.rfn.service.processor.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.amr.rfn.service.processor.RfnArchiveRequestProcessor;
import com.cannontech.amr.rfn.service.processor.RfnEventConditionDataProcessorHelper;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.InvalidEventMessageException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.message.dispatch.message.PointData;

public class RfnRestoreEventArchiveRequestProcessor extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {
    
    static final DateTime y2k = new LocalDate(2000, 1, 1).toDateTimeAtStartOfDay();
    static final Duration year = Duration.standardDays(365);

    @Override
    public <T extends RfnEvent> void process(RfnDevice device, T event, List<? super PointData> pointDatas) {
        
        long eventTimestamp = event.getTimeStamp();
        PointQuality pointQuality = PointQuality.Normal;
        
        Instant eventTime = new Instant(eventTimestamp);
        Instant now = Instant.now();
        
        // Set time to now if unset or greater than one year before or after now.
        // The outage was too long, and the firmware didn'tknow what the real time was.
        if (eventTimestamp == 0) {
            eventTimestamp = now.getMillis();
            pointQuality = PointQuality.Estimated;
        }
        // Bad timestamp - do not process this record
        else if (eventTime.isAfter(now.plus(year)) || eventTime.isBefore(y2k)) {
            return;
        }

        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.OUTAGE_STATUS,
            eventTimestamp, OutageStatus.GOOD.getRawState(), pointQuality);
        
        if (event.getTimeStamp() != 0) { // do not process Outage Log when actual eventTimestamp unknown.
            Long durationInSeconds = RfnInvalidValues.OUTAGE_DURATION.getValue();
            PointQuality outageLogPointQuality = PointQuality.Unknown;
            try {
                Long start = (Long) getEventDataWithType(event, RfnConditionDataType.EVENT_START_TIME);
                Long end = eventTimestamp;
                durationInSeconds = (end - start) / 1000;
                outageLogPointQuality = PointQuality.Normal;
            } catch (InvalidEventMessageException e) {
                // Old firmware doesn't include the EVENT_START_TIME meta-data, so just use the invalid value set above for the duration if we get here
            }
            rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.OUTAGE_LOG, eventTimestamp, durationInSeconds, outageLogPointQuality);
        }
        
        Long count = (Long) getEventDataWithType(event, RfnConditionDataType.COUNT);
        rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.RFN_OUTAGE_RESTORE_COUNT, eventTimestamp, count, pointQuality);
    }
    
    @Override
    public RfnConditionType getRfnConditionType() {
        return RfnConditionType.RESTORE;
    }
    
}