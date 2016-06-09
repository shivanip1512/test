package com.cannontech.web.dev.service;

import java.util.Collection;

import com.cannontech.web.support.systemMetrics.SystemHealthMetricType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * Enum to describe the methods available for SystemHealthMetrics.
 */
public enum SystemHealthMetricMethod {
    ARCHIVED_READINGS_COUNT("setArchivedReadingsCount", Integer.class),
    ARCHIVED_REQUESTS_PROCESSED("setArchiveRequestsProcessed", Integer.class),
    ENQUEUED_COUNT("setEnqueuedCount", Long.class),
    DEQUEUED_COUNT("setDequeuedCount", Long.class),
    QUEUE_SIZE("setQueueSize", Long.class),
    AVERAGE_ENQUEUE_TIME("setAverageEnqueueTime", Double.class);
    
    private final String methodName;
    private final Class<?>[] parameterTypes;
    
    private SystemHealthMetricMethod(String methodName, Class<?>... parameterTypes) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }
    
    /**
     * @return The string representation of the method, suitable for reflexively invoking the method.
     */
    public String getMethodName() {
        return methodName;
    }
    
    /**
     * @return The Classes of the method's parameters, suitable for reflexively invoking the method.
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
    
    private static final Multimap<SystemHealthMetricType, SystemHealthMetricMethod> metricToMethods;
    
    static {
        metricToMethods = ArrayListMultimap.create();
        metricToMethods.putAll(
            SystemHealthMetricType.JMS_QUEUE,
                ImmutableSet.of(
                   ENQUEUED_COUNT,
                   DEQUEUED_COUNT,
                   QUEUE_SIZE,
                   AVERAGE_ENQUEUE_TIME
        ));
        metricToMethods.putAll(
            SystemHealthMetricType.JMS_QUEUE_EXTENDED,
                ImmutableSet.of(
                   ARCHIVED_READINGS_COUNT, 
                   ARCHIVED_REQUESTS_PROCESSED,
                   ENQUEUED_COUNT,
                   DEQUEUED_COUNT,
                   QUEUE_SIZE,
                   AVERAGE_ENQUEUE_TIME
        ));
    }
    
    /**
     * @return A Collection of all the methods supported by the given SystemHealthMetricType.
     */
    public static Collection<SystemHealthMetricMethod> getMethodsForMetricType(SystemHealthMetricType type) {
        return metricToMethods.get(type);
    }
}
