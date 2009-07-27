package com.cannontech.web.group;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.web.util.ExtTreeNode;

public class HighlightSelectedGroupNodeAttributeSettingCallback implements NodeAttributeSettingCallback {

	private DeviceGroup selectedDeviceGroup;
	private String extSelectedNodePath;
	
	public HighlightSelectedGroupNodeAttributeSettingCallback(DeviceGroup selectedDeviceGroup) {
		
		this.selectedDeviceGroup = selectedDeviceGroup;
	}
	
	@Override
	public void setAdditionalAttributes(ExtTreeNode node, DeviceGroup deviceGroup) {
        if (this.selectedDeviceGroup != null && this.selectedDeviceGroup.getFullName().equals(deviceGroup.getFullName())) {
            node.setAttribute("cls", "highlightNode");
            extSelectedNodePath = node.getNodePath();
        }
    }
	
	public String getExtSelectedNodePath() {
		return extSelectedNodePath;
	}
}
