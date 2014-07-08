package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to create a new set with the specified name beneath the root set ("/").
 */
public final class CreateSetRequest extends SetRequest{
    private final String setName;
    private final String parentPath = "/";
    
    @JsonCreator
    public CreateSetRequest(@JsonProperty("setName") String setName) {
        super("add");
        this.setName = setName;
    }
    
    public String getSetName() {
        return setName;
    }
    
    public String getParentPath() {
        return parentPath;
    }
}
