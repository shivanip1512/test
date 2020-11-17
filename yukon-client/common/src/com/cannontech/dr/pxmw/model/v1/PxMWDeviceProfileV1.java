package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWDeviceProfileV1 implements Serializable {
    private final String profileGuid;
    private final String createdTime;
    private final String createdByUser;
    private final String modifiedTime;
    private final String modifiedByUser;
    private final PxMWDeviceV1 device;
    private final List <PxMWChannelV1> channels;

    @JsonCreator
    public PxMWDeviceProfileV1(@JsonProperty("id") String profileGuid, 
                             @JsonProperty("created") String createdTime, 
                             @JsonProperty("created_by") String createdByUser, 
                             @JsonProperty("modified") String modifiedTime, 
                             @JsonProperty("modified_by") String modifiedByUser,
                             @JsonProperty("device") PxMWDeviceV1 device, 
                             @JsonProperty("channels") List <PxMWChannelV1> channels) {
        this.profileGuid = profileGuid;
        this.createdTime = createdTime;
        this.createdByUser = createdByUser;
        this.modifiedTime = modifiedTime;
        this.modifiedByUser = modifiedByUser;
        this.device = device;
        this.channels = channels;
    }

    @JsonProperty("id")
    public String getDeviceProfileGuid() {
        return profileGuid;
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

    @JsonProperty("device")
    public PxMWDeviceV1 getDevice() {
        return device;
    }
    
    @JsonProperty("channels")
    public List <PxMWChannelV1> getChannels() {
        return channels;
    }
}
