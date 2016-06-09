package com.cannontech.web.support.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.web.support.service.SystemHealthService;
import com.cannontech.web.support.systemMetrics.MetricHealthCriteria;
import com.cannontech.web.support.systemMetrics.MetricStatus;
import com.cannontech.web.support.systemMetrics.MetricStatusWithMessages;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A sub-service to SystemHealthService. Periodically calculates the status of every system health metric by evaluating
 * them against all pertinent MetricHealthCriteria, then caches the status for instantaneous lookup.
 */
public class SystemHealthStatusHelper {
    private static final Logger log = YukonLogManager.getLogger(SystemHealthStatusHelper.class);
    private static final MetricStatusWithMessages unknownStatus = new MetricStatusWithMessages(MetricStatus.UNKNOWN, new ArrayList<>());
    private static final MetricStatusWithMessages defaultGoodStatus = new MetricStatusWithMessages(MetricStatus.GOOD, new ArrayList<>());
    private static final int updateFrequencyMinutes = 5; //TODO: configurable?
    
    private final ScheduledExecutor executor;
    private final SystemHealthService systemHealthService;
    private final Multimap<SystemHealthMetricIdentifier, MetricHealthCriteria> metricHealthCriteriaByMetricId = ArrayListMultimap.create();
    
    private final Map<SystemHealthMetricIdentifier, MetricStatusWithMessages> metricStatusCache = new ConcurrentHashMap<>();
    
    public SystemHealthStatusHelper(SystemHealthService systemHealthService, ScheduledExecutor executor, List<MetricHealthCriteria> metricHealthCriteria) {
        this.systemHealthService = systemHealthService;
        this.executor = executor;
        
        for (MetricHealthCriteria criteria : metricHealthCriteria) {
            metricHealthCriteriaByMetricId.put(criteria.getPertainsTo(), criteria);
        }
        
        init();
        log.debug("Initialized SystemHealthStatusHelper.");
    }
    
    /**
     * Initializes the metric status cache to default values (unknown status) and starts the executor that periodically
     * evaluates statuses for all metrics and updates the cache.
     */
    private void init() {
        // Set all metric statuses to "unknown" on startup, in case they're requested before the first calculation is complete.
        for (SystemHealthMetricIdentifier metric : SystemHealthMetricIdentifier.values()) {
            metricStatusCache.put(metric, unknownStatus);
        }
        
        Runnable metricUpdater = () -> {
            log.debug("Updating system health metric statuses.");
            
            List<SystemHealthMetric> metrics = systemHealthService.getAllMetrics();
            for (SystemHealthMetric metric : metrics) {
                MetricStatusWithMessages status = calculateStatus(metric);
                metric.setStatus(status);
                metricStatusCache.put(metric.getMetricIdentifier(), status);
                //archivePointData(metric); //TODO: YUK-15285
                
                log.debug(metric.getType() + " status calculation complete.");
                log.trace(metric.getType() + "Status: " + status);
            }
        };
        
        executor.scheduleAtFixedRate(metricUpdater, 0, updateFrequencyMinutes, TimeUnit.MINUTES);
    }
    
    /**
     * Calculate the status of the specified metric by evaluating current values against all pertinent criteria. If
     * there are multiple criteria to evaluate, the status returned will be the "worst" status returned by any criteria,
     * and the messages will contain i18ned error messages for every status that was WARN or ERROR.
     */
    public MetricStatusWithMessages calculateStatus(SystemHealthMetric metric) {
        Collection<MetricHealthCriteria> pertinentCriteria = metricHealthCriteriaByMetricId.get(metric.getMetricIdentifier());
        
        MetricStatusWithMessages status = defaultGoodStatus;
        
        for (MetricHealthCriteria criteria : pertinentCriteria) {
            MetricStatusWithMessages newStatus = criteria.checkMetric(metric);
            status = status.merge(newStatus);
        }
        
        return status;
    }
    
    /**
     * Get the latest status for the specified metric. (Statuses are evaluated periodically, not when this method is called.)
     */
    public MetricStatusWithMessages getStatus(SystemHealthMetricIdentifier metric) {
        return metricStatusCache.get(metric);
    }
}
