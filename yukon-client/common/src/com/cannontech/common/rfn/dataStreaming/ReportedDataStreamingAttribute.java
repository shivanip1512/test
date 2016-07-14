package com.cannontech.common.rfn.dataStreaming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Values within a data streaming configuration, as reported from the device.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportedDataStreamingAttribute {
    private String attribute;
    private int interval;
    private boolean enabled;
    
    public String getAttribute() {
        return attribute;
    }
    
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    
    public int getInterval() {
        return interval;
    }
    
    public void setInterval(int interval) {
        this.interval = interval;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "DataStreamingMetric [attribute=" + attribute + ", interval=" + interval + ", enabled=" + enabled + "]";
    }
}
