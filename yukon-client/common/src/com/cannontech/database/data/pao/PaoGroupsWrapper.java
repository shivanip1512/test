package com.cannontech.database.data.pao;

/**
 * Interface for PaoGroups - the implementing class for this interface should
 * delegate method calls to PaoGroups' static methods
 */
public interface PaoGroupsWrapper {

    public abstract int getDeviceType(String typeString);
    
    public abstract String getPAOTypeString(int type);

}