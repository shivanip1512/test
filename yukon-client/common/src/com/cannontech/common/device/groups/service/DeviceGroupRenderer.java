package com.cannontech.common.device.groups.service;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.gui.tree.Renderer;

public class DeviceGroupRenderer implements Renderer<DeviceGroup> {
    public String render(DeviceGroup o) {
        return o.getName();
    }
}
