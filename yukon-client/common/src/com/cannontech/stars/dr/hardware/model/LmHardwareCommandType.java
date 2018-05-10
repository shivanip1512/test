package com.cannontech.stars.dr.hardware.model;

public enum LmHardwareCommandType {
    CONFIG,
    IN_SERVICE,
    OUT_OF_SERVICE,
    TEMP_OUT_OF_SERVICE,
    CANCEL_TEMP_OUT_OF_SERVICE,
    READ_NOW,
    PERFORMANCE_VERIFICATION,
    SHED,
    RESTORE,
    
    //Power Quality Response
    PQR_ENABLE(0x66, 1),
    PQR_LOV_PARAMETERS(0x61, 8),
    PQR_LOV_EVENT_DURATION(0x67, 5),
    PQR_LOV_DELAY_DURATION(0x68, 5),
    PQR_LOF_PARAMETERS(0x65, 8),
    PQR_LOF_EVENT_DURATION(0x67, 5),
    PQR_LOF_DELAY_DURATION(0x68, 5),
    PQR_EVENT_SEPARATION(0x69, 2),
    ;
    
    private final Byte configNumber;
    private final Byte dataLength;
    
    private LmHardwareCommandType() {
        configNumber = null;
        dataLength = null;
    }
    
    private LmHardwareCommandType(int configNumber, int dataLength) {
        this.configNumber = (byte) configNumber;
        this.dataLength = (byte) dataLength;
    }
    
    public boolean hasConfigNumber() {
        return configNumber != null;
    }
    
    public byte getConfigNumber() {
        return configNumber;
    }
    
    public byte getDataLength() {
        return dataLength;
    }
}