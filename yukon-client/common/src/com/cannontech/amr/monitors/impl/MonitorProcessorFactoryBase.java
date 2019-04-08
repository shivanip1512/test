package com.cannontech.amr.monitors.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.core.monitors.RichPointDataListenerFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public abstract class MonitorProcessorFactoryBase<T extends PointMonitor> implements
        RichPointDataListenerFactory {

    private static final Duration trackingLogFrequency = Duration.standardSeconds(30);
    private Instant nextTrackingLog = Instant.now().plus(trackingLogFrequency);
    private Multimap<Boolean, String> trackingIds = ArrayListMultimap.create();

    @Override
    public List<RichPointDataListener> createListeners() {
        List<T> allMonitors = getAllMonitors();
        List<RichPointDataListener> result = Lists.newArrayListWithExpectedSize(allMonitors.size());
        for (T monitor : allMonitors) {
            if (monitor.getEvaluatorStatus() == MonitorEvaluatorStatus.ENABLED) {
                RichPointDataListener pointDataListner = createPointListener(monitor);
                result.add(pointDataListner);
            }
        }
        return result;
    }

    protected abstract List<T> getAllMonitors();

    protected abstract RichPointDataListener createPointListener(T monitor);

    protected void acceptTrackingId(RichPointData data) {
        processTrackingId(data, true);
    }
    protected void rejectTrackingId(RichPointData data) {
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

    protected void logTrackingIds(Multimap<Boolean, String> trackingIds) {
        var log = getTrackingLogger();
        
        if (log != null && log.isInfoEnabled()) {
            if (trackingIds.containsKey(true)) {
                log.info("Tracking IDs accepted: " + String.join(" ", trackingIds.get(true)));
            }
            if (trackingIds.containsKey(false)) {
                log.info("Tracking IDs rejected: " + String.join(" ", trackingIds.get(false)));
            }
        }
    }
    
    protected Logger getTrackingLogger() {
        return null;
    }
}
