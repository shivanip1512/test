package com.cannontech.web.group;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.web.util.ExtTreeNode;

public interface NodeAttributeSettingCallback {

    public void setAdditionalAttributes(ExtTreeNode node, DeviceGroup deviceGroup);
}
