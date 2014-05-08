package com.cannontech.dr.ecobee.message;

public final class MoveSetRequest {
    private final String operation = "move";
    private final String setPath;
    private final String toPath;
    
    /**
     * @param setPath A full management set path (not just the set name). Should begin with the root "/".
     */
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
