package com.cannontech.web.support.systemMetrics;


/**
 * Interface for objects that measure the 'healthiness' of a metric.
 */
public interface MetricHealthCriteria {
    
    /**
     * @return The identifier of the metric that this criteria pertains to.
     */
    public SystemHealthMetricIdentifier getPertainsTo();
    
    /**
     * @return The metric status, based on an evaluation of the specified metric data. If the status is WARN or ERROR,
     * one or more messages will be included to explain why the metric is 'unhealthy'.
     */
    public MetricStatusWithMessages checkMetric(SystemHealthMetric metric);

}
