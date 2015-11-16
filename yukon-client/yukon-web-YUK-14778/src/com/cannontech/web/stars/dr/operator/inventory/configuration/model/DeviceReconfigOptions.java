package com.cannontech.web.stars.dr.operator.inventory.configuration.model;

public class DeviceReconfigOptions {
    
    private String name;
    private boolean sendInService = true;
    private boolean sendOutOfService = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSendInService(boolean sendInService) {
        this.sendInService = sendInService;
    }

    public boolean isSendInService() {
        return sendInService;
    }

    public boolean isSendOutOfService() {
        return sendOutOfService;
    }

    public void setSendOutOfService(boolean sendOutOfService) {
        this.sendOutOfService = sendOutOfService;
    }
    
}