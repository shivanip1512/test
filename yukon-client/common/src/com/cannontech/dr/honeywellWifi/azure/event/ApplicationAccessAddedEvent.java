package com.cannontech.dr.honeywellWifi.azure.event;

import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Honeywell Azure service bus application access event. Contains info about Honeywell accounts granting API access to 
 * Yukon.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationAccessAddedEvent extends AbstractHoneywellWifiData {
    private final Integer userId;
    private final Integer locationId;
    private final String macId;
    private final Integer appId;
    private final String applicationName;
    private final Boolean isConfirmed;
    
    @JsonCreator
    public ApplicationAccessAddedEvent(@JsonProperty("userId") Integer userId, 
                                       @JsonProperty("locationId") Integer locationId, 
                                       @JsonProperty("macId") String macId, 
                                       @JsonProperty("appId") Integer appId, 
                                       @JsonProperty("applicationName") String applicationName, 
                                       @JsonProperty("isConfirmed") Boolean isConfirmed) {
        this.userId = userId;
        this.locationId = locationId;
        this.macId = macId;
        this.appId = appId;
        this.applicationName = applicationName;
        this.isConfirmed = isConfirmed;
    }
    
    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.APPLICATION_ACCESS_ADDED_EVENT;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public String getMacId() {
        return macId;
    }

    public Integer getAppId() {
        return appId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    @Override
    public String toString() {
        return "ApplicationAccessAddedEvent [userId=" + userId + ", locationId=" + locationId + ", macId=" + macId
               + ", appId=" + appId + ", applicationName=" + applicationName + ", isConfirmed=" + isConfirmed + "]";
    }
    
}
