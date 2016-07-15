package com.cannontech.common.rfn.message.datastreaming.gateway;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Describes data streaming related information for a gateway including:
 * 1. its primary devices and their current data streaming load;
 * 2. its current capacity related information.
 */
public class GatewayDataStreamingInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private RfnIdentifier gatewayRfnIdentifier;
    private Map<RfnIdentifier, Double> deviceRfnIdentifiers; // Map<device, currentDeviceDataStreamingLoad> 
    
    // The following lists capacity information for the gateway.
    private double maxGatewayDataStreamingCapacity;
    private double currentGatewayDataStreamingLoading;
    
    public RfnIdentifier getGatewayRfnIdentifier() {
        return gatewayRfnIdentifier;
    }
    
    public void setGatewayRfnIdentifier(RfnIdentifier gatewayRfnIdentifier) {
        this.gatewayRfnIdentifier = gatewayRfnIdentifier;
    }
    
    public Map<RfnIdentifier, Double> getDeviceRfnIdentifiers() {
        return deviceRfnIdentifiers;
    }
    
    public void setDeviceRfnIdentifiers(Map<RfnIdentifier, Double> deviceRfnIdentifiers) {
        this.deviceRfnIdentifiers = deviceRfnIdentifiers;
    }
    
    public double getMaxGatewayDataStreamingCapacity() {
        return maxGatewayDataStreamingCapacity;
    }
    
    public void setMaxGatewayDataStreamingCapacity(double maxGatewayDataStreamingCapacity) {
        this.maxGatewayDataStreamingCapacity = maxGatewayDataStreamingCapacity;
    }
    
    public double getCurrentGatewayDataStreamingLoading() {
        return currentGatewayDataStreamingLoading;
    }
    
    public void setCurrentGatewayDataStreamingLoading(double currentGatewayDataStreamingLoading) {
        this.currentGatewayDataStreamingLoading = currentGatewayDataStreamingLoading;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(currentGatewayDataStreamingLoading);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((deviceRfnIdentifiers == null) ? 0 : deviceRfnIdentifiers.hashCode());
        result = prime * result + ((gatewayRfnIdentifier == null) ? 0 : gatewayRfnIdentifier.hashCode());
        temp = Double.doubleToLongBits(maxGatewayDataStreamingCapacity);
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
        GatewayDataStreamingInfo other = (GatewayDataStreamingInfo) obj;
        if (Double.doubleToLongBits(currentGatewayDataStreamingLoading) != Double
            .doubleToLongBits(other.currentGatewayDataStreamingLoading)) {
            return false;
        }
        if (deviceRfnIdentifiers == null) {
            if (other.deviceRfnIdentifiers != null) {
                return false;
            }
        } else if (!deviceRfnIdentifiers.equals(other.deviceRfnIdentifiers)) {
            return false;
        }
        if (gatewayRfnIdentifier == null) {
            if (other.gatewayRfnIdentifier != null) {
                return false;
            }
        } else if (!gatewayRfnIdentifier.equals(other.gatewayRfnIdentifier)) {
            return false;
        }
        if (Double.doubleToLongBits(maxGatewayDataStreamingCapacity) != Double
            .doubleToLongBits(other.maxGatewayDataStreamingCapacity)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GatewayDataStreamingInfo [gatewayRfnIdentifier=" + gatewayRfnIdentifier + ", deviceRfnIdentifiers="
               + deviceRfnIdentifiers + ", maxGatewayDataStreamingCapacity=" + maxGatewayDataStreamingCapacity
               + ", currentGatewayDataStreamingLoading=" + currentGatewayDataStreamingLoading + "]";
    }
    
}
