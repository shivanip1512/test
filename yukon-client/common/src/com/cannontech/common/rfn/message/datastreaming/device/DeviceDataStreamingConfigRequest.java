package com.cannontech.common.rfn.message.datastreaming.device;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Request to update data streaming configurations for devices.
 * It first defines a list of data streaming configurations, then describes a list of devices 
 * and their corresponding configurations. A configuration can be applied to multiple devices.
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.dataStreaming.request
 */
public class DeviceDataStreamingConfigRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private DeviceDataStreamingConfigRequestType requestType;
    private String requestId; // an unique randomly-generated Id to correlate CONFIRM to UPDATE.
    private int requestExpiration;  // pending time in minutes for UPDATE; <= 0 indicates pending until confirm.

    private DeviceDataStreamingConfig[] configs;
    private Map<RfnIdentifier, Integer> devices; // Map<deviceRfnIdentifier, indexOfDeviceDataStreamingConfig>
    
    public DeviceDataStreamingConfigRequestType getRequestType() {
        return requestType;
    }
    
    public void setRequestType(DeviceDataStreamingConfigRequestType requestType) {
        this.requestType = requestType;
    }
    
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
    public int getRequestExpiration() {
        return requestExpiration;
    }
    
    public void setRequestExpiration(int requestExpiration) {
        this.requestExpiration = requestExpiration;
    }
    
    public DeviceDataStreamingConfig[] getConfigs() {
        return configs;
    }

    public void setConfigs(DeviceDataStreamingConfig[] configs) {
        this.configs = configs;
    }

    public Map<RfnIdentifier, Integer> getDevices() {
        return devices;
    }

    public void setDevices(Map<RfnIdentifier, Integer> devices) {
        this.devices = devices;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(configs);
        result = prime * result + ((devices == null) ? 0 : devices.hashCode());
        result = prime * result + requestExpiration;
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
        result = prime * result + ((requestType == null) ? 0 : requestType.hashCode());
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
        DeviceDataStreamingConfigRequest other = (DeviceDataStreamingConfigRequest) obj;
        if (!Arrays.equals(configs, other.configs)) {
            return false;
        }
        if (devices == null) {
            if (other.devices != null) {
                return false;
            }
        } else if (!devices.equals(other.devices)) {
            return false;
        }
        if (requestExpiration != other.requestExpiration) {
            return false;
        }
        if (requestId == null) {
            if (other.requestId != null) {
                return false;
            }
        } else if (!requestId.equals(other.requestId)) {
            return false;
        }
        if (requestType != other.requestType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeviceDataStreamingConfigRequest [requestType=" + requestType + ", requestId=" + requestId
               + ", requestExpiration=" + requestExpiration + ", configs=" + Arrays.toString(configs) + ", devices="
               + devices + "]";
    }

}
