package com.cannontech.common.device.groups.service;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.gui.tree.Renderer;

public class DeviceGroupRenderer extends Renderer<DeviceGroup> {
    public DeviceGroupRenderer() {
        super(DeviceGroup.class);
    }
    public String doRender(DeviceGroup o) {
        return o.getName();
    }
}
