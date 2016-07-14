package com.cannontech.common.rfn.dataStreaming;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data streaming configuration, as reported from the device.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportedDataStreamingConfig {
    private boolean streamingEnabled;
    private List<ReportedDataStreamingAttribute> configuredMetrics;
    
    public boolean isStreamingEnabled() {
        return streamingEnabled;
    }
    
    public void setStreamingEnabled(boolean streamingEnabled) {
        this.streamingEnabled = streamingEnabled;
    }
    
    public List<ReportedDataStreamingAttribute> getConfiguredMetrics() {
        return configuredMetrics;
    }
    
    public void setConfiguredMetrics(List<ReportedDataStreamingAttribute> configuredMetrics) {
        this.configuredMetrics = configuredMetrics;
    }

    @Override
    public String toString() {
        return "ReportedDataStreamingConfig [streamingEnabled=" + streamingEnabled + ", configuredMetrics="
               + configuredMetrics + "]";
    }
    
}
