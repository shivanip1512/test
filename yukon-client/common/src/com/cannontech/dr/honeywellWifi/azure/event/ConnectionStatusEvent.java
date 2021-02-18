package com.cannontech.dr.honeywellWifi.azure.event;

import com.cannontech.dr.JsonSerializers.FROM_CONNECTION_STATUS;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Honeywell Azure service bus connection status event. Contains info about whether the thermostat is connected to
 * Honeywell servers or not (usually indicating whether the thermostat has a wifi connection).
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ConnectionStatusEvent extends AbstractHoneywellWifiData {
    private final ConnectionStatus connectionStatus;
    private final Integer deviceId;
    private final String macId;
    
    @JsonCreator
    public ConnectionStatusEvent(@JsonDeserialize(using=FROM_CONNECTION_STATUS.class) @JsonProperty("connectionStatus") ConnectionStatus connectionStatus,
                                 @JsonProperty("deviceId") Integer deviceId,
                                 @JsonProperty("macId") String macId) {
        
        this.connectionStatus = connectionStatus;
        this.deviceId = deviceId;
        this.macId = macId;
    }
    
    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.CONNECTION_STATUS_EVENT;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public Integer getDeviceId() {
        return deviceId;
    }
    
    @Override
    public String getMacId() {
        return macId;
    }

    @Override
    public String toString() {
        return "ConnectionStatusEvent [connectionStatus=" + connectionStatus + ", deviceId=" + deviceId + ", macId="
               + macId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionStatus == null) ? 0 : connectionStatus.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        result = prime * result + ((macId == null) ? 0 : macId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConnectionStatusEvent other = (ConnectionStatusEvent) obj;
        if (connectionStatus != other.connectionStatus)
            return false;
        if (deviceId == null) {
            if (other.deviceId != null)
                return false;
        } else if (!deviceId.equals(other.deviceId))
            return false;
        if (macId == null) {
            if (other.macId != null)
                return false;
        } else if (!macId.equals(other.macId))
            return false;
        return true;
    }
}
