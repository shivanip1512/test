package com.cannontech.dr.honeywell.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessControlListItemRequest {
    private int deviceId;
    private Boolean accessConfirmed;

    @JsonCreator
    public AccessControlListItemRequest(@JsonProperty("DeviceId") int deviceId,
            @JsonProperty("AccessConfirmed") Boolean accessConfirmed) {
        this.deviceId = deviceId;
        this.accessConfirmed = accessConfirmed;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getAccessConfirmed() {
        return accessConfirmed;
    }

    public void setAccessConfirmed(Boolean accessConfirmed) {
        this.accessConfirmed = accessConfirmed;
    }

}
