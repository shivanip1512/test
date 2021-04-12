package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWSiteDeviceV1 implements Serializable{
    private String deviceGuid;
    private String name;
   
    @JsonCreator
    public PxMWSiteDeviceV1(@JsonProperty("id") String deviceGuid, 
            @JsonProperty("name") String name) {
        this.deviceGuid = deviceGuid;
        this.name = name;
    }

    @JsonProperty("id")
    public String getDeviceUuid() {
        return deviceGuid;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }   
}
