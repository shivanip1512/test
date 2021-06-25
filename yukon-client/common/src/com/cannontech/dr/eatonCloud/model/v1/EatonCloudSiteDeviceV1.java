package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EatonCloudSiteDeviceV1 implements Serializable {
    private String deviceGuid;
    private String profileGuid;
    private String name;
    private String serial;
    private String vendor;
    private String family;
    private String role;
    private String model;
    private String modelLongname;
    private String modelShortname;
    private String hardware;
    private String software;
    private String cloudEnabled;

    @JsonCreator
    public EatonCloudSiteDeviceV1(@JsonProperty("id") String deviceGuid, @JsonProperty("profile_id") String profileGuid,
            @JsonProperty("name") String name, @JsonProperty("serial") String serial,
            @JsonProperty("vendor") String vendor, @JsonProperty("family") String family, 
            @JsonProperty("role") String role, @JsonProperty("model") String model, 
            @JsonProperty("model_lname") String modelLongname, @JsonProperty("model_sname") String modelShortname, 
            @JsonProperty("hardware") String hardware, @JsonProperty("software") String software, 
            @JsonProperty("cloud_enabled") String cloudEnabled) {
        this.deviceGuid = deviceGuid;
        this.profileGuid = profileGuid;
        this.name = name;
        this.serial = serial;
        this.vendor = vendor;
        this.family = family;
        this.role = role;
        this.model = model;
        this.modelLongname = modelLongname;
        this.modelShortname = modelShortname;
        this.hardware = hardware;
        this.software = software;
        this.cloudEnabled = cloudEnabled;
    }

    @JsonProperty("id")
    public String getDeviceGuid() {
        return deviceGuid;
    }

    @JsonProperty("profile_id")
    public String getProfileGuid() {
        return profileGuid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("serial")
    public String getSerial() {
        return serial;
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
    public String getModelLongname() {
        return modelLongname;
    }

    @JsonProperty("model_sname")
    public String getModelShortname() {
        return modelShortname;
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
    public String getCloudEnabled() {
        return cloudEnabled;
    }
}