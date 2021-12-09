package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudSecretV1 implements Serializable {
    private String name;
    private String expiryTime;

    public EatonCloudSecretV1(@JsonProperty("name") String name, @JsonProperty("expiry") String expiryTime) {
        this.name = name;
        this.expiryTime = expiryTime;
    }

    @JsonProperty("name") 
    public String getName() {
        return name;
    }

    @JsonProperty("expiry") 
    public String getExpiryTime() {
        return expiryTime;
    }
}
