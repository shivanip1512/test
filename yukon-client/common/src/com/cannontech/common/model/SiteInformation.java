package com.cannontech.common.model;

import org.apache.commons.lang3.StringUtils;

public class SiteInformation {
    
    private String feeder;
    private String pole;
    private String transformerSize;
    private String serviceVoltage;
    private String substationName;
    
    // Null check is to make sure null is never set in feeder, pole, transfomerSize, serviceVoltage for YUK-14417
    public String getFeeder() {
        if (StringUtils.isBlank(feeder)) {
            return "";
        }
        return feeder;
    }
    
    public void setFeeder(String feeder) {
        this.feeder = feeder;
    }
    
    public String getPole() {
        if (StringUtils.isBlank(pole)) {
            return "";
        }
        return pole;
    }
    
    public void setPole(String pole) {
        this.pole = pole;
    }
    
    public String getTransformerSize() {
        if (StringUtils.isBlank(transformerSize)) {
            return "";
        }
        return transformerSize;
    }
    
    public void setTransformerSize(String transformerSize) {
        this.transformerSize = transformerSize;
    }
    
    public String getServiceVoltage() {
        if (StringUtils.isBlank(serviceVoltage)) {
            return "";
        }
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