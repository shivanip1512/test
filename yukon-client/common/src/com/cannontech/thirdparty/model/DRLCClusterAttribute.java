package com.cannontech.thirdparty.model;

public enum DRLCClusterAttribute {

    UTILITY_ENROLLMENT_GROUP(0x00,0x20,true,true),
    START_RANDOMIZE_MINUTES(0x01,0x20,true,true),
    STOP_RANDOMIZE_MINTES(0x02,0x20,true,true),
    DEVICE_CLASS(0x03,0x21,true,false);
    
    private int id;
    private int type;
    private boolean canRead;
    private boolean canWrite;
    
    private DRLCClusterAttribute(int id, int type, boolean canRead, boolean canWrite) {
        this.id = id;
        this.type = type;
        this.canRead = canRead;
        this.canWrite = canWrite;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }
}
