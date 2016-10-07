package com.cannontech.dr.honeywellWifi;

import org.joda.time.Instant;

import com.cannontech.dr.JsonSerializers.FROM_DATE;
import com.cannontech.dr.JsonSerializers.TO_DATE;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UiDataBasicEvent implements HoneywellWifiData {
    private BrokeredMessage originalMessage;
    private final Instant created;
    private final Double displayedTemp;
    private final Double heatSetpoint;
    private final Double coolSetpoint;
    private final String displayedUnits;
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
    
    public UiDataBasicEvent(@JsonDeserialize(using=FROM_DATE.class) @JsonProperty("created") Instant created, 
                            @JsonProperty("displayedTemp") Double displayedTemp, 
                            @JsonProperty("heatSetpoint") Double heatSetpoint, 
                            @JsonProperty("coolSetpoint") Double coolSetpoint,
                            @JsonProperty("displayedUnits") String displayedUnits, 
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
    public BrokeredMessage getOriginalMessage() {
        return originalMessage;
    }
    
    @Override
    public void setOriginalMessage(BrokeredMessage originalMessage) {
        this.originalMessage = originalMessage;
    }

    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.UI_DATA_BASIC_EVENT;
    }
    
    @JsonSerialize(using=TO_DATE.class)
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

    public String getDisplayedUnits() {
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

    public String getMacId() {
        return macId;
    }

    @Override
    public String toString() {
        return "UiDataBasicEvent [originalMessage=" + originalMessage + ", createdDate=" + created
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
}
