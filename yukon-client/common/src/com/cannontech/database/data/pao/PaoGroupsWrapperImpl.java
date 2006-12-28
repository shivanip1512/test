package com.cannontech.database.data.pao;

/**
 * Delegator class to allow injection of PaoGroups. Delegates calls to
 * PaoGroups' static methods.
 */
public class PaoGroupsWrapperImpl implements PaoGroupsWrapper {

    public final int getDeviceType(String typeString) {
        return PAOGroups.getDeviceType(typeString);
    }

}
