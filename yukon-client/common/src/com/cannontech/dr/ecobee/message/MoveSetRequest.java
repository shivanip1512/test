package com.cannontech.dr.ecobee.message;

public final class MoveSetRequest {
    private final String operation = "move";
    private final String setPath;
    private final String toPath;
    
    public MoveSetRequest(String currentPath, String newPath) {
        setPath = currentPath;
        toPath = newPath;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getSetPath() {
        return setPath;
    }
    
    public String getToPath() {
        return toPath;
    }
}
