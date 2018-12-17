package com.cannontech.amr.rfn.service.pointmapping.icd;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModelPointDefinition extends PointDefinition {
    @JsonProperty("Models")
    public List<ManufacturerModel> models;

    public List<ManufacturerModel> getModels() {
        return models;
    }
}