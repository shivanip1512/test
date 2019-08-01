package com.cannontech.common.device.groups;

public class DeviceGroupInUse {
    String groupName;
    String referenceType;
    String name;
    String owner;

    public DeviceGroupInUse(String groupName, String referenceType, String name, String owner) {
        this.groupName = groupName;
        this.referenceType = referenceType;
        this.name = name;
        this.owner = owner;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }
}