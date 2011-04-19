package com.cannontech.thirdparty.model;

public class SEPControlResponse {

    private int deviceCount;
    private int acknowledgeCount;

    public SEPControlResponse(int deviceCount, int acknowledgeCount) {
        this.deviceCount = deviceCount;
        this.acknowledgeCount = acknowledgeCount;
    }
    
    public int getDeviceCount() {
        return deviceCount;
    }
    public int getAcknowledgeCount() {
        return acknowledgeCount;
    }
}
