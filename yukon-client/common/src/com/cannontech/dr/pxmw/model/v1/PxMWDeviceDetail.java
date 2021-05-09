package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWDeviceDetail implements Serializable {
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
    private String modelCanonicalName;
    private String modelLongDisplayName;
    private String modelShortDisplayName;
    private String hardware;
    private String software;
    private boolean cloudEnabled;
    private String devices;
    
    public PxMWDeviceDetail(@JsonProperty("Id") String id, @JsonProperty("ProfileId") String profileId,
            @JsonProperty("PublisherId") String publisherId, @JsonProperty("SiteId") String siteId,
            @JsonProperty("Name") String name, @JsonProperty("Serial") String serial,
            @JsonProperty("Asset") String asset, @JsonProperty("MacAddress") String macAddress,
            @JsonProperty("Vendor") String vendor, @JsonProperty("Family") String family, @JsonProperty("Role") String role,
            @JsonProperty("ModelCanonicalName") String modelCanonicalName,
            @JsonProperty("ModelLongDisplayName") String modelLongDisplayName,
            @JsonProperty("ModelShortDisplayName") String modelShortDisplayName, @JsonProperty("Hardware") String hardware,
            @JsonProperty("Software") String software, @JsonProperty("CloudEnabled") boolean cloudEnabled,
            @JsonProperty("Devices") String devices) {
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
        this.modelCanonicalName = modelCanonicalName;
        this.modelLongDisplayName = modelLongDisplayName;
        this.modelShortDisplayName = modelShortDisplayName;
        this.hardware = hardware;
        this.software = software;
        this.cloudEnabled = cloudEnabled;
        this.devices = devices;
    }

    @JsonProperty("Id") 
    public String getId() {
        return id;
    }

    @JsonProperty("ProfileId") 
    public String getProfileId() {
        return profileId;
    }

    @JsonProperty("PublisherId") 
    public String getPublisherId() {
        return publisherId;
    }

    @JsonProperty("SiteId") 
    public String getSiteId() {
        return siteId;
    }

    @JsonProperty("Name") 
    public String getName() {
        return name;
    }

    @JsonProperty("Serial") 
    public String getSerial() {
        return serial;
    }

    @JsonProperty("Asset") 
    public String getAsset() {
        return asset;
    }

    @JsonProperty("MacAddress") 
    public String getMacAddress() {
        return macAddress;
    }

    @JsonProperty("Vendor") 
    public String getVendor() {
        return vendor;
    }

    @JsonProperty("Family") 
    public String getFamily() {
        return family;
    }

    @JsonProperty("Role") 
    public String getRole() {
        return role;
    }

    @JsonProperty("ModelCanonicalName") 
    public String getModelCanonicalName() {
        return modelCanonicalName;
    }

    @JsonProperty("ModelLongDisplayName") 
    public String getModelLongDisplayName() {
        return modelLongDisplayName;
    }

    @JsonProperty("ModelShortDisplayName") 
    public String getModelShortDisplayName() {
        return modelShortDisplayName;
    }

    @JsonProperty("Hardware") 
    public String getHardware() {
        return hardware;
    }

    @JsonProperty("Software") 
    public String getSoftware() {
        return software;
    }

    @JsonProperty("CloudEnabled") 
    public boolean isCloudEnabled() {
        return cloudEnabled;
    }

    @JsonProperty("Devices") 
    public String getDevices() {
        return devices;
    }
}
