package com.cannontech.dr.honeywellWifi.azure.event;

import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Honeywell Azure service bus application removed event. Contains info about Honeywell accounts granting API access to 
 * Yukon.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ApplicationAccessRemovedEvent extends AbstractHoneywellWifiData {
    private final Integer userId;
    private final Integer locationId;
    private final String macId;
    private final Integer appId;
    private final String applicationName;
    
    @JsonCreator
    public ApplicationAccessRemovedEvent(@JsonProperty("userId") Integer userId, 
                                       @JsonProperty("locationId") Integer locationId, 
                                       @JsonProperty("macId") String macId, 
                                       @JsonProperty("appId") Integer appId, 
                                       @JsonProperty("applicationName") String applicationName) {
        this.userId = userId;
        this.locationId = locationId;
        this.macId = macId;
        this.appId = appId;
        this.applicationName = applicationName;
    }

    @Override
    public HoneywellWifiDataType getType() {
        return HoneywellWifiDataType.APPLICATION_ACCESS_REMOVED_EVENT;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getLocationId() {
        return locationId;
    }
    
    @Override
    public String getMacId() {
        return macId;
    }

    public Integer getAppId() {
        return appId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String toString() {
        return "ApplicationAccessRemovedEvent [userId=" + userId + ", locationId=" + locationId + ", macId=" + macId
               + ", appId=" + appId + ", applicationName=" + applicationName + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appId == null) ? 0 : appId.hashCode());
        result = prime * result + ((applicationName == null) ? 0 : applicationName.hashCode());
        result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
        result = prime * result + ((macId == null) ? 0 : macId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
        ApplicationAccessRemovedEvent other = (ApplicationAccessRemovedEvent) obj;
        if (appId == null) {
            if (other.appId != null)
                return false;
        } else if (!appId.equals(other.appId))
            return false;
        if (applicationName == null) {
            if (other.applicationName != null)
                return false;
        } else if (!applicationName.equals(other.applicationName))
            return false;
        if (locationId == null) {
            if (other.locationId != null)
                return false;
        } else if (!locationId.equals(other.locationId))
            return false;
        if (macId == null) {
            if (other.macId != null)
                return false;
        } else if (!macId.equals(other.macId))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }
}
