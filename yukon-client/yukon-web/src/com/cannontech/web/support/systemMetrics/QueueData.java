package com.cannontech.web.support.systemMetrics;

import java.text.DecimalFormat;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.i18n.YukonMessageSourceResolvable;

/**
 * System metric for ActiveMQ queue data.
 */
public class QueueData implements SystemHealthMetric {
    
    private static final String keyBase = "yukon.web.modules.support.systemHealth.metric.";
    private static final DecimalFormat df = new DecimalFormat("##,###.## ms");
    
    private SystemHealthMetricIdentifier metricIdentifier;
    private Long enqueuedCount;
    private Long dequeuedCount;
    private String formattedAverageEnqueueMillis;
    private Long queueSize;
    private MetricStatusWithMessages status;
    
    public QueueData(SystemHealthMetricIdentifier metricIdentifier) {
        this.metricIdentifier = metricIdentifier;
    }
    
    @Override
    public MessageSourceResolvable getMessage() {
        return new YukonMessageSourceResolvable(keyBase + metricIdentifier.getKeySuffix());
    }

    public String getQueueName() {
        return metricIdentifier.getQueueName();
    }

    public Long getEnqueuedCount() {
        return enqueuedCount;
    }

    public void setEnqueuedCount(Long enqueuedCount) {
        this.enqueuedCount = enqueuedCount;
    }

    public Long getDequeuedCount() {
        return dequeuedCount;
    }

    public void setDequeuedCount(Long dequeuedCount) {
        this.dequeuedCount = dequeuedCount;
    }
    
    /**
     * @return The average enqueue time, in milliseconds, as a formatted String.
     */
    public String getAverageEnqueueTime() {
        return formattedAverageEnqueueMillis;
    }

    public void setAverageEnqueueTime(Double averageEnqueueMillis) {
        if (averageEnqueueMillis == null) {
            averageEnqueueMillis = 0.0;
        }
        formattedAverageEnqueueMillis = df.format(averageEnqueueMillis);
    }

    public Long getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Long queueSize) {
        this.queueSize = queueSize;
    }

    @Override
    public MetricStatusWithMessages getStatus() {
        return status;
    }

    @Override
    public void setStatus(MetricStatusWithMessages status) {
        this.status = status;
    }
    
    @Override
    public SystemHealthMetricType getType() {
        return SystemHealthMetricType.JMS_QUEUE;
    }

    @Override
    public SystemHealthMetricIdentifier getMetricIdentifier() {
        return metricIdentifier;
    }

    @Override
    public String toString() {
        return "QueueData [metricIdentifier=" + metricIdentifier + ", enqueuedCount=" + enqueuedCount
               + ", dequeuedCount=" + dequeuedCount + ", formattedAverageEnqueueMillis="
               + formattedAverageEnqueueMillis + ", queueSize=" + queueSize + ", status=" + status + "]";
    }
    
}
