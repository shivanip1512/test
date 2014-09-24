package com.cannontech.web.group;

import java.util.List;

import com.cannontech.common.device.groups.composed.dao.ComposedGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposedCompositionType;
import com.cannontech.common.util.LazyList;

public class ComposedGroupRules {

    private String groupName;
    private DeviceGroupComposedCompositionType compositionType;
    private List<ComposedGroup> groups = LazyList.ofInstance(ComposedGroup.class);

    public final String getGroupName() {
        return groupName;
    }

    public final void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public final DeviceGroupComposedCompositionType getCompositionType() {
        return compositionType;
    }

    public final void setCompositionType(DeviceGroupComposedCompositionType compositionType) {
        this.compositionType = compositionType;
    }

    public final List<ComposedGroup> getGroups() {
        return groups;
    }

    public final void setGroups(List<ComposedGroup> groups) {
        this.groups = groups;
    }
}