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
}
