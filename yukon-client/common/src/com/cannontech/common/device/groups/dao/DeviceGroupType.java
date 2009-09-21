package com.cannontech.common.device.groups.dao;

public enum DeviceGroupType {
    STATIC(DeviceGroupPermission.EDIT_MOD),
    ROLODEX(DeviceGroupPermission.NOEDIT_NOMOD),
    ROUTE(DeviceGroupPermission.NOEDIT_NOMOD),
    DEVICETYPE(DeviceGroupPermission.NOEDIT_NOMOD),
    DEVICETAG(DeviceGroupPermission.NOEDIT_NOMOD),
    DEVICECONFIG(DeviceGroupPermission.NOEDIT_NOMOD),
    METERS_SCANNING_LOAD_PROFILE(DeviceGroupPermission.NOEDIT_NOMOD),
    METERS_SCANNING_VOLTAGE_PROFILE(DeviceGroupPermission.NOEDIT_NOMOD),
    METERS_SCANNING_INTEGRITY(DeviceGroupPermission.NOEDIT_NOMOD),
    METERS_SCANNING_ACCUMULATOR(DeviceGroupPermission.NOEDIT_NOMOD),
    METERS_DISABLED(DeviceGroupPermission.NOEDIT_NOMOD),
    COMPOSED(DeviceGroupPermission.EDIT_NOMOD);
    
    private DeviceGroupPermission deviceGroupPermission;

    DeviceGroupType(DeviceGroupPermission deviceGroupPermission) {
        this.deviceGroupPermission = deviceGroupPermission;
    }
    
    public DeviceGroupPermission getDeviceGroupPermission() {
        return deviceGroupPermission;
    }
}
