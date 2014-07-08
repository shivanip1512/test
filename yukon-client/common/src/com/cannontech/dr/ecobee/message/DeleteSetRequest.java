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
    
}
