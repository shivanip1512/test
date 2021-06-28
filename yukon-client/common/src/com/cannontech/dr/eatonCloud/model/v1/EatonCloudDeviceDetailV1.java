package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EatonCloudDeviceDetailV1 implements Serializable {
    private String id;
    private String profileId;
    private String publisherId;
    private String siteId;
    private String name; 
    private String serial;
    private String asset;
    private String macAddress;
    private String vendor; 
    private String family;
    private String role;
    private String model;
    private String modelLongName;
    private String modelShortName;
    private String hardware;
    private String software;
    private boolean cloudEnabled;
    private String devices;
    
    public EatonCloudDeviceDetailV1(@JsonProperty("id") String id, @JsonProperty("profile_id") String profileId,
            @JsonProperty("publisher_id") String publisherId, @JsonProperty("site_id") String siteId,
            @JsonProperty("name") String name, @JsonProperty("serial") String serial,
            @JsonProperty("asset") String asset, @JsonProperty("mac") String macAddress,
            @JsonProperty("vendor") String vendor, @JsonProperty("family") String family,
            @JsonProperty("role") String role, @JsonProperty("model") String model,
            @JsonProperty("model_lname") String modelLongName, @JsonProperty("model_sname") String modelShortName,
            @JsonProperty("hardware") String hardware, @JsonProperty("software") String software, 
            @JsonProperty("cloud_enabled") boolean cloudEnabled, @JsonProperty("devices") String devices) {
        this.id = id;
        this.profileId = profileId;
        this.publisherId = publisherId;
        this.siteId = siteId;
        this.name = name;
        this.serial = serial;
        this.asset = asset;
        this.macAddress = macAddress;
        this.vendor = vendor;
        this.family = family;
        this.role = role;
        this.model = model;
        this.modelLongName = modelLongName;
        this.modelShortName = modelShortName;
        this.hardware = hardware;
        this.software = software;
        this.cloudEnabled = cloudEnabled;
        this.devices = devices;
    }

    @JsonProperty("id") 
    public String getId() {
        return id;
    }

    @JsonProperty("profile_id") 
    public String getProfileId() {
        return profileId;
    }

    @JsonProperty("publisher_id") 
    public String getPublisherId() {
        return publisherId;
    }

    @JsonProperty("site_id") 
    public String getSiteId() {
        return siteId;
    }

    @JsonProperty("name") 
    public String getName() {
        return name;
    }

    @JsonProperty("serial") 
    public String getSerial() {
        return serial;
    }

    @JsonProperty("asset") 
    public String getAsset() {
        return asset;
    }

    @JsonProperty("mac") 
    public String getMacAddress() {
        return macAddress;
    }

    @JsonProperty("vendor") 
    public String getVendor() {
        return vendor;
    }

    @JsonProperty("family") 
    public String getFamily() {
        return family;
    }

    @JsonProperty("role") 
    public String getRole() {
        return role;
    }

    @JsonProperty("model") 
    public String getModel() {
        return model;
    }

    @JsonProperty("model_lname") 
    public String getModelLongName() {
        return modelLongName;
    }

    @JsonProperty("model_sname") 
    public String getModelShortName() {
        return modelShortName;
    }

    @JsonProperty("hardware") 
    public String getHardware() {
        return hardware;
    }

    @JsonProperty("software") 
    public String getSoftware() {
        return software;
    }

    @JsonProperty("cloud_enabled") 
    public boolean isCloudEnabled() {
        return cloudEnabled;
    }

    @JsonProperty("devices") 
    public String getDevices() {
        return devices;
    }
}