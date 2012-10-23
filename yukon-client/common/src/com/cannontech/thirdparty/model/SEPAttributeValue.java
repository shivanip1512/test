package com.cannontech.thirdparty.model;

public class SEPAttributeValue {

    private DRLCClusterAttribute attribute;
    private int value;

    public SEPAttributeValue(DRLCClusterAttribute attribute, int value) {
        this.attribute = attribute;
        this.value = value;
    }
    
    public DRLCClusterAttribute getAttribute() {
        return attribute;
    }
    public int getValue() {
        return value;
    }
}
