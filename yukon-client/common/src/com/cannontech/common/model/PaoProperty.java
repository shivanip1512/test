package com.cannontech.common.model;

import com.cannontech.common.pao.PaoIdentifier;

public class PaoProperty {

    private PaoIdentifier paoIdentifier;
    private PaoPropertyName propertyName;
    private String propertyValue;
    
    public PaoProperty() {
        
    }
    
    public PaoProperty(PaoIdentifier identifier, PaoPropertyName name, String value) {
        this.paoIdentifier = identifier;
        this.propertyName = name;
        this.propertyValue = value;
    }
    
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public void setPaoIdentifier(PaoIdentifier identifier) {
        this.paoIdentifier = identifier;
    }

    public PaoPropertyName getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(PaoPropertyName propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
