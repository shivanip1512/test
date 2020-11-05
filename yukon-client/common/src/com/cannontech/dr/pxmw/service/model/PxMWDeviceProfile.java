package com.cannontech.dr.pxmw.service.model;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data for a single device profile.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWDeviceProfile {
    private final String deviceGuid;
    private final String createdTime;
    private final String createdByUser;
    private final String modifiedTime;
    private final String modifiedByUser;
    private final HashMap<String, String> device;
    private final HashMap<String, String> channels;

    @JsonCreator
    public PxMWDeviceProfile(@JsonProperty("id") String deviceGuid, 
                             @JsonProperty("created") String createdTime, 
                             @JsonProperty("created_by")String createdByUser, 
                             @JsonProperty("modified")String modifiedTime, 
                             @JsonProperty("modified_by")String modifiedByUser,
                             HashMap<String, String> device, 
                             HashMap<String, String> channels) {
        this.deviceGuid = deviceGuid;
        this.createdTime = createdTime;
        this.createdByUser = createdByUser;
        this.modifiedTime = modifiedTime;
        this.modifiedByUser = modifiedByUser;
        this.device = device;
        this.channels = channels;
    }

    @JsonProperty("id")
    public String getDeviceGuid() {
        return deviceGuid;
    }

    @JsonProperty("created")
    public String getCreatedTime() {
        return createdTime;
    }

    @JsonProperty("created_by")
    public String getCreatedByUser() {
        return createdByUser;
    }

    @JsonProperty("modified")
    public String getModifiedTime() {
        return modifiedTime;
    }

    @JsonProperty("modified_by")
    public String getModifiedByUser() {
        return modifiedByUser;
    }

    public HashMap<String, String> getDevice() {
        return device;
    }

    public HashMap<String, String> getChannels() {
        return channels;
    }

}
