package com.cannontech.web.rfn.dataStreaming.model;

public class DataStreamingAttribute {
    private String name;
    private Boolean attributeOn = Boolean.FALSE;
    private int interval;
    
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Boolean getAttributeOn() {
        return attributeOn;
    }
    public Boolean isAttributeOn() {
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

}
