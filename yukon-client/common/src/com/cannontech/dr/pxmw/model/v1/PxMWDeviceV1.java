package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWDeviceV1 implements Serializable {
    private final String vendor;
    private final String family;
    private final String role;
    private final String model;
    private final String modelLname;
    private final String modelSname;
    private final String hardware;
    private final String software;
    private final String mclId;

    @JsonCreator
    public PxMWDeviceV1(@JsonProperty("vendor") String vendor, @JsonProperty("family") String family,
            @JsonProperty("role") String role, @JsonProperty("model") String model,
            @JsonProperty("model_lname") String modelLname, @JsonProperty("model_sname") String modelSname,
            @JsonProperty("hardware") String hardware, @JsonProperty("software") String software,
            @JsonProperty("mclId") String mclId) {
        this.vendor = vendor;
        this.family = family;
        this.role = role;
        this.model = model;
        this.modelLname = modelLname;
        this.modelSname = modelSname;
        this.hardware = hardware;
        this.software = software;
        this.mclId = mclId;
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

    @JsonProperty("model_sname")
    public String getModelSname() {
        return modelSname;
    }

    @JsonProperty("model_lname")
    public String getModelLname() {
        return modelLname;
    }

    @JsonProperty("hardware") 
    public String getHardware() {
        return hardware;
    }
    
    @JsonProperty("software")
    public String getSoftware() {
        return software;
    }

    @JsonProperty("mclId")
    public String getMclId() {
        return mclId;
    }
}
