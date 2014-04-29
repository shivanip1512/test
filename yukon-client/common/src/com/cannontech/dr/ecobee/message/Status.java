package com.cannontech.dr.ecobee.message;

import com.cannontech.dr.ecobee.service.EcobeeStatusCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This status object is included in most responses from the Ecobee API.
 * @param code A code of 0 indicates a successfuly operation. Any other code is an error code.
 * @param message Additional error details.
 * @see EcobeeStatusCode
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public final class Status {
    private final int code;
    private final String message;
    
    @JsonCreator
    public Status(@JsonProperty("code") int code, @JsonProperty("message") String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
