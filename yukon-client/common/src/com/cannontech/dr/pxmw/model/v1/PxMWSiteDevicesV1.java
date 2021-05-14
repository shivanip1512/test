package com.cannontech.dr.pxmw.model.v1;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWSiteDevicesV1 extends PxMWSiteV1 {
    private List<PxMWSiteDeviceV1> devices;

    @JsonCreator
    public PxMWSiteDevicesV1(@JsonProperty("id") String siteGuid,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("contact") String contact,
            @JsonProperty("email") String email,
            @JsonProperty("phone") String phone,
            @JsonProperty("created") String createdTime,
            @JsonProperty("created_by") String createdByUser,
            @JsonProperty("modified") String modifiedTime,
            @JsonProperty("modified_by") String modifiedByUser,
            @JsonProperty("devices") List<PxMWSiteDeviceV1> devices) {
        super(siteGuid, name, description, contact, email, phone, createdTime, createdByUser, modifiedTime, modifiedByUser);
        this.devices = devices;
    }
 
    @JsonProperty("devices")
    public List<PxMWSiteDeviceV1> getDevices() {
        return devices;
    }
}
