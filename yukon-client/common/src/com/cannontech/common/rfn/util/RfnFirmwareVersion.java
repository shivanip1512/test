package com.cannontech.common.rfn.util;

public enum RfnFirmwareVersion {
    FW_9_0(9, 0),
    FW_9_1(9, 1),
    FW_9_3(9, 3),
    FW_9_4(9, 4),
    ;
    
    private int major;
    private int minor;
    
    RfnFirmwareVersion(int major, int minor) {
        if (major < 9) {
            throw new IllegalArgumentException("Major version must be 9 or greater");
        }
        if (minor < 0 || minor > 9) {
            throw new IllegalArgumentException("Minor version must be 0 to 9");
        }
        this.major = major;
        this.minor = minor;
    }
    
    public boolean lessOrEqualTo(Double version) {
        if (version == null) {
            return false;
        }
        int fixed = (major * 10) + minor;
        int other = (int) Math.round(version * 10.0);
        
        return fixed <= other;
    }
}
