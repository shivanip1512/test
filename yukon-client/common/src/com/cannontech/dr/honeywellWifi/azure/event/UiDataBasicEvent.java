package com.cannontech.dr.honeywellWifi.azure.event;

import org.joda.time.Instant;

import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.dr.JsonSerializers.FROM_DATE_HONEYWELL;
import com.cannontech.dr.JsonSerializers.FROM_TEMPERATURE_UNIT;
import com.cannontech.dr.JsonSerializers.TO_DATE_HONEYWELL;
import com.cannontech.dr.JsonSerializers.TO_TEMPERATURE_UNIT;
import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Honeywell Azure service bus data event. Contains general data about the settings and state of the device.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UiDataBasicEvent extends AbstractHoneywellWifiData {
    private final Instant created;
    private final Double displayedTemp;
    private final Double heatSetpoint;
    private final Double coolSetpoint;
    private final TemperatureUnit displayedUnits;
    private final String statusHeat;
    private final Double heatLowerSetpointLimit;
    private final Double heatUpperSetpointLimit;
    private final Double coolLowerSetpointLimit;
    private final Double coolUpperSetpointLimit;
    private final Double scheduledHeatSetpoint;
    private final Double scheduledCoolSetpoint;
    private final Boolean switchEmergencyHeatAllowed;
    private final String systemSwitchPosition;
    private final Double deadband;
    private final String displayedTempStatus;
    private final Integer deviceId;
    private final String macId;
    
    public UiDataBasicEvent(@JsonDeserialize(using=FROM_DATE_HONEYWELL.class) @JsonProperty("created") Instant created, 
                            @JsonProperty("displayedTemp") Double displayedTemp, 
                            @JsonProperty("heatSetpoint") Double heatSetpoint, 
                            @JsonProperty("coolSetpoint") Double coolSetpoint,
                            @JsonDeserialize(using=FROM_TEMPERATURE_UNIT.class) @JsonProperty("displayedUnits") TemperatureUnit displayedUnits, 
                            @JsonProperty("statusHeat") String statusHeat, 
                            @JsonProperty("heatLowerSetpointLimit") Double heatLowerSetpointLimit,
                            @JsonProperty("heatUpperSetpointLimit") Double heatUpperSetpointLimit, 
                            @JsonProperty("coolLowerSetpointLimit") Double coolLowerSetpointLimit,
                            @JsonProperty("coolUpperSetpointLimit") Double coolUpperSetpointLimit, 
                            @JsonProperty("scheduledHeatSetpoint") Double scheduledHeatSetpoint, 
                            @JsonProperty("scheduledCoolSetpoint") Double scheduledCoolSetpoint,
                            @JsonProperty("switchEmergencyHeatAllowed") Boolean switchEmergencyHeatAllowed, 
                            @JsonProperty("systemSwitchPosition") String systemSwitchPosition, 
                            @JsonProperty("deadband") Double deadband,
                            @JsonProperty("displayedTempStatus") String displayedTempStatus, 
                            @JsonProperty("deviceId") Integer deviceId, 
                            @JsonProperty("macId") String macId) {
        
        this.created = created;
        this.displayedTemp = displayedTemp;
        this.heatSetpoint = heatSetpoint;
        this.coolSetpoint = coolSetpoint;
        this.displayedUnits = displayedUnits;
        this.statusHeat = statusHeat;
        this.heatLowerSetpointLimit = heatLowerSetpointLimit;
        this.heatUpperSetpointLimit = heatUpperSetpointLimit;
        this.coolLowerSetpointLimit = coolLowerSetpointLimit;
        this.coolUpperSetpointLimit = coolUpperSetpointLimit;
        this.scheduledHeatSetpoint = scheduledHeatSetpoint;
        this.scheduledCoolSetpoint = scheduledCoolSetpoint;
        this.switchEmergencyHeatAllowed = switchEmergencyHeatAllowed;
        this.systemSwitchPosition = systemSwitchPosition;
        this.deadband = deadband;
        this.displayedTempStatus = displayedTempStatus;
        this.deviceId = deviceId;
        this.macId = macId;
    }

    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.UI_DATA_BASIC_EVENT;
    }
    
    @JsonSerialize(using=TO_DATE_HONEYWELL.class)
    public Instant getCreatedDate() {
        return created;
    }

    public Double getDisplayedTemp() {
        return displayedTemp;
    }

    public Double getHeatSetpoint() {
        return heatSetpoint;
    }

    public Double getCoolSetpoint() {
        return coolSetpoint;
    }
    
    @JsonSerialize(using=TO_TEMPERATURE_UNIT.class)
    public TemperatureUnit getDisplayedUnits() {
        return displayedUnits;
    }

    public String getStatusHeat() {
        return statusHeat;
    }

    public Double getHeatLowerSetpointLimit() {
        return heatLowerSetpointLimit;
    }

    public Double getHeatUpperSetpointLimit() {
        return heatUpperSetpointLimit;
    }

    public Double getCoolLowerSetpointLimit() {
        return coolLowerSetpointLimit;
    }

    public Double getCoolUpperSetpointLimit() {
        return coolUpperSetpointLimit;
    }

    public Double getScheduledHeatSetpoint() {
        return scheduledHeatSetpoint;
    }

    public Double getScheduledCoolSetpoint() {
        return scheduledCoolSetpoint;
    }

    public Boolean getSwitchEmergencyHeatAllowed() {
        return switchEmergencyHeatAllowed;
    }

    public String getSystemSwitchPosition() {
        return systemSwitchPosition;
    }

    public Double getDeadband() {
        return deadband;
    }

    public String getDisplayedTempStatus() {
        return displayedTempStatus;
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
        return "UiDataBasicEvent [createdDate=" + created
               + ", displayedTemp=" + displayedTemp + ", heatSetpoint=" + heatSetpoint + ", coolSetpoint="
               + coolSetpoint + ", displayedUnits=" + displayedUnits + ", statusHeat=" + statusHeat
               + ", heatLowerSetpointLimit=" + heatLowerSetpointLimit + ", heatUpperSetpointLimit="
               + heatUpperSetpointLimit + ", coolLowerSetpointLimit=" + coolLowerSetpointLimit
               + ", coolUpperSetpointLimit=" + coolUpperSetpointLimit + ", scheduledHeatSetpoint="
               + scheduledHeatSetpoint + ", scheduledCoolSetpoint=" + scheduledCoolSetpoint
               + ", switchEmergencyHeatAllowed=" + switchEmergencyHeatAllowed + ", systemSwitchPosition="
               + systemSwitchPosition + ", deadband=" + deadband + ", displayedTempStatus=" + displayedTempStatus
               + ", deviceId=" + deviceId + ", macId=" + macId + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                 + ((coolLowerSetpointLimit == null) ? 0 : coolLowerSetpointLimit.hashCode());
        result = prime * result + ((coolSetpoint == null) ? 0 : coolSetpoint.hashCode());
        result = prime * result
                 + ((coolUpperSetpointLimit == null) ? 0 : coolUpperSetpointLimit.hashCode());
        result = prime * result + ((created == null) ? 0 : created.hashCode());
        result = prime * result + ((deadband == null) ? 0 : deadband.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        result = prime * result + ((displayedTemp == null) ? 0 : displayedTemp.hashCode());
        result =
            prime * result + ((displayedTempStatus == null) ? 0 : displayedTempStatus.hashCode());
        result = prime * result + ((displayedUnits == null) ? 0 : displayedUnits.hashCode());
        result = prime * result
                 + ((heatLowerSetpointLimit == null) ? 0 : heatLowerSetpointLimit.hashCode());
        result = prime * result + ((heatSetpoint == null) ? 0 : heatSetpoint.hashCode());
        result = prime * result
                 + ((heatUpperSetpointLimit == null) ? 0 : heatUpperSetpointLimit.hashCode());
        result = prime * result + ((macId == null) ? 0 : macId.hashCode());
        result = prime * result
                 + ((scheduledCoolSetpoint == null) ? 0 : scheduledCoolSetpoint.hashCode());
        result = prime * result
                 + ((scheduledHeatSetpoint == null) ? 0 : scheduledHeatSetpoint.hashCode());
        result = prime * result + ((statusHeat == null) ? 0 : statusHeat.hashCode());
        result = prime * result + ((switchEmergencyHeatAllowed == null) ? 0
                : switchEmergencyHeatAllowed.hashCode());
        result =
            prime * result + ((systemSwitchPosition == null) ? 0 : systemSwitchPosition.hashCode());
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
        UiDataBasicEvent other = (UiDataBasicEvent) obj;
        if (coolLowerSetpointLimit == null) {
            if (other.coolLowerSetpointLimit != null)
                return false;
        } else if (!coolLowerSetpointLimit.equals(other.coolLowerSetpointLimit))
            return false;
        if (coolSetpoint == null) {
            if (other.coolSetpoint != null)
                return false;
        } else if (!coolSetpoint.equals(other.coolSetpoint))
            return false;
        if (coolUpperSetpointLimit == null) {
            if (other.coolUpperSetpointLimit != null)
                return false;
        } else if (!coolUpperSetpointLimit.equals(other.coolUpperSetpointLimit))
            return false;
        if (created == null) {
            if (other.created != null)
                return false;
        } else if (!created.equals(other.created))
            return false;
        if (deadband == null) {
            if (other.deadband != null)
                return false;
        } else if (!deadband.equals(other.deadband))
            return false;
        if (deviceId == null) {
            if (other.deviceId != null)
                return false;
        } else if (!deviceId.equals(other.deviceId))
            return false;
        if (displayedTemp == null) {
            if (other.displayedTemp != null)
                return false;
        } else if (!displayedTemp.equals(other.displayedTemp))
            return false;
        if (displayedTempStatus == null) {
            if (other.displayedTempStatus != null)
                return false;
        } else if (!displayedTempStatus.equals(other.displayedTempStatus))
            return false;
        if (displayedUnits != other.displayedUnits)
            return false;
        if (heatLowerSetpointLimit == null) {
            if (other.heatLowerSetpointLimit != null)
                return false;
        } else if (!heatLowerSetpointLimit.equals(other.heatLowerSetpointLimit))
            return false;
        if (heatSetpoint == null) {
            if (other.heatSetpoint != null)
                return false;
        } else if (!heatSetpoint.equals(other.heatSetpoint))
            return false;
        if (heatUpperSetpointLimit == null) {
            if (other.heatUpperSetpointLimit != null)
                return false;
        } else if (!heatUpperSetpointLimit.equals(other.heatUpperSetpointLimit))
            return false;
        if (macId == null) {
            if (other.macId != null)
                return false;
        } else if (!macId.equals(other.macId))
            return false;
        if (scheduledCoolSetpoint == null) {
            if (other.scheduledCoolSetpoint != null)
                return false;
        } else if (!scheduledCoolSetpoint.equals(other.scheduledCoolSetpoint))
            return false;
        if (scheduledHeatSetpoint == null) {
            if (other.scheduledHeatSetpoint != null)
                return false;
        } else if (!scheduledHeatSetpoint.equals(other.scheduledHeatSetpoint))
            return false;
        if (statusHeat == null) {
            if (other.statusHeat != null)
                return false;
        } else if (!statusHeat.equals(other.statusHeat))
            return false;
        if (switchEmergencyHeatAllowed == null) {
            if (other.switchEmergencyHeatAllowed != null)
                return false;
        } else if (!switchEmergencyHeatAllowed.equals(other.switchEmergencyHeatAllowed))
            return false;
        if (systemSwitchPosition == null) {
            if (other.systemSwitchPosition != null)
                return false;
        } else if (!systemSwitchPosition.equals(other.systemSwitchPosition))
            return false;
        return true;
    }
}
