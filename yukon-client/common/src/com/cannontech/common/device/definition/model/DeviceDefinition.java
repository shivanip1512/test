package com.cannontech.common.device.definition.model;

import com.cannontech.common.device.DeviceType;

/**
 * Interface which represents the default definition for a device
 */
public interface DeviceDefinition extends Comparable<DeviceDefinition> {

    public abstract String getDisplayName();

    public abstract DeviceType getType();

    public abstract boolean isChangeable();

    public abstract String getDisplayGroup();

    public abstract String getJavaConstant();

    public abstract String getChangeGroup();

}