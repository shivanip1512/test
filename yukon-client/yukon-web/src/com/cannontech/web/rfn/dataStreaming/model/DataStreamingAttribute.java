package com.cannontech.web.rfn.dataStreaming.model;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class DataStreamingAttribute {
    private BuiltInAttribute attribute;
    private Boolean attributeOn = Boolean.FALSE;
    private int interval;
    
    public Boolean getAttributeOn() {
        return attributeOn;
    }
    public void setAttributeOn(Boolean attributeOn) {
        this.attributeOn = attributeOn;
    }
    public int getInterval() {
        return interval;
    }
    public void setInterval(int interval) {
        this.interval = interval;
    }
    public BuiltInAttribute getAttribute() {
        return attribute;
    }
    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

}
