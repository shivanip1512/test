package com.cannontech.amr.monitors.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.core.dynamic.RichPointData;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class PointDataTrackingLogger {

    private static final Duration trackingLogFrequency = Duration.standardSeconds(30);
    private Instant nextTrackingLog = Instant.now().plus(trackingLogFrequency);
    private Multimap<Boolean, String> trackingIds = ArrayListMultimap.create();
    private Logger log;
    
    public PointDataTrackingLogger(Logger log) {
        this.log = log;
    }
    
    public void acceptId(RichPointData data) {
        processTrackingId(data, true);
    }
    public void rejectId(RichPointData data) {
        processTrackingId(data, false);
    }
    
    private void processTrackingId(RichPointData data, boolean accepted) {
        String trackingId = data.getPointValue().getTrackingId();
        if (StringUtils.isNotEmpty(trackingId)) {
            trackingIds.put(accepted, trackingId);
        }
        
        if (Instant.now().isAfter(nextTrackingLog)) {
            nextTrackingLog = Instant.now().plus(trackingLogFrequency);

            logTrackingIds(trackingIds);
            
            trackingIds.clear();
        }
    }

    private void logTrackingIds(Multimap<Boolean, String> trackingIds) {
        if (log.isInfoEnabled()) {
            if (trackingIds.containsKey(true)) {
                log.info("Tracking IDs accepted: " + String.join(" ", trackingIds.get(true)));
            }
            if (trackingIds.containsKey(false)) {
                log.info("Tracking IDs rejected: " + String.join(" ", trackingIds.get(false)));
            }
        }
    }
}
