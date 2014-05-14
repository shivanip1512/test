package com.cannontech.dr.ecobee.message;

import java.util.List;

import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class HierarchyResponse extends BaseResponse {
    private final List<SetNode> sets;
    
    @JsonCreator
    public HierarchyResponse(@JsonProperty("sets") List<SetNode> sets,
                             @JsonProperty("status") Status status) {
        super(status);
        this.sets = sets;
    }
    
    public List<SetNode> getSets() {
        return sets;
    }
}
