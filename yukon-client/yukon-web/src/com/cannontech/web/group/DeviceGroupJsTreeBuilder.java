/**
 * 
 */
package com.cannontech.web.group;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.util.JsTreeBuilderUtil;
import com.cannontech.web.util.JsTreeNode;

public class DeviceGroupJsTreeBuilder {
    
    private Map<String, Integer> nodeIdHistory = new HashMap<String, Integer>();
    
    public JsTreeNode doMakeDeviceGroupJsTree(DeviceGroupHierarchy dgh, String rootName, Set<? extends NodeAttributeSettingCallback<DeviceGroup>> callbacks, String parentNodeId) {
        
        DeviceGroup deviceGroup = dgh.getGroup();
        
        // setup node basics
        JsTreeNode node = new JsTreeNode();
        
        // node id
        String nodeId = JsTreeBuilderUtil.createUniqueNodeId(deviceGroup.getFullName(), nodeIdHistory);
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
        JsTreeNode.addToNodeInfo(node, "groupName", StringEscapeUtils.escapeXml11(deviceGroup.getFullName()));
        
        // recursively add child groups
        for (DeviceGroupHierarchy d : dgh.getChildGroupList()) {
            node.addChild(doMakeDeviceGroupJsTree(d, null, callbacks, nodePath));
        }
        
        // run special callback to set specific attributes if provided
        if (callbacks != null ) {
            for (NodeAttributeSettingCallback<DeviceGroup> callback : callbacks) {
                callback.setAdditionalAttributes(node, deviceGroup);
            }
        }
        
        // leaf attribute should only be set after possible child groups have been added
        JsTreeNode.setLeaf(node);
        
        return node;
    }
    
}