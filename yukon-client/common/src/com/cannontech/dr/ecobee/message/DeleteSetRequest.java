package com.cannontech.dr.ecobee.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request to delete a set.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DeleteSetRequest {
    private final String operation = "remove";
    private final String setPath;
    
    /**
     * Creates a request for a set that is directly under the root "/" path.
     */
    @JsonCreator
    public DeleteSetRequest(@JsonProperty("setPath") String setName) {
        setPath = "/" + setName;
    }
    
    /**
     * Creates a request by specifying the full path, or a set name under the "/" path.
     */
    
    public DeleteSetRequest(String setPath, boolean isFullPath) {
        if (isFullPath) {
            this.setPath = setPath;
        } else {
            this.setPath = "/" + setPath;
        }
    }
    
    public String getSetPath() {
        return setPath;
    }
    
    public String getOperation() {
        return operation;
    }
}
