package com.cannontech.services.systemDataPublisher.yaml.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScalarField {

    @JsonProperty("Yukon")
    public List<DictionariesField> yukonDictionaries;
    
    @JsonProperty("Network Manager")
    public List<DictionariesField> nmDictionaries;

    @JsonProperty("Other")
    public List<DictionariesField> otherSettingsDictionaries;
    
    public List<DictionariesField> getYukonDictionaries() {
        return yukonDictionaries;
    }

    public List<DictionariesField> getNmDictionaries() {
        return nmDictionaries;
    }

    public List<DictionariesField> getOtherSettingsDictionaries() {
        return otherSettingsDictionaries;
    }
}
