package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to delete a set.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteSetRequest {
    private final String operation = "remove";
    private final String setPath;

    /**
     * Creates a delete request for a set. If the path starts with a "/", it's assumed to be a full path,
     * otherwise it's
     * assumed to be the name of a set that is directly under the root "/" path.
     */
    @JsonCreator
    public DeleteSetRequest(@JsonProperty("setPath") String setPath) {
        this.setPath = (setPath.startsWith("/") ? "" : "/") + setPath;
    }

    public String getSetPath() {
        return setPath;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((setPath == null) ? 0 : setPath.hashCode());
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
        DeleteSetRequest other = (DeleteSetRequest) obj;
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (setPath == null) {
            if (other.setPath != null) {
                return false;
            }
        } else if (!setPath.equals(other.setPath)) {
            return false;
        }
        return true;
    }
}
