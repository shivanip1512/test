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
}
