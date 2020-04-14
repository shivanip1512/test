package com.cannontech.services.systemDataPublisher.yaml.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScalarField {

    @JsonProperty("Yukon")
    public List<CloudDataConfiguration> yukonConfigurations;
    
    @JsonProperty("Network Manager")
    public List<CloudDataConfiguration> nmConfigurations;

    @JsonProperty("Other")
    public List<CloudDataConfiguration> otherConfigurations;
    
    public List<CloudDataConfiguration> getYukonConfigurations() {
        return yukonConfigurations;
    }

    public List<CloudDataConfiguration> getNmConfigurations() {
        return nmConfigurations;
    }

    public List<CloudDataConfiguration> getOtherConfigurations() {
        return otherConfigurations;
    }
}
