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
    private final String user;
    private final String password;
    private final String applicationId;

    @JsonCreator
    public PxMWCredentialsV1(@JsonProperty("user") String user, @JsonProperty("password") String password,
            @JsonProperty("applicationId") String applicationId) {
        this.user = user;
        this.password = password;
        this.applicationId = applicationId;
    }

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonProperty("applicationId")
    public String getApplicationId() {
        return applicationId;
    }
}
