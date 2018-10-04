package com.cannontech.dr.pxwhite.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes a list of data channels and their meta-data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxWhiteDeviceChannels {
    /**
     * The deserialization from json only seems to work correctly if this is a non-final field with getter and setter.
     * When this a final field and set in the constructor with @JsonCreator, serialization fails.
     */
    private List<PxWhiteDeviceChannel> channels;
    
    @JsonProperty("channels")
    public void setChannels(List<PxWhiteDeviceChannel> channels) {
        this.channels = channels;
    }
    
    @JsonProperty("channels")
    public List<PxWhiteDeviceChannel> getChannels() {
        return channels;
    }
}
