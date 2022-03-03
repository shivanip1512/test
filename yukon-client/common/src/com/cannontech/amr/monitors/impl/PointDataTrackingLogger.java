package com.cannontech.amr.monitors.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.core.dynamic.RichPointData;

public class PointDataTrackingLogger {

    private static final Duration trackingLogFrequency = Duration.standardMinutes(1);
    private Instant nextTrackingLog = Instant.now().plus(trackingLogFrequency);
    private Set<String> acceptedIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private Set<String> rejectedIds = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private Logger log;
    private String name;
    
    public PointDataTrackingLogger(String name, Logger log) {
        this.name = name;
        this.log = log;
    }
    
    public void acceptId(RichPointData data) {
        processTrackingId(acceptedIds, data);
        logTrackingIds();
    }
    public void rejectId(RichPointData data) {
        processTrackingId(rejectedIds, data);
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
                log.info("{} accepted tracking IDs: {}", name, String.join(" ", acceptedIds));
                log.info("{} rejected tracking IDs: {}", name, String.join(" ", rejectedIds));
            }
            acceptedIds.clear();
            rejectedIds.clear();
        }
    }
}
