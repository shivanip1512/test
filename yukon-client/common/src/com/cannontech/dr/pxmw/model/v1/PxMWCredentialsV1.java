package com.cannontech.dr.pxmw.model.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Credentials for authenticating with PX White.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PxMWCredentialsV1 implements Serializable {
    private final String serviceAccountId;
    private final String secret;

    @JsonCreator
    public PxMWCredentialsV1(@JsonProperty("serviceAccountId") String serviceAccountId, @JsonProperty("secret") String secret) {
        this.serviceAccountId = serviceAccountId;
        this.secret = secret;
    }

    @JsonProperty("serviceAccountId")
    public String getServiceAccountId() {
        return serviceAccountId;
    }

    @JsonProperty("secret")
    public String getSecret() {
        return secret;
    }
}
