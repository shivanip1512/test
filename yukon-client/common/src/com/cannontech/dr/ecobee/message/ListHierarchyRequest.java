package com.cannontech.dr.ecobee.message;

public class ListHierarchyRequest {
    private final String operation = "list";
    private final String setPath = "/";
    private final boolean recursive = true;
    private final boolean includeThermostats = true;

    public String getOperation() {
        return operation;
    }

    public String getSetPath() {
        return setPath;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public boolean isIncludeThermostats() {
        return includeThermostats;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (includeThermostats ? 1231 : 1237);
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + (recursive ? 1231 : 1237);
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
        ListHierarchyRequest other = (ListHierarchyRequest) obj;
        if (includeThermostats != other.includeThermostats) {
            return false;
        }
        if (operation == null) {
            if (other.operation != null) {
                return false;
            }
        } else if (!operation.equals(other.operation)) {
            return false;
        }
        if (recursive != other.recursive) {
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
