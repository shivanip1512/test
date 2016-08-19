package com.cannontech.common.rfn.message.datastreaming.device;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfo;

/**
 * Response to DataStreamingConfigRequest.
 * 
 * JMS Queue name: 
 *     The temporary(i.e., reply-To) queue of the DataStreamingConfigRequest.
 */
public class DeviceDataStreamingConfigResponse implements Serializable {
    private static final long serialVersionUID = 1L;
 
    private String requestId;
    private DeviceDataStreamingConfigResponseType responseType;
    private String responseMessage;
    private Map<RfnIdentifier, GatewayDataStreamingInfo> affectedGateways; // Map<Gateway, GatewayDataStreamingInfo> is provided in both ACCEPTED and REJECTED cases
    
    private Map<RfnIdentifier, ConfigError> errorConfigedDevices; // Map<Device, configError> only used when responseType is REJECTED

    public DeviceDataStreamingConfigResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(DeviceDataStreamingConfigResponseType responseType) {
        this.responseType = responseType;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Map<RfnIdentifier, GatewayDataStreamingInfo> getAffectedGateways() {
        return affectedGateways;
    }

    public void setAffectedGateways(Map<RfnIdentifier, GatewayDataStreamingInfo> affectedGateways) {
        this.affectedGateways = affectedGateways;
    }

    public Map<RfnIdentifier, ConfigError> getErrorConfigedDevices() {
        return errorConfigedDevices;
    }

    public void setErrorConfigedDevices(Map<RfnIdentifier, ConfigError> errorConfigedDevices) {
        this.errorConfigedDevices = errorConfigedDevices;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((affectedGateways == null) ? 0 : affectedGateways.hashCode());
        result = prime * result + ((errorConfigedDevices == null) ? 0 : errorConfigedDevices.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
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
        if (affectedGateways == null) {
            if (other.affectedGateways != null) {
                return false;
            }
        } else if (!affectedGateways.equals(other.affectedGateways)) {
            return false;
        }
        if (errorConfigedDevices == null) {
            if (other.errorConfigedDevices != null) {
                return false;
            }
        } else if (!errorConfigedDevices.equals(other.errorConfigedDevices)) {
            return false;
        }
        if (requestId == null) {
            if (other.requestId != null) {
                return false;
            }
        } else if (!requestId.equals(other.requestId)) {
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
        return "DeviceDataStreamingConfigResponse [requestId=" + requestId + ", responseType=" + responseType
               + ", responseMessage=" + responseMessage + ", affectedGateways=" + affectedGateways
               + ", errorConfigedDevices=" + errorConfigedDevices + "]";
    }

    public static class ConfigError implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private DeviceDataStreamingConfigError errorType;
        private String errorMessage;
        
        private RfnIdentifier overSubscribedGatewayRfnIdentifier; // only used when errorType is GATEWAY_OVERLOADED
        private Set<Integer> unSupportedMetricIds; // only used when errorType is UNSUPPORTED_METRIC_ID
        private Set<Short> unSupportedIntervals; // only used when errorType is UNSUPPORTED_REPORT_INTERVAL
        
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
        
        public Set<Integer> getUnSupportedMetricIds() {
            return unSupportedMetricIds;
        }
        
        public void setUnSupportedMetricIds(Set<Integer> unSupportedMetricIds) {
            this.unSupportedMetricIds = unSupportedMetricIds;
        }
        
        public Set<Short> getUnSupportedIntervals() {
            return unSupportedIntervals;
        }
        
        public void setUnSupportedIntervals(Set<Short> unSupportedIntervals) {
            this.unSupportedIntervals = unSupportedIntervals;
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
            result = prime * result + ((unSupportedIntervals == null) ? 0 : unSupportedIntervals.hashCode());
            result = prime * result + ((unSupportedMetricIds == null) ? 0 : unSupportedMetricIds.hashCode());
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
            if (unSupportedIntervals == null) {
                if (other.unSupportedIntervals != null) {
                    return false;
                }
            } else if (!unSupportedIntervals.equals(other.unSupportedIntervals)) {
                return false;
            }
            if (unSupportedMetricIds == null) {
                if (other.unSupportedMetricIds != null) {
                    return false;
                }
            } else if (!unSupportedMetricIds.equals(other.unSupportedMetricIds)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ConfigError [errorType=" + errorType + ", errorMessage=" + errorMessage
                   + ", overSubscribedGatewayRfnIdentifier=" + overSubscribedGatewayRfnIdentifier
                   + ", unSupportedMetricIds=" + unSupportedMetricIds + ", unSupportedIntervals="
                   + unSupportedIntervals + "]";
        }
        
    }
}
