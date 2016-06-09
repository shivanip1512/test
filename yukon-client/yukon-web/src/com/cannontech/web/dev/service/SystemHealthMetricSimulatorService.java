package com.cannontech.web.dev.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.systemMetrics.SystemHealthMetric;
import com.cannontech.web.support.systemMetrics.SystemHealthMetricIdentifier;

/**
 * This service is for development testing only. It allows a developer to insert fake values
 * for system health metrics.
 */
public class SystemHealthMetricSimulatorService {
    private static final Logger log = YukonLogManager.getLogger(SystemHealthMetricSimulatorService.class);
    private boolean isActive;
    private Map<SystemHealthMetricIdentifier, Map<SystemHealthMetricMethod, Object>> metricValues = new ConcurrentHashMap<>();
    
    /**
     * "Start" the service. From this point forward, requests for metric data will use the fake values.
     */
    public void start() {
        isActive = true;
    }
    
    /**
     * "Stop" the service. Fake values will no longer be provided for metric data requests.
     */
    public void stop() {
        isActive = false;
    }
    
    /**
     * @return True if the service is actively providing fake metric data for requests.
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Set a fake value to be used for the specified method on the given metric.
     */
    public void setValue(SystemHealthMetricIdentifier metricId, SystemHealthMetricMethod method, Object value) {
        if (!metricValues.containsKey(metricId)) {
            metricValues.put(metricId, new ConcurrentHashMap<>());
        }
        metricValues.get(metricId).put(method, value);
    }
    
    /**
     * Remove any existing value for the specified method and metric.
     */
    public void removeValue(SystemHealthMetricIdentifier metricId, SystemHealthMetricMethod method) {
        if (metricValues.containsKey(metricId) && metricValues.get(metricId).containsKey(method)) {
            metricValues.get(metricId).remove(method);
        }
    }
    
    /**
     * @return The fake value being used for the specified metric and method, or null if no value has been set for that
     * metric and method.
     */
    public Object getValue(SystemHealthMetricIdentifier metricId, SystemHealthMetricMethod method) {
        if (metricValues.containsKey(metricId)) {
            return metricValues.get(metricId).get(method);
        }
        return null;
    }
    
    /**
     * @return The complete map of values for metrics and methods.
     */
    public Map<SystemHealthMetricIdentifier, Map<SystemHealthMetricMethod, Object>> getAllValues() {
        return metricValues;
    }
    
    /**
     * @return True if a fake value has been specified for the given metric and method.
     */
    public boolean hasValue(SystemHealthMetricIdentifier metricId, String methodName) {
        if (metricValues.containsKey(metricId)) {
            return metricValues.get(metricId).containsKey(methodName);
        }
        return false;
    }
    
    /**
     * Replaces the real values in the provided SystemHealthMetric with any fake values that have been specified and
     * apply to this type of SystemHealthMetric.
     */
    public void applyFakeMetrics(SystemHealthMetric metric) {
        Map<SystemHealthMetricMethod, Object> thisMetricValues = metricValues.get(metric.getMetricIdentifier());
        if (thisMetricValues == null) {
            return;
        }
        for (Entry<SystemHealthMetricMethod, Object> entry : thisMetricValues.entrySet()) {
            SystemHealthMetricMethod methodInfo = entry.getKey();
            Object fakeMetricValue = entry.getValue();
            
            //Only apply the fake value if the metric supports that value type
            if (SystemHealthMetricMethod.getMethodsForMetricType(metric.getType()).contains(methodInfo) && fakeMetricValue != null) {
                try {
                    Method method = metric.getClass().getMethod(methodInfo.getMethodName(), methodInfo.getParameterTypes());
                    
                    method.invoke(metric, fakeMetricValue);
                } catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException | ClassCastException e) {
                    log.error("Error applying fake metric. Metric: " + metric.getMetricIdentifier() + ", Method: " 
                              + methodInfo + ", Value: " + fakeMetricValue + "(" + fakeMetricValue.getClass() + ")", e);
                }
            }
        }
    }
}
