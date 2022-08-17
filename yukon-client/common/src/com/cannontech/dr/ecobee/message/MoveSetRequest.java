package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class MoveSetRequest extends SetRequest{
    private final String setPath;
    private final String toPath;
    
    /**
     * @param setPath A full management set path (not just the set name). Should begin with the root "/".
     */
    @JsonCreator
    public MoveSetRequest(@JsonProperty("setPath") String currentPath, @JsonProperty("toPath") String newPath) {
        super("move");
        setPath = currentPath;
        toPath = newPath;
    }
    
    public String getSetPath() {
        return setPath;
    }
    
    public String getToPath() {
        return toPath;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((setPath == null) ? 0 : setPath.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((toPath == null) ? 0 : toPath.hashCode());
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
        MoveSetRequest other = (MoveSetRequest) obj;
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
        if (toPath == null) {
            if (other.toPath != null) {
                return false;
            }
        } else if (!toPath.equals(other.toPath)) {
            return false;
        }
        return true;
    }
}
