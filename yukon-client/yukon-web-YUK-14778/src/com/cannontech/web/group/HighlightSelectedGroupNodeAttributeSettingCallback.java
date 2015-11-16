package com.cannontech.web.group;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.web.util.JsTreeNode;

public class HighlightSelectedGroupNodeAttributeSettingCallback implements NodeAttributeSettingCallback<DeviceGroup> {

	private DeviceGroup selectedDeviceGroup;
	private String jsTreeSelectedNodePath;
	
	public HighlightSelectedGroupNodeAttributeSettingCallback(DeviceGroup selectedDeviceGroup) {
		
		this.selectedDeviceGroup = selectedDeviceGroup;
	}
	
	@Override
	public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {
        if (this.selectedDeviceGroup != null && this.selectedDeviceGroup.getFullName().equals(deviceGroup.getFullName())) {
            jsTreeSelectedNodePath = node.getNodePath();
        }
    }
	
	public String getJsTreeSelectedNodePath() {
		return jsTreeSelectedNodePath;
	}
}
