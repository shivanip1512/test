package com.cannontech.web.tools.commander.model;

public enum CommandTarget {
    
    DEVICE,
    LOAD_GROUP,
    EXPRESSCOM,
    VERSACOM;
    
    public boolean isPao() {
        return this == DEVICE || this == LOAD_GROUP;
    }
    
    public boolean isRoute() {
        return this == VERSACOM || this == EXPRESSCOM;
    }
    
    public boolean isSerialNumber() {
        return this == VERSACOM || this == EXPRESSCOM;
    }
    
    public String getRequestTextKey() {
        if (isRoute()) {
            return "yukon.web.modules.tools.commander.request.text.serialNumber";
        } else {
            return "yukon.web.modules.tools.commander.request.text." + name();
        }
    }
}