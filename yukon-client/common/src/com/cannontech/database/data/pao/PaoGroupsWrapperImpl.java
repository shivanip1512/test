package com.cannontech.database.data.pao;

/**
 * Delegator class to allow injection of PaoGroups. Delegates calls to
 * PaoGroups' static methods.
 */
public class PaoGroupsWrapperImpl implements PaoGroupsWrapper {

    public final int getDeviceType(String typeString) {
        int deviceType = PAOGroups.getDeviceType(typeString);
        if (deviceType == PAOGroups.INVALID) {
            throw new IllegalArgumentException("Device type: " + typeString + " is not supported");
        }
        return deviceType;
    }

    public String getPAOTypeString(int type) {
        return PAOGroups.getPAOTypeString(type);
    }

}
