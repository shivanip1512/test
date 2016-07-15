package com.cannontech.common.rfn.message.datastreaming.device;

import java.io.Serializable;
import java.util.Map;

/**
 * Describes data streaming configuration settings for a device.
 * For a configuration update, value null indicates no change.
 */
public class DeviceDataStreamingConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    private Boolean dataStreamingOn; // Turns on or off data streaming for the whole device.
                                     // Changing this value doesn't change individual metric status.
                                     // Devices are initially set false (dataStreamingoff).
    private Map<Integer, MetricConfig>  metrics; // Map<metricID, metricConfig>
                                                 // Describes the list of metrics being changed for configuration
                                                 // or a full list of metrics for synchronization.
    private Long sequenceNumber; // This value is set only by device (i.e., only used for synchronization);
                                 // Sets null for configuration.
    
    public Boolean getDataStreamingOn() {
        return dataStreamingOn;
    }

    public void setDataStreamingOn(Boolean dataStreamingOn) {
        this.dataStreamingOn = dataStreamingOn;
    }

    public Map<Integer, MetricConfig> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<Integer, MetricConfig> metrics) {
        this.metrics = metrics;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataStreamingOn == null) ? 0 : dataStreamingOn.hashCode());
        result = prime * result + ((metrics == null) ? 0 : metrics.hashCode());
        result = prime * result + ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DeviceDataStreamingConfig other = (DeviceDataStreamingConfig) obj;
        if (dataStreamingOn == null) {
            if (other.dataStreamingOn != null) {
                return false;
            }
        } else if (!dataStreamingOn.equals(other.dataStreamingOn)) {
            return false;
        }
        if (metrics == null) {
            if (other.metrics != null) {
                return false;
            }
        } else if (!metrics.equals(other.metrics)) {
            return false;
        }
        if (sequenceNumber == null) {
            if (other.sequenceNumber != null) {
                return false;
            }
        } else if (!sequenceNumber.equals(other.sequenceNumber)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeviceDataStreamingConfig [dataStreamingOn=" + dataStreamingOn + ", metrics=" + metrics
               + ", sequenceNumber=" + sequenceNumber + "]";
    }

    /**
     *  Describes data streaming configuration settings for a metric
     */
    public static class MetricConfig implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Boolean enabled; // Turns on or off data streaming for this Metric only
                                 // Metrics are initially set false (disabled).
        private Short interval;  // reporting interval in minutes
        
        public Boolean getEnabled() {
            return enabled;
        }
        
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
        
        public Short getInterval() {
            return interval;
        }
        
        public void setInterval(Short interval) {
            this.interval = interval;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
            result = prime * result + ((interval == null) ? 0 : interval.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            MetricConfig other = (MetricConfig) obj;
            if (enabled == null) {
                if (other.enabled != null) {
                    return false;
                }
            } else if (!enabled.equals(other.enabled)) {
                return false;
            }
            if (interval == null) {
                if (other.interval != null) {
                    return false;
                }
            } else if (!interval.equals(other.interval)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "MetricConfig [enabled=" + enabled + ", interval=" + interval + "]";
        }
    }
}
