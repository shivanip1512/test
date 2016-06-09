package com.cannontech.web.dev;

import java.util.Map;

import com.cannontech.web.dev.service.SystemHealthMetricMethod;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

public class MetricSimulatorValues {
    private boolean active = false;
    private Map<SystemHealthMetricIdentifier, Map<SystemHealthMetricMethod, Object>> metricValues;
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Map<SystemHealthMetricIdentifier, Map<SystemHealthMetricMethod, Object>> getMetricValues() {
        return metricValues;
    }
    
    public void setMetricValues(Map<SystemHealthMetricIdentifier, Map<SystemHealthMetricMethod, Object>> metricValues) {
        this.metricValues = metricValues;
    }
    
}
