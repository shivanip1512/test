/**
 * 
 */
package com.cannontech.web.group;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.util.ExtTreeBuilderUtil;
import com.cannontech.web.util.ExtTreeNode;

public class DeviceGroupExtTreeBuilder {
    
    private Map<String, Integer> nodeIdHistory = new HashMap<String, Integer>();
    
    public ExtTreeNode doMakeDeviceGroupExtTree(DeviceGroupHierarchy dgh, String rootName, NodeAttributeSettingCallback<DeviceGroup> nodeCallback, String parentNodeId) {
        
        DeviceGroup deviceGroup = dgh.getGroup();
        
        // setup node basics
        ExtTreeNode node = new ExtTreeNode();
        
        // node id
        String nodeId = ExtTreeBuilderUtil.createUniqueNodeId(deviceGroup.getFullName(), nodeIdHistory);
        if (rootName != null) {
        	nodeId = rootName;
        }
        
        // node path
        // this is the path that Ext uses organize the tree, it can be used in Ext js code to perform certain functions
        String nodePath = parentNodeId + "/" + nodeId;
        node.setNodePath(nodePath);
        
        // node attributes
        DeviceGroupTreeUtils.setupNodeAttributes(node, deviceGroup, nodeId, rootName, "javascript:void(0);");
        
        // add group name to the list of items in the node's "info" attribute
        ExtTreeNode.addToNodeInfo(node, "groupName", deviceGroup.getFullName());
        
        // recursively add child groups
        for (DeviceGroupHierarchy d : dgh.getChildGroupList()) {
            node.addChild(doMakeDeviceGroupExtTree(d, null, nodeCallback, nodePath));
        }
        
        // run special callback to set specific attributes if provided
        if (nodeCallback != null) {
            nodeCallback.setAdditionalAttributes(node, deviceGroup);
        }
        
        // leaf attribute should only be set after possible child groups have been added
        ExtTreeNode.setLeaf(node);
        
        return node;
    }
    
}