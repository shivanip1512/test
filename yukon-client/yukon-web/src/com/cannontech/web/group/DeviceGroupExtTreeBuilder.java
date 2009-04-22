/**
 * 
 */
package com.cannontech.web.group;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.web.util.ExtTreeNode;

public class DeviceGroupExtTreeBuilder {
    
    private Map<String, Integer> nodeIdHistory = new HashMap<String, Integer>();
    
    public ExtTreeNode doMakeDeviceGroupExtTree(DeviceGroupHierarchy dgh, String rootName, NodeAttributeSettingCallback nodeCallback, String parentNodeId) {
        
        DeviceGroup deviceGroup = dgh.getGroup();
        
        // setup node basics
        ExtTreeNode node = new ExtTreeNode();
        
        // node id
        String nodeId = createUniqueNodeId(deviceGroup.getFullName());
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
        DeviceGroupTreeUtils.addToNodeInfo(node, "groupName", deviceGroup.getFullName());
        
        // recursively add child groups
        for (DeviceGroupHierarchy d : dgh.getChildGroupList()) {
            node.addChild(doMakeDeviceGroupExtTree(d, null, nodeCallback, nodePath));
        }
        
        // run special callback to set specific attributes if provided
        if (nodeCallback != null) {
            nodeCallback.setAdditionalAttributes(node, deviceGroup);
        }
        
        // leaf attribute should only be set after possible child groups have been added
        DeviceGroupTreeUtils.setLeaf(node);
        
        return node;
    }
    
    private String createUniqueNodeId(String groupName) {
        
        String simpleNodeIdChoice = groupName.replaceAll("[^a-zA-Z0-9]","");
        
        if (nodeIdHistory.containsKey(simpleNodeIdChoice)) {
            
            // find out the last number
            int lastSuffix = nodeIdHistory.get(simpleNodeIdChoice);
            int newSuffix = lastSuffix + 1;
            nodeIdHistory.put(simpleNodeIdChoice, newSuffix);
            return simpleNodeIdChoice + "_" + newSuffix;
            
        } else {
            
            nodeIdHistory.put(simpleNodeIdChoice, 1);
            return simpleNodeIdChoice;
        }
    }
}