package com.cannontech.web.tools.mapping.model;

import java.util.List;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.LazyList;

public class Filter {

    private BuiltInAttribute attribute;
    private List<Group> groups = LazyList.ofInstance(Group.class);
    private String tempDeviceGroupName;

    public BuiltInAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(BuiltInAttribute attribute) {
        this.attribute = attribute;
    }

    public List<Group> getGroups() {
        return groups;
    }
    
    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public String getTempDeviceGroupName() {
        return tempDeviceGroupName;
    }

    public void setTempDeviceGroupName(String tempDeviceGroupName) {
        this.tempDeviceGroupName = tempDeviceGroupName;
    }
}