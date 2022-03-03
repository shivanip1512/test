package com.cannontech.dr.eatonCloud.model.v1;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response object for PX White token and token renewal requests.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public final class EatonCloudTokenV1 implements Serializable {
    private final String token;
    
    @JsonCreator
    public EatonCloudTokenV1(@JsonProperty("Token") String token) {
        this.token = token;
    }
    
    @JsonProperty("Token")
    public String getToken() {
        return token;
    }
    
    @Override
    public String toString() {
        return "EatonCloudTokenV1 [token=" + StringUtils.abbreviateMiddle(token, "...", 49) + "]";
    }
}
