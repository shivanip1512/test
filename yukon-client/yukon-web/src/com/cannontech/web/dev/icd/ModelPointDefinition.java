package com.cannontech.web.dev.icd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelPointDefinition extends PointDefinition {
    @JsonProperty("Models")
    public List<Model> models;

    public List<Model> getModels() {
        return models;
    }
}