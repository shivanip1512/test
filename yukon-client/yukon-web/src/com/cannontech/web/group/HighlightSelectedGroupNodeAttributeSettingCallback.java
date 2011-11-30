package com.cannontech.web.group;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.web.util.JsTreeNode;

public class HighlightSelectedGroupNodeAttributeSettingCallback implements NodeAttributeSettingCallback<DeviceGroup> {

	private DeviceGroup selectedDeviceGroup;
	private String extSelectedNodePath;
	
	public HighlightSelectedGroupNodeAttributeSettingCallback(DeviceGroup selectedDeviceGroup) {
		
		this.selectedDeviceGroup = selectedDeviceGroup;
	}
	
	@Override
	public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {
        if (this.selectedDeviceGroup != null && this.selectedDeviceGroup.getFullName().equals(deviceGroup.getFullName())) {
            
            //DEPRECATED - cls no longer needed w/ jsTree
            node.setAttribute("cls", "highlightNode");
            
            extSelectedNodePath = node.getNodePath();
        }
    }
	
	public String getExtSelectedNodePath() {
		return extSelectedNodePath;
	}
}
