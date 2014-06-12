package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to create a new set with the specified name beneath the root set ("/").
 */
public final class CreateSetRequest {
    private final String operation = "add";
    private final String setName;
    private final String parentPath = "/";

    @JsonCreator
    public CreateSetRequest(@JsonProperty("setName") String setName) {
        this.setName = setName;
    }

    public String getOperation() {
        return operation;
    }

    public String getSetName() {
        return setName;
    }

    public String getParentPath() {
        return parentPath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((parentPath == null) ? 0 : parentPath.hashCode());
        result = prime * result + ((setName == null) ? 0 : setName.hashCode());
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
        CreateSetRequest other = (CreateSetRequest) obj;
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (parentPath == null) {
            if (other.parentPath != null) {
                return false;
            }
        } else if (!parentPath.equals(other.parentPath)) {
            return false;
        }
        if (setName == null) {
            if (other.setName != null) {
                return false;
            }
        } else if (!setName.equals(other.setName)) {
            return false;
        }
        return true;
    }
}
