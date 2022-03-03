package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Credentials for authenticating with PX White.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EatonCloudCredentialsV1 implements Serializable {
    private final String serviceAccountId;
    private final String secret;

    @JsonCreator
    public EatonCloudCredentialsV1(@JsonProperty("service_Account_Id") String serviceAccountId, @JsonProperty("secret") String secret) {
        this.serviceAccountId = serviceAccountId;
        this.secret = secret;
    }

    @JsonProperty("service_Account_Id")
    public String getServiceAccountId() {
        return serviceAccountId;
    }

    @JsonProperty("secret")
    public String getSecret() {
        return secret;
    }
}
