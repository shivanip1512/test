package com.cannontech.message;

import com.cannontech.common.pao.PaoIdentifier;

public class DeviceCreationDescriptor {

    private final int paoId;
    private final String category;
    private final String deviceType;
    
    public DeviceCreationDescriptor(int paoId, String category, String deviceType) {
        this.paoId = paoId;
        this.category = category;
        this.deviceType = deviceType;
    }

    public DeviceCreationDescriptor(PaoIdentifier paoIdentifier) {
        this.paoId = paoIdentifier.getPaoId();
        this.category = paoIdentifier.getPaoType().getPaoCategory().getDbString();
        this.deviceType = paoIdentifier.getPaoType().getDbString();
    }
    
    public int getPaoId() {
        return paoId;
    }

    public String getCategory() {
        return category;
    }

    public String getDeviceType() {
        return deviceType;
    }
    
    public String toString() {
        return String.format("DeviceCreationDescriptor [paoId=%d, category=%s, deviceType=%s]",
                             paoId,
                             category,
                             deviceType);
    }
}
