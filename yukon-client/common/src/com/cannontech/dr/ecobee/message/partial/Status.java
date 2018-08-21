package com.cannontech.dr.ecobee.message.partial;

import com.cannontech.dr.ecobee.service.EcobeeStatusCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This status object is included in most responses from the Ecobee API.
 * @param code A code of 0 indicates a successful operation. Any other code is an error code.
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + code;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Status other = (Status) obj;
        if (code != other.code) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        return true;
    }
    
}
