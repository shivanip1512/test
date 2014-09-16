package com.cannontech.web.tools.commander.model;

public enum CommandType {
    
    DEVICE,
    LOAD_GROUP,
    EXPRESSCOM,
    VERSACOM;
    
    public boolean hasPao() {
        return this == DEVICE || this == LOAD_GROUP;
    }
    
    public boolean hasRoute() {
        return this == VERSACOM || this == EXPRESSCOM;
    }
    
    public boolean hasSerialNumber() {
        return this == VERSACOM || this == EXPRESSCOM;
    }
    
    public String getRequestTextKey() {
        if (hasRoute()) {
            return "yukon.web.modules.tools.commander.request.text.serialNumber";
        } else {
            return "yukon.web.modules.tools.commander.request.text." + name();
        }
    }
}