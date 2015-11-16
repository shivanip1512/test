package com.cannontech.web.group;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.web.util.JsTreeNode;

public final class DisableNodeCallback implements NodeAttributeSettingCallback<DeviceGroup> {
    
    private final DeviceGroup group;
    
    private DisableNodeCallback(DeviceGroup group) {
        this.group = group;
    }
    
    @Override
    public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {
        
        if (group.equals(deviceGroup)) {
            node.setAttribute("disabled", true);
        }
    }
    
    public static DisableNodeCallback of(DeviceGroup group) {
        return new DisableNodeCallback(group);
    }
    
}