package com.cannontech.web.common.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventCategoryHierarchy;
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
        String nodeId = createUniqueNodeId(fullEventTypeName);
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
        EventLogTreeUtils.addToNodeInfo(node, "categoryName", fullEventTypeName);
        
        // recursively add child groups
        for (EventCategoryHierarchy e : elh.getChildEventCategoryHierarchyList()) {
            node.addChild(doMakeEventCategoryExtTree(e, null, nodeCallback, nodePath));
        }
        
        // run special callback to set specific attributes if provided
        if (nodeCallback != null) {
            nodeCallback.setAdditionalAttributes(node, eventCategory);
        }
        
        // leaf attribute should only be set after possible child groups have been added
        EventLogTreeUtils.setLeaf(node);
        
        return node;
    }
    
    public static String createUniqueNodeId(String groupName) {
        
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