package com.cannontech.common.rfn.message.datastreaming.device;

import java.io.Serializable;

import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.datastreaming.gateway.GatewayDataStreamingInfo;

/**
 * Describes data streaming related information for a device including:
 * 1. its current data streaming load;
 * 2. its current data streaming configuration;
 * 3. its primary Gateway.
 * 4. its primary Gateway's data streaming related information; 
 */
public class DeviceDataStreamingInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private RfnIdentifier deviceRfnIdentifier;
    private DeviceDataStreamingConfig config; // the current configuration setting for the device.
    private double load; // calculated from the above config.
    
    private RfnIdentifier gatewayRfnIdentifier; // the device's primary gateway
    private GatewayDataStreamingInfo gatewayDataStreamingInfo; 
    
    public RfnIdentifier getDeviceRfnIdentifier() {
        return deviceRfnIdentifier;
    }
    
    public void setDeviceRfnIdentifier(RfnIdentifier deviceRfnIdentifier) {
        this.deviceRfnIdentifier = deviceRfnIdentifier;
    }
    
    public double getLoad() {
        return load;
    }
    
    public void setLoad(double load) {
        this.load = load;
    }
    
    public DeviceDataStreamingConfig getConfig() {
        return config;
    }
    
    public void setConfig(DeviceDataStreamingConfig config) {
        this.config = config;
    }
    
    public RfnIdentifier getGatewayRfnIdentifier() {
        return gatewayRfnIdentifier;
    }
    
    public void setGatewayRfnIdentifier(RfnIdentifier gatewayRfnIdentifier) {
        this.gatewayRfnIdentifier = gatewayRfnIdentifier;
    }
    
    public GatewayDataStreamingInfo getGatewayDataStreamingInfo() {
        return gatewayDataStreamingInfo;
    }
    
    public void setGatewayDataStreamingInfo(GatewayDataStreamingInfo gatewayDataStreamingInfo) {
        this.gatewayDataStreamingInfo = gatewayDataStreamingInfo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((config == null) ? 0 : config.hashCode());
        result = prime * result + ((deviceRfnIdentifier == null) ? 0 : deviceRfnIdentifier.hashCode());
        result = prime * result + ((gatewayDataStreamingInfo == null) ? 0 : gatewayDataStreamingInfo.hashCode());
        result = prime * result + ((gatewayRfnIdentifier == null) ? 0 : gatewayRfnIdentifier.hashCode());
        long temp;
        temp = Double.doubleToLongBits(load);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        DeviceDataStreamingInfo other = (DeviceDataStreamingInfo) obj;
        if (config == null) {
            if (other.config != null) {
                return false;
            }
        } else if (!config.equals(other.config)) {
            return false;
        }
        if (deviceRfnIdentifier == null) {
            if (other.deviceRfnIdentifier != null) {
                return false;
            }
        } else if (!deviceRfnIdentifier.equals(other.deviceRfnIdentifier)) {
            return false;
        }
        if (gatewayDataStreamingInfo == null) {
            if (other.gatewayDataStreamingInfo != null) {
                return false;
            }
        } else if (!gatewayDataStreamingInfo.equals(other.gatewayDataStreamingInfo)) {
            return false;
        }
        if (gatewayRfnIdentifier == null) {
            if (other.gatewayRfnIdentifier != null) {
                return false;
            }
        } else if (!gatewayRfnIdentifier.equals(other.gatewayRfnIdentifier)) {
            return false;
        }
        if (Double.doubleToLongBits(load) != Double.doubleToLongBits(other.load)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeviceDataStreamingInfo [deviceRfnIdentifier=" + deviceRfnIdentifier + ", load=" + load + ", config="
               + config + ", gatewayRfnIdentifier=" + gatewayRfnIdentifier + ", gatewayDataStreamingInfo="
               + gatewayDataStreamingInfo + "]";
    }
    
}
