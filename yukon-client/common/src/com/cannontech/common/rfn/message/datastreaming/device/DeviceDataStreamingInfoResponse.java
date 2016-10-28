package com.cannontech.common.rfn.message.datastreaming.device;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Response to DeviceDataStreamingInfoRequest.
 * 
 * JMS Queue name:
 *     com.eaton.eas.yukon.networkmanager.dataStreaming.response
 */
public class DeviceDataStreamingInfoResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    // null DeviceDataStreamingInfo value indicates invalid device.
    private Map<RfnIdentifier, DeviceDataStreamingInfo> deviceDataStreamingInfos;

    public Map<RfnIdentifier, DeviceDataStreamingInfo> getDeviceDataStreamingInfos() {
        return deviceDataStreamingInfos;
    }

    public void setDeviceDataStreamingInfos(Map<RfnIdentifier, DeviceDataStreamingInfo> deviceDataStreamingInfos) {
        this.deviceDataStreamingInfos = deviceDataStreamingInfos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceDataStreamingInfos == null) ? 0 : deviceDataStreamingInfos.hashCode());
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
        DeviceDataStreamingInfoResponse other = (DeviceDataStreamingInfoResponse) obj;
        if (deviceDataStreamingInfos == null) {
            if (other.deviceDataStreamingInfos != null) {
                return false;
            }
        } else if (!deviceDataStreamingInfos.equals(other.deviceDataStreamingInfos)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DeviceDataStreamingInfoResponse [deviceDataStreamingInfos=" + deviceDataStreamingInfos + "]";
    }
    
}
