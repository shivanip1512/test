package com.cannontech.common.rfn.message.datastreaming.device;

import java.io.Serializable;
import java.util.Set;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * A query for device's data streaming information.
 * You can use it to query one device or a list of devices.
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.dataStreaming.request
 */
public class DeviceDataStreamingInfoRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<RfnIdentifier> deviceRfnIdentifiers;

    public Set<RfnIdentifier> getDeviceRfnIdentifiers() {
        return deviceRfnIdentifiers;
    }

    public void setDeviceRfnIdentifiers(Set<RfnIdentifier> deviceRfnIdentifiers) {
        this.deviceRfnIdentifiers = deviceRfnIdentifiers;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceRfnIdentifiers == null) ? 0 : deviceRfnIdentifiers.hashCode());
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
        DeviceDataStreamingInfoRequest other = (DeviceDataStreamingInfoRequest) obj;
        if (deviceRfnIdentifiers == null) {
            if (other.deviceRfnIdentifiers != null) {
                return false;
            }
        } else if (!deviceRfnIdentifiers.equals(other.deviceRfnIdentifiers)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeviceDataStreamingInfoRequest [deviceRfnIdentifiers=" + deviceRfnIdentifiers + "]";
    }
    
}
