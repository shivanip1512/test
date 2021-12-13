package com.cannontech.dr.eatonCloud.model.v1;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EatonCloudSecretValueV1 extends EatonCloudSecretV1 {
    public String secret;
    
    public EatonCloudSecretValueV1(@JsonProperty("name") String name, @JsonProperty("expiry") Date expiryTime,
            @JsonProperty("value") String secret) {
        super(name, expiryTime);
        this.secret = secret;
    }
    
    @JsonProperty("value") 
    public String getSecret() {
        return secret;
    }
}
