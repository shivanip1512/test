package com.cannontech.web.group;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.web.util.ExtTreeNode;

public class DeviceGroupTreeUtils {

    public static ExtTreeNode makeDeviceGroupExtTree(DeviceGroupHierarchy dgh, String rootName, NodeAttributeSettingCallback nodeCallback) {
    
        DeviceGroupExtTreeBuilder builder = new DeviceGroupExtTreeBuilder();
        ExtTreeNode root = builder.doMakeDeviceGroupExtTree(dgh, rootName, nodeCallback);
        
        // give the root an id
        root.setAttribute("id", rootName);
        
        return root;
    }
    
    public static void setupNodeAttributes(ExtTreeNode node, DeviceGroup deviceGroup, String nodeId, String rootName) {
        
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
    }
    
    /**
     * To be called on node after tree heirarchy has been constructed
     * @param node
     */
    public static void setLeaf(ExtTreeNode node) {
        
        // leaf? (must be after child groups are added)
        if (node.hasChildren()) {
            node.setAttribute("leaf", false);
        }
        else {
            node.setAttribute("leaf", true);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static void addToNodeInfo(ExtTreeNode node, String key, String data) {
        
        Map<String, String> info = new HashMap<String, String>(); 
        
        if (node.getAttributes().keySet().contains("info")) {
            info = (Map<String, String>)node.getAttributes().get("info");
        }
        
        info.put(key, data);
        node.setAttribute("info", info);
    }
    
}
