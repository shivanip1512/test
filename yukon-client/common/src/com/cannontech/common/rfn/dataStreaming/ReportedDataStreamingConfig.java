package com.cannontech.common.rfn.dataStreaming;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Data streaming configuration, as reported from the device.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportedDataStreamingConfig {
    private boolean streamingEnabled;
    private List<ReportedDataStreamingAttribute> attributes;
    
    public boolean isStreamingEnabled() {
        return streamingEnabled;
    }
    
    public void setStreamingEnabled(boolean streamingEnabled) {
        this.streamingEnabled = streamingEnabled;
    }
    
    public List<ReportedDataStreamingAttribute> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(List<ReportedDataStreamingAttribute> configuredMetrics) {
        this.attributes = configuredMetrics;
    }

    @Override
    public String toString() {
        return "ReportedDataStreamingConfig [streamingEnabled=" + streamingEnabled + ", configuredMetrics="
               + attributes + "]";
    }
    
}
