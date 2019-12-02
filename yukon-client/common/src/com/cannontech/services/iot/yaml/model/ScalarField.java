package com.cannontech.services.iot.yaml.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScalarField {

    @JsonProperty("Yukon")
    public List<DictionariesField> yukonDictionaries;
    
    @JsonProperty("Network Manager")
    public List<DictionariesField> nmDictionaries;

    public List<DictionariesField> getYukonDictionaries() {
        return yukonDictionaries;
    }

    public List<DictionariesField> getNmDictionaries() {
        return nmDictionaries;
    }
}
