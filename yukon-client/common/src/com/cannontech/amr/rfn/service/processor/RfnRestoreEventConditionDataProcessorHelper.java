package com.cannontech.amr.rfn.service.processor;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.InvalidEventMessageException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public abstract class RfnRestoreEventConditionDataProcessorHelper extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {

    public static final Logger log = YukonLogManager.getLogger(RfnRestoreEventConditionDataProcessorHelper.class);

    protected void processOutageLog(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now, Instant eventInstant) {
        try {
            Long start = getLongEventData(event, RfnConditionDataType.EVENT_START_TIME);
            Long end = eventInstant.getMillis();
            var eventStart = new Instant(start);
            Long durationInSeconds = (end - start) / 1000;
            log.debug("Preparing to process OutageLog, Device: " + device + "Event: " + event + "Start: " + eventStart + " End: " + eventInstant + " Duration: " + new Instant(durationInSeconds));
            rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.OUTAGE_LOG, eventStart, durationInSeconds, PointQuality.Normal, now);
            log.debug("OutageLog processed successfully");
        } catch (InvalidEventMessageException e) {
            // Old firmware and "compact aggregated restoration alarms" don't include the EVENT_START_TIME
            // meta-data, so don't create the outage log if we get here
            log.debug(device + " " + getRfnConditionType() + " not creating OUTAGE_LOG entry", e);
        }
    }
}