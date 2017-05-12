package com.cannontech.web.support.service;

import java.util.Collection;
import java.util.List;

import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;
import com.cannontech.web.support.systemMetrics.criteria.MetricHealthCriteria;
import com.google.common.collect.Multimap;

/**
 * Service that handles system health metrics.
 */
public interface SystemHealthService {
    
    /**
     * Retrieves the current data for the specified metric.
     */
    SystemHealthMetric getMetric(SystemHealthMetricIdentifier metric);
    
    /**
     * Retrieves the current data for all metrics.
     */
    List<SystemHealthMetric> getAllMetrics();
    
    /**
     * Retrieves the current data for all metrics of the specified type.
     */
    List<SystemHealthMetric> getMetricsByType(SystemHealthMetricType type);
    
    /**
     * Retrieves the current data for all metrics with the specified identifiers.
     */
    Multimap<SystemHealthMetricType, SystemHealthMetric> getMetricsByIdentifiers(Collection<SystemHealthMetricIdentifier> metricIds);
    
    /**
     * Retrieves all MetricHealthCriteria that pertain to the specified metric.
     */
    Collection<MetricHealthCriteria> getPertinentCriteria(SystemHealthMetricIdentifier metricId);
    
}
