package com.cannontech.dr.honeywellWifi;

import com.cannontech.dr.JsonSerializers.FROM_CONNECTION_STATUS;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Honeywell Azure service bus connection status event. Contains info about whether the thermostat is connected to
 * Honeywell servers or not (usually indicating whether the thermostat has a wifi connection).
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ConnectionStatusEvent extends HoneywellWifiDataBase {
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

    public String getMacId() {
        return macId;
    }

    @Override
    public String toString() {
        return "ConnectionStatusEvent [connectionStatus=" + connectionStatus + ", deviceId=" + deviceId + ", macId="
               + macId + "]";
    }
    
}
