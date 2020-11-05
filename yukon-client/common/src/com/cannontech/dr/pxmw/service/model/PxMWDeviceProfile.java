package com.cannontech.dr.pxmw.service.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

class Device{
    public String vendor;
    public String family;
    public String role;
    public String model;
    public String model_lname;
    public String model_sname;
    public String hardware;
    public String software;
    public String mcl_id;
}

class Channel{
    public String tag;
    public String mcltag;
}

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
    private final Device device;
    private final List <Channel> channels;

    @JsonCreator
    public PxMWDeviceProfile(@JsonProperty("id") String deviceGuid, 
                             @JsonProperty("created") String createdTime, 
                             @JsonProperty("created_by") String createdByUser, 
                             @JsonProperty("modified") String modifiedTime, 
                             @JsonProperty("modified_by") String modifiedByUser,
                             Device device, 
                             List <Channel> channels) {
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

    public Device getDevice() {
        return device;
    }

    public List <Channel> getChannels() {
        return channels;
    }

}
