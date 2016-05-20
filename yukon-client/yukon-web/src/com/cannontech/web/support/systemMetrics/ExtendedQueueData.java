package com.cannontech.web.support.systemMetrics;

/**
 * System metric for ActiveMQ queue data which includes additional fields vs. regular QueueData.
 */
public class ExtendedQueueData extends QueueData implements SystemHealthMetric {
    private Integer archivedReadingsCount;
    private Integer archiveRequestsProcessed;
    
    public ExtendedQueueData(SystemHealthMetricIdentifier metricIdentifier) {
        super(metricIdentifier);
    }

    public Integer getArchivedReadingsCount() {
        return archivedReadingsCount;
    }

    public void setArchivedReadingsCount(Integer archivedReadingsCount) {
        this.archivedReadingsCount = archivedReadingsCount;
    }

    public Integer getArchiveRequestsProcessed() {
        return archiveRequestsProcessed;
    }

    public void setArchiveRequestsProcessed(Integer archiveRequestsProcessed) {
        this.archiveRequestsProcessed = archiveRequestsProcessed;
    }
    
    @Override
    public SystemHealthMetricType getType() {
        return SystemHealthMetricType.JMS_QUEUE_EXTENDED;
    }

    @Override
    public String toString() {
        return "ExtendedQueueData [archivedReadingsCount=" + archivedReadingsCount + ", archiveRequestsProcessed="
               + archiveRequestsProcessed + "]";
    }
    
}
