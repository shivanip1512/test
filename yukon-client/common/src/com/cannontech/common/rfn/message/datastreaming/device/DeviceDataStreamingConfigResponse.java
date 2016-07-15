package com.cannontech.common.rfn.message.datastreaming.device;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Response to DataStreamingConfigRequest.
 * 
 * JMS Queue name: 
 *     The temporary(i.e., reply-To) queue of the DataStreamingConfigRequest.
 */
public class DeviceDataStreamingConfigResponse implements Serializable {
    private static final long serialVersionUID = 1L;
 
    private DeviceDataStreamingConfigResponseType responseType;
    private String responseMessage;
 
    // The following is used when responseType is REJECTED
    private Map<RfnIdentifier, ConfigError> errorConfigedDevices;  // Map<Device, configError>
    private Map<RfnIdentifier, Double> overSubscribedGateways; // Map<overSubscribedGateway, overSubscriptedBy>
    
    public DeviceDataStreamingConfigResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(DeviceDataStreamingConfigResponseType responseType) {
        this.responseType = responseType;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Map<RfnIdentifier, ConfigError> getErrorConfigedDevices() {
        return errorConfigedDevices;
    }

    public void setErrorConfigedDevices(Map<RfnIdentifier, ConfigError> errorConfigedDevices) {
        this.errorConfigedDevices = errorConfigedDevices;
    }

    public Map<RfnIdentifier, Double> getOverSubscribedGateways() {
        return overSubscribedGateways;
    }

    public void setOverSubscribedGateways(Map<RfnIdentifier, Double> overSubscribedGateways) {
        this.overSubscribedGateways = overSubscribedGateways;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errorConfigedDevices == null) ? 0 : errorConfigedDevices.hashCode());
        result = prime * result + ((overSubscribedGateways == null) ? 0 : overSubscribedGateways.hashCode());
        result = prime * result + ((responseMessage == null) ? 0 : responseMessage.hashCode());
        result = prime * result + ((responseType == null) ? 0 : responseType.hashCode());
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
        DeviceDataStreamingConfigResponse other = (DeviceDataStreamingConfigResponse) obj;
        if (errorConfigedDevices == null) {
            if (other.errorConfigedDevices != null) {
                return false;
            }
        } else if (!errorConfigedDevices.equals(other.errorConfigedDevices)) {
            return false;
        }
        if (overSubscribedGateways == null) {
            if (other.overSubscribedGateways != null) {
                return false;
            }
        } else if (!overSubscribedGateways.equals(other.overSubscribedGateways)) {
            return false;
        }
        if (responseMessage == null) {
            if (other.responseMessage != null) {
                return false;
            }
        } else if (!responseMessage.equals(other.responseMessage)) {
            return false;
        }
        if (responseType != other.responseType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeviceDataStreamingConfigResponse [responseType=" + responseType + ", responseMessage="
               + responseMessage + ", errorConfigedDevices=" + errorConfigedDevices + ", overSubscribedGateways="
               + overSubscribedGateways + "]";
    }

    public static class ConfigError implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private DeviceDataStreamingConfigError errorType;
        private String errorMessage;
        private RfnIdentifier overSubscribedGatewayRfnIdentifier; // only used when errorType is GATEWAY_OVERLOADED
        private Set<Integer> unSupportedMetricIDs; // only used when errorType is UNSUPPORTED_METRIC_ID
        private Set<Short> unSupportedReportIntervals; // only used when errorType is UNSUPPORTED_REPORT_INTERVAL
        
        public DeviceDataStreamingConfigError getErrorType() {
            return errorType;
        }
        
        public void setErrorType(DeviceDataStreamingConfigError errorType) {
            this.errorType = errorType;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        public RfnIdentifier getOverSubscribedGatewayRfnIdentifier() {
            return overSubscribedGatewayRfnIdentifier;
        }
        
        public void setOverSubscribedGatewayRfnIdentifier(RfnIdentifier overSubscribedGatewayRfnIdentifier) {
            this.overSubscribedGatewayRfnIdentifier = overSubscribedGatewayRfnIdentifier;
        }
        
        public Set<Integer> getUnSupportedMetricIDs() {
            return unSupportedMetricIDs;
        }
        
        public void setUnSupportedMetricIDs(Set<Integer> unSupportedMetricIDs) {
            this.unSupportedMetricIDs = unSupportedMetricIDs;
        }
        
        public Set<Short> getUnSupportedReportIntervals() {
            return unSupportedReportIntervals;
        }
        
        public void setUnSupportedReportIntervals(Set<Short> unSupportedReportIntervals) {
            this.unSupportedReportIntervals = unSupportedReportIntervals;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
            result = prime * result + ((errorType == null) ? 0 : errorType.hashCode());
            result =
                prime
                        * result
                        + ((overSubscribedGatewayRfnIdentifier == null) ? 0 : overSubscribedGatewayRfnIdentifier
                            .hashCode());
            result = prime * result + ((unSupportedMetricIDs == null) ? 0 : unSupportedMetricIDs.hashCode());
            result =
                prime * result + ((unSupportedReportIntervals == null) ? 0 : unSupportedReportIntervals.hashCode());
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
            ConfigError other = (ConfigError) obj;
            if (errorMessage == null) {
                if (other.errorMessage != null) {
                    return false;
                }
            } else if (!errorMessage.equals(other.errorMessage)) {
                return false;
            }
            if (errorType != other.errorType) {
                return false;
            }
            if (overSubscribedGatewayRfnIdentifier == null) {
                if (other.overSubscribedGatewayRfnIdentifier != null) {
                    return false;
                }
            } else if (!overSubscribedGatewayRfnIdentifier.equals(other.overSubscribedGatewayRfnIdentifier)) {
                return false;
            }
            if (unSupportedMetricIDs == null) {
                if (other.unSupportedMetricIDs != null) {
                    return false;
                }
            } else if (!unSupportedMetricIDs.equals(other.unSupportedMetricIDs)) {
                return false;
            }
            if (unSupportedReportIntervals == null) {
                if (other.unSupportedReportIntervals != null) {
                    return false;
                }
            } else if (!unSupportedReportIntervals.equals(other.unSupportedReportIntervals)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ConfigError [errorType=" + errorType + ", errorMessage=" + errorMessage
                   + ", overSubscribedGatewayRfnIdentifier=" + overSubscribedGatewayRfnIdentifier
                   + ", unSupportedMetricIDs=" + unSupportedMetricIDs + ", unSupportedReportIntervals="
                   + unSupportedReportIntervals + "]";
        }
        
    }
}
