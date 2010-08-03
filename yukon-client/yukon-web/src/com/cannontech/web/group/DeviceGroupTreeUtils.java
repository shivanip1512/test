package com.cannontech.web.group;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.web.util.ExtTreeNode;

public class DeviceGroupTreeUtils {

    public static ExtTreeNode makeDeviceGroupExtTree(DeviceGroupHierarchy dgh, String rootName, NodeAttributeSettingCallback<DeviceGroup> nodeCallback) {
    
        DeviceGroupExtTreeBuilder builder = new DeviceGroupExtTreeBuilder();
        
        return builder.doMakeDeviceGroupExtTree(dgh, rootName, nodeCallback, "");
    }
    
    public static void setupNodeAttributes(ExtTreeNode node, DeviceGroup deviceGroup, String nodeId, String rootName, String href) {
        
        // set id
        node.setAttribute("id", nodeId);
        
        // set icon class
        for (SystemGroupEnum systemGroup : SystemGroupEnum.values()) {
            if ((deviceGroup.getFullName() + "/").equals(systemGroup.getFullPath())) {
                node.setAttribute("iconCls", systemGroup.toString());
                break;
            }
        }
        
        // set name
        if (rootName != null) {
            node.setAttribute("text", rootName);
        }
        else {
            node.setAttribute("text", deviceGroup.getName());
        }
        
        // set href
        if (!StringUtils.isBlank(href)) {
        	node.setAttribute("href", href);
        }
    }
    
}
