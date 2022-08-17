package com.cannontech.dr.ecobee.message.partial;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SetNode {
    private final String setName;
    private final String setPath;
    private final List<SetNode> children;
    private final List<String> thermostats;
    
    @JsonCreator
    public SetNode(@JsonProperty("setName") String setName, 
                   @JsonProperty("setPath") String setPath,
                   @JsonProperty("children") List<SetNode> children, 
                   @JsonProperty("thermostats") List<String> thermostats) {
        this.setName = setName;
        this.setPath = setPath;
        this.children = children;
        this.thermostats = thermostats;
    }

    public String getSetName() {
        return setName;
    }

    public String getSetPath() {
        return setPath;
    }

    public List<SetNode> getChildren() {
        return children;
    }

    public List<String> getThermostats() {
        return thermostats;
    }
}
