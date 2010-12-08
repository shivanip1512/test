package com.cannontech.web.stars.dr.operator.inventoryOperations.deviceReconfig.model;

public class DeviceReconfigOptions {
    
    private String name;
    private boolean sendInService = true;

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
    
}