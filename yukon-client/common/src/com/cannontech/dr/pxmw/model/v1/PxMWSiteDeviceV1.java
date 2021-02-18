package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWSiteDeviceV1 implements Serializable{
    private String deviceUuid;
    private String profileUuid;
    private String name;
    private String macAddress;
    private String serial;
    private String asset;
    private String family;
    private String hardware;
    private String model;
    private String modelLname;
    private String modelSname;
    private String software;
    private String movendordel;
    
    @JsonCreator
    public PxMWSiteDeviceV1(@JsonProperty("id") String deviceUuid, @JsonProperty("profile_id") String profileUuid,
            @JsonProperty("name") String name, @JsonProperty("mac_address") String macAddress,
            @JsonProperty("serial") String serial, @JsonProperty("asset") String asset,
            @JsonProperty("family") String family, @JsonProperty("hardware") String hardware, @JsonProperty("model") String model,
            @JsonProperty("model_lname") String modelLname, @JsonProperty("model_sname") String modelSname, @JsonProperty("software") String software,
            @JsonProperty("movendordel") String movendordel) {
        this.deviceUuid = deviceUuid;
        this.profileUuid = profileUuid;
        this.name = name;
        this.macAddress = macAddress;
        this.serial = serial;
        this.asset = asset;
        this.family = family;
        this.hardware = hardware;
        this.model = model;
        this.modelLname = modelLname;
        this.modelSname = modelSname;
        this.software = software;
        this.movendordel = movendordel;
    }

    @JsonProperty("id")
    public String getDeviceUuid() {
        return deviceUuid;
    }

    @JsonProperty("profile_id")
    public String getProfileUuid() {
        return profileUuid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("mac_address")
    public String getMacAddress() {
        return macAddress;
    }

    @JsonProperty("serial")
    public String getSerial() {
        return serial;
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
    public String getModelLname() {
        return modelLname;
    }

    @JsonProperty("model_sname")
    public String getModelSname() {
        return modelSname;
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
