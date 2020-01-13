package com.cannontech.amr.rfn.service.processor;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.event.RfnEvent;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.model.InvalidEventMessageException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.message.dispatch.message.PointData;

public abstract class RfnOutageLogEventConditionDataProcessorHelper extends RfnEventConditionDataProcessorHelper
        implements RfnArchiveRequestProcessor {

    public static final Logger log = YukonLogManager.getLogger(RfnOutageLogEventConditionDataProcessorHelper.class);

    protected void processOutageLog(RfnDevice device, RfnEvent event, List<? super PointData> pointDatas, Instant now,
            Instant eventInstant, RfnConditionType rfnConditionType) {
        try {
            Long start;
            Long end;
            if (rfnConditionType == RfnConditionType.OUTAGE_BLINK) {
                start = eventInstant.getMillis();
                end = getLongEventData(event, RfnConditionDataType.EVENT_END_TIME);
            } else {
                start = getLongEventData(event, RfnConditionDataType.EVENT_START_TIME);
                end = eventInstant.getMillis();
            }

            var eventStart = new Instant(start);
            Long durationInSeconds = (end - start) / 1000;
            rfnMeterEventService.processAttributePointData(device, pointDatas, BuiltInAttribute.OUTAGE_LOG, eventStart,
                    durationInSeconds, PointQuality.Normal, now);

            log.debug("OutageLog processed {} for Device: {} Event: {} Start: {} End: {} Duration: {} ", rfnConditionType, device,
                    event, eventStart,
                    eventInstant, new Instant(durationInSeconds));
        } catch (InvalidEventMessageException e) {
            // Old firmware and "compact aggregated restoration alarms" don't include the EVENT_START_TIME
            // meta-data, so don't create the outage log if we get here
            log.trace(device + " " + getRfnConditionType() + " not creating OUTAGE_LOG entry", e);
        }
    }
}