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
    
    public ExtTreeNode doMakeDeviceGroupExtTree(DeviceGroupHierarchy dgh, String rootName, NodeAttributeSettingCallback nodeCallback) {
        
        DeviceGroup deviceGroup = dgh.getGroup();
        
        // setup node basics
        ExtTreeNode node = new ExtTreeNode();
        String nodeId = createUniqueNodeId(deviceGroup.getFullName());
        
        DeviceGroupTreeUtils.setupNodeAttributes(node, deviceGroup, nodeId, rootName);
        
        // no href
        node.setAttribute("href", "javascript:void(0);");
        
        // run speical callback to set specific attributes if provided
        if (nodeCallback != null) {
            nodeCallback.setAdditionalAttributes(node, deviceGroup);
        }
        
        // always add group name to node info attribute
        DeviceGroupTreeUtils.addToNodeInfo(node, "groupName", deviceGroup.getFullName());
        
        // recursively add child groups
        for (DeviceGroupHierarchy d : dgh.getChildGroupList()) {
            node.addChild(doMakeDeviceGroupExtTree(d, null, nodeCallback));
        }
        
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