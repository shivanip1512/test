package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWSiteDeviceV1 implements Serializable {
    private String deviceGuid;
    private String profileId;
    private String name;
    private String macAddress;
    private String asset;
    private String family;
    private String hardware;
    private String model;
    private String modelLongName;
    private String modelShortName;
    private String software;
    private String movendordel;

    @JsonCreator
    public PxMWSiteDeviceV1(@JsonProperty("id") String deviceGuid, @JsonProperty("profile_id") String profileId,
            @JsonProperty("name") String name, @JsonProperty("mac_address") String macAddress,
            @JsonProperty("asset") String asset, @JsonProperty("family") String family,
            @JsonProperty("hardware") String hardware, @JsonProperty("model") String model,
            @JsonProperty("model_lname") String modelLongName, @JsonProperty("model_sname") String modelShortName,
            @JsonProperty("software") String software, @JsonProperty("movendordel") String movendordel) {
        this.deviceGuid = deviceGuid;
        this.profileId = profileId;
        this.name = name;
        this.macAddress = macAddress;
        this.asset = asset;
        this.family = family;
        this.hardware = hardware;
        this.model = model;
        this.modelLongName = modelLongName;
        this.modelShortName = modelShortName;
        this.software = software;
        this.movendordel = movendordel;
    }

    @JsonProperty("id")
    public String getDeviceGuid() {
        return deviceGuid;
    }

    @JsonProperty("profile_id")
    public String getProfileId() {
        return profileId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("mac_address")
    public String getMacAddress() {
        return macAddress;
    }

    @JsonProperty("asset")
    public String getAsset() {
        return asset;
    }

    @JsonProperty("family")
    public String getFamily() {
        return family;
    }

    @JsonProperty("hardware")
    public String getHardware() {
        return hardware;
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

    @JsonProperty("software")
    public String getSoftware() {
        return software;
    }

    @JsonProperty("movendordel")
    public String getMovendordel() {
        return movendordel;
    }

}
