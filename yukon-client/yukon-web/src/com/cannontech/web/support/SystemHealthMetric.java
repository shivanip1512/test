package com.cannontech.web.support;

import com.cannontech.common.i18n.Displayable;

/**
 * Interface implemented by system metric data objects.
 */
public interface SystemHealthMetric extends Displayable {
    
    /**
     * @return the metric type. All metrics of a given type are composed of the same set of data points.
     */
    public SystemHealthMetricType getType();
    
    /**
     * @return the unique metric identifier.
     */
    public SystemHealthMetricIdentifier getMetricIdentifier();
}
