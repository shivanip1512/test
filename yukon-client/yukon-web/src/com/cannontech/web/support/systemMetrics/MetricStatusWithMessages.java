package com.cannontech.web.support.systemMetrics;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * A metric status with explanatory messages.
 */
public class MetricStatusWithMessages {
    private final MetricStatus metricStatus;
    private final ImmutableList<String> messages;
    
    public MetricStatusWithMessages(MetricStatus metricStatus) {
        this.metricStatus = metricStatus;
        messages = ImmutableList.of();
    }
    
    public MetricStatusWithMessages(MetricStatus metricStatus, List<String> messages) {
        this.metricStatus = metricStatus;
        this.messages = ImmutableList.copyOf(messages);
    }
    
    public MetricStatus getMetricStatus() {
        return metricStatus;
    }
    
    /**
     * @return The name of the icon visually representing this status.
     */
    public String getIconName() {
        return metricStatus.getIconName();
    }
    
    /**
     * @return The i18ned message strings explaining this status.
     */
    public List<String> getMessages() {
        return messages;
    }
    
    /**
     * @return All the message strings, concatenated, separated by HTML newlines.
     */
    public String getAllMessages() {
        return StringUtils.join(messages, "&#10;");
    }
    
    public boolean hasMessages() {
        return messages.size() > 0;
    }
    
    /**
     * @return a new MetricStatusWithMessages whose status is the highest priority of the two statuses, and whose
     * messages list contains all messages from both objects.
     */
    public MetricStatusWithMessages merge(MetricStatusWithMessages other) {
        List<String> mergedMessages = Lists.newArrayList(messages);
        mergedMessages.addAll(other.getMessages());
        if (other.getMetricStatus().getPriority() > metricStatus.getPriority()) {
            return new MetricStatusWithMessages(other.getMetricStatus(), mergedMessages);
        }
        return new MetricStatusWithMessages(metricStatus, mergedMessages);
    }

    /**
     * Used for debugging purposes. To get status or messages for display, use getMetricStatus(), getMessages() or 
     * getAllMessages().
     */
    @Override
    public String toString() {
        return "MetricStatusWithMessages [metricStatus=" + metricStatus + ", messages=" + messages + "]";
    }
}
