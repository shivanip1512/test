package com.cannontech.common.model;

public class SiteInformation {
    
    private String feeder;
    private String pole;
    private String transformerSize;
    private String serviceVoltage;
    private String substationName;
    
    public String getFeeder() {
        return feeder;
    }
    
    public void setFeeder(String feeder) {
        this.feeder = feeder;
    }
    
    public String getPole() {
        return pole;
    }
    
    public void setPole(String pole) {
        this.pole = pole;
    }
    
    public String getTransformerSize() {
        return transformerSize;
    }
    
    public void setTransformerSize(String transformerSize) {
        this.transformerSize = transformerSize;
    }
    
    public String getServiceVoltage() {
        return serviceVoltage;
    }
    
    public void setServiceVoltage(String serviceVoltage) {
        this.serviceVoltage = serviceVoltage;
    }
    
    public String getSubstationName() {
        return substationName;
    }
    
    public void setSubstationName(String substationName) {
        this.substationName = substationName;
    }
}