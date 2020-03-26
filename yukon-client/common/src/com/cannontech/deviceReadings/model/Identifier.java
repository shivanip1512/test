package com.cannontech.deviceReadings.model;

import com.cannontech.common.pao.PaoType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Identifier {

    private String value;
    private IdentifierType identifierType;
    private String sensorManufacturer;
    private String sensorModel;
    private PaoType paoType;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(IdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public String getSensorManufacturer() {
        return sensorManufacturer;
    }

    public void setSensorManufacturer(String sensorManufacturer) {
        this.sensorManufacturer = sensorManufacturer;
    }

    public String getSensorModel() {
        return sensorModel;
    }

    public void setSensorModel(String sensorModel) {
        this.sensorModel = sensorModel;
    }

    public PaoType getPaoType() {
        return paoType;
    }

    public void setPaoType(PaoType paoType) {
        this.paoType = paoType;
    }
}
