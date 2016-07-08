package com.cannontech.web.rfn.dataStreaming.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DataStreamingConfiguration {
    private Integer behaviorId;
    private boolean streamingEnabled;
    private List<DataStreamingMetric> configuredMetrics;
    
    public Integer getBehaviorId() {
        return behaviorId;
    }
    
    public void setBehaviorId(Integer behaviorId) {
        this.behaviorId = behaviorId;
    }
    
    public boolean isStreamingEnabled() {
        return streamingEnabled;
    }
    
    public void setStreamingEnabled(boolean streamingEnabled) {
        this.streamingEnabled = streamingEnabled;
    }
    
    public List<DataStreamingMetric> getConfiguredMetrics() {
        return configuredMetrics;
    }
    
    public void setConfiguredMetrics(List<DataStreamingMetric> configuredMetrics) {
        this.configuredMetrics = configuredMetrics;
    }
}
