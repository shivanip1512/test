package com.cannontech.common.device.groups.composed.dao;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class ComposedGroup implements Comparable<ComposedGroup> {

    private String groupFullName = "";
    private boolean negate = false;


    public ComposedGroup() {
    }

    public ComposedGroup(DeviceGroup deviceGroup, boolean negate) {
        groupFullName = deviceGroup == null ? "" : deviceGroup.getFullName();
        this.negate = negate;
    }

    public String getGroupFullName() {
        return groupFullName;
    }
    public void setGroupFullName(String groupFullName) {
        this.groupFullName = groupFullName;
    }
    public boolean isNegate() {
        return negate;
    }
    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    @Override
    public int compareTo(ComposedGroup o) {
        return groupFullName.compareTo(o.getGroupFullName());
    }
}