package com.cannontech.dr.eatonCloud.model.v1;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EatonCloudSiteDevicesV1 extends EatonCloudSiteV1 {
    private List<EatonCloudSiteDeviceV1> devices;

    @JsonCreator
    public EatonCloudSiteDevicesV1(@JsonProperty("id") String siteGuid,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("contact") String contact,
            @JsonProperty("email") String email,
            @JsonProperty("phone") String phone,
            @JsonProperty("created") String createdTime,
            @JsonProperty("created_by") String createdByUser,
            @JsonProperty("modified") String modifiedTime,
            @JsonProperty("modified_by") String modifiedByUser,
            @JsonProperty("devices") List<EatonCloudSiteDeviceV1> devices) {
        super(siteGuid, name, description, contact, email, phone, createdTime, createdByUser, modifiedTime, modifiedByUser);
        this.devices = devices;
    }
 
    @JsonProperty("devices")
    public List<EatonCloudSiteDeviceV1> getDevices() {
        return devices;
    }
}
