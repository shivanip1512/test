package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to delete a set.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DeleteSetRequest extends SetRequest{
    private final String setPath;
    
    /**
     * Creates a delete request for a set. If the path starts with a "/", it's assumed to be a full path, otherwise it's
     * assumed to be the name of a set that is directly under the root "/" path.
     */
    @JsonCreator
    public DeleteSetRequest(@JsonProperty("setPath") String setPath) {
        super("remove");
        this.setPath = (setPath.startsWith("/") ? "" : "/") + setPath;
    }
    
    public String getSetPath() {
        return setPath;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((setPath == null) ? 0 : setPath.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
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
        if (setPath == null) {
            if (other.setPath != null) {
                return false;
            }
        } else if (!setPath.equals(other.setPath)) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
       
        return true;
    }
}
