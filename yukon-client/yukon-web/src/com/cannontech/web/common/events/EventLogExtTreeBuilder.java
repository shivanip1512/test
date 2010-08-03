package com.cannontech.web.common.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventCategoryHierarchy;
import com.cannontech.util.ExtTreeBuilderUtil;
import com.cannontech.web.group.NodeAttributeSettingCallback;
import com.cannontech.web.util.ExtTreeNode;

public class EventLogExtTreeBuilder {
    
    private static Map<String, Integer> nodeIdHistory = new HashMap<String, Integer>();
    
    public ExtTreeNode doMakeEventCategoryExtTree(EventCategoryHierarchy elh, String rootName, NodeAttributeSettingCallback<EventCategory> nodeCallback, String parentNodeId) {
        
        EventCategory eventCategory = elh.getEventCategory();
        List<String> eventLogTypes = elh.getEventLogTypes();
        
        // setup node basics
        ExtTreeNode node = new ExtTreeNode();
        
        // node id
        String fullEventTypeName = eventCategory.getFullName();
        String nodeId = ExtTreeBuilderUtil.createUniqueNodeId(fullEventTypeName, nodeIdHistory);
        if (rootName != null) {
        	nodeId = rootName;
        }
        
        // node path
        // this is the path that Ext uses organize the tree, it can be used in Ext js code to perform certain functions
        String nodePath = parentNodeId + "/" + nodeId;
        node.setNodePath(nodePath);
        
        // node attributes
        EventLogTreeUtils.setupNodeAttributes(node, eventCategory, eventLogTypes, nodeId, rootName, "javascript:void(0);");
        
        // add group name to the list of items in the node's "info" attribute
        ExtTreeNode.addToNodeInfo(node, "categoryName", fullEventTypeName);
        
        // recursively add child groups
        for (EventCategoryHierarchy e : elh.getChildEventCategoryHierarchyList()) {
            node.addChild(doMakeEventCategoryExtTree(e, null, nodeCallback, nodePath));
        }
        
        // run special callback to set specific attributes if provided
        if (nodeCallback != null) {
            nodeCallback.setAdditionalAttributes(node, eventCategory);
        }
        
        // leaf attribute should only be set after possible child groups have been added
        ExtTreeNode.setLeaf(node);
        
        return node;
    }

    
    public static String createUniqueNodeId(String nodeName) {
        return ExtTreeBuilderUtil.createUniqueNodeId(nodeName, nodeIdHistory);
    }
    
}