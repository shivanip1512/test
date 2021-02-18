package com.cannontech.dr.honeywellWifi.azure.event;

import com.cannontech.dr.JsonSerializers.FROM_EQUIPMENT_STATUS;
import com.cannontech.dr.JsonSerializers.TO_EQUIPMENT_STATUS;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Honeywell Azure service bus equipment status event. Contains info about the state of the thermostat heat/cool and fan
 * relays.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class EquipmentStatusEvent extends AbstractHoneywellWifiData {
    private final EquipmentStatus equipmentStatus;
    private final EquipmentStatus previousEquipmentStatus;
    // If we ever decide to care about fanStatus, these should be converted to an enum.
    private final String fanStatus;
    private final String previousFanStatus;
    private final Integer deviceId;
    private final String macId;
    
    @JsonCreator
    public EquipmentStatusEvent(@JsonDeserialize(using=FROM_EQUIPMENT_STATUS.class) @JsonProperty("equipmentStatus") EquipmentStatus equipmentStatus,
                                @JsonDeserialize(using=FROM_EQUIPMENT_STATUS.class) @JsonProperty("previousEquipmentStatus") EquipmentStatus previousEquipmentStatus,
                                @JsonProperty("fanStatus") String fanStatus,
                                @JsonProperty("previousFanStatus") String previousFanStatus,
                                @JsonProperty("deviceId") Integer deviceId,
                                @JsonProperty("macId") String macId) {
        
        // Message will contain *either* equipmentStatus or fanStatus. The other will be null.
        this.equipmentStatus = equipmentStatus;
        this.previousEquipmentStatus = previousEquipmentStatus;
        this.fanStatus = fanStatus;
        this.previousFanStatus = previousFanStatus;
        this.deviceId = deviceId;
        this.macId = macId;
    }
    
    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.EQUIPMENT_STATUS_EVENT;
    }
    
    @JsonSerialize(using=TO_EQUIPMENT_STATUS.class)
    public EquipmentStatus getEquipmentStatus() {
        return equipmentStatus;
    }
    
    @JsonSerialize(using=TO_EQUIPMENT_STATUS.class)
    public EquipmentStatus getPreviousEquipmentStatus() {
        return previousEquipmentStatus;
    }
    
    public String getFanStatus() {
        return fanStatus;
    }
    
    public String getPreviousFanStatus() {
        return previousFanStatus;
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
        return "EquipmentStatusEvent [equipmentStatus=" + equipmentStatus + ", fanStatus=" + fanStatus + ", deviceId="
               + deviceId + ", macId=" + macId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        result = prime * result + ((equipmentStatus == null) ? 0 : equipmentStatus.hashCode());
        result = prime * result + ((fanStatus == null) ? 0 : fanStatus.hashCode());
        result = prime * result + ((macId == null) ? 0 : macId.hashCode());
        result = prime * result
                 + ((previousEquipmentStatus == null) ? 0 : previousEquipmentStatus.hashCode());
        result = prime * result + ((previousFanStatus == null) ? 0 : previousFanStatus.hashCode());
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
        EquipmentStatusEvent other = (EquipmentStatusEvent) obj;
        if (deviceId == null) {
            if (other.deviceId != null)
                return false;
        } else if (!deviceId.equals(other.deviceId))
            return false;
        if (equipmentStatus != other.equipmentStatus)
            return false;
        if (fanStatus == null) {
            if (other.fanStatus != null)
                return false;
        } else if (!fanStatus.equals(other.fanStatus))
            return false;
        if (macId == null) {
            if (other.macId != null)
                return false;
        } else if (!macId.equals(other.macId))
            return false;
        if (previousEquipmentStatus != other.previousEquipmentStatus)
            return false;
        if (previousFanStatus == null) {
            if (other.previousFanStatus != null)
                return false;
        } else if (!previousFanStatus.equals(other.previousFanStatus))
            return false;
        return true;
    }
}
