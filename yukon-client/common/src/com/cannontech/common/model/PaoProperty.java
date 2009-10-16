package com.cannontech.common.model;

public class PaoProperty {

    private int paoId;
    private String propertyName;
    private String propertyValue;
    
    public PaoProperty() {
        
    }
    
    public PaoProperty(int id, String name, String value) {
        this.paoId = id;
        this.propertyName = name;
        this.propertyValue = value;
    }
    
    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
