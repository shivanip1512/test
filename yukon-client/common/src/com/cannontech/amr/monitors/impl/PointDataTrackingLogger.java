package com.cannontech.amr.monitors.impl;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.core.dynamic.RichPointData;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class PointDataTrackingLogger {

    private static final Duration trackingLogFrequency = Duration.standardMinutes(1);
    private Instant nextTrackingLog = Instant.now().plus(trackingLogFrequency);
    private Multimap<String, String> acceptedIds = ArrayListMultimap.create();
    private Multimap<String, String> rejectedIds = ArrayListMultimap.create();
    private Logger log;
    
    public PointDataTrackingLogger(Logger log) {
        this.log = log;
    }
    
    public void acceptId(String monitorName, RichPointData data) {
        processTrackingId(acceptedIds.get(monitorName), data);
        logTrackingIds();
    }
    public void rejectId(String monitorName, RichPointData data) {
        processTrackingId(rejectedIds.get(monitorName), data);
        logTrackingIds();
    }
    
    private static void processTrackingId(Collection<String> ids, RichPointData data) {
        String trackingId = data.getPointValue().getTrackingId();
        if (StringUtils.isNotEmpty(trackingId)) {
            ids.add(trackingId);
        }
    }

    private void logTrackingIds() {
        if (Instant.now().isAfter(nextTrackingLog)) {
            nextTrackingLog = Instant.now().plus(trackingLogFrequency);
            if (log.isInfoEnabled()) {
                acceptedIds.asMap().forEach((monitorName, ids) -> 
                    log.info("{} accepted tracking IDs: {}", monitorName, String.join(" ", ids)));
                rejectedIds.asMap().forEach((monitorName, ids) -> 
                    log.info("{} rejected tracking IDs: {}", monitorName, String.join(" ", ids)));
            }
            acceptedIds.clear();
            rejectedIds.clear();
        }
    }
}
