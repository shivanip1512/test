package com.cannontech.dr.honeywell.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token Response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {
    private final String accessToken;
    private final String tokenType;
    private final Long expiresIn;
    private final String scope;

    @JsonCreator
    public TokenResponse(@JsonProperty("access_token") String accessToken,
            @JsonProperty("token_type") String tokenType, @JsonProperty("expires_in") Long expiresIn,
            @JsonProperty("scope") String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
        result = prime * result + ((expiresIn == null) ? 0 : expiresIn.hashCode());
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        result = prime * result + ((tokenType == null) ? 0 : tokenType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TokenResponse other = (TokenResponse) obj;
        if (accessToken == null) {
            if (other.accessToken != null)
                return false;
        } else if (!accessToken.equals(other.accessToken))
            return false;
        if (expiresIn == null) {
            if (other.expiresIn != null)
                return false;
        } else if (!expiresIn.equals(other.expiresIn))
            return false;
        if (scope == null) {
            if (other.scope != null)
                return false;
        } else if (!scope.equals(other.scope))
            return false;
        if (tokenType == null) {
            if (other.tokenType != null)
                return false;
        } else if (!tokenType.equals(other.tokenType))
            return false;
        return true;
    }

}
