package com.cannontech.web.common.events;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventCategoryHierarchy;
import com.cannontech.util.ExtTreeBuilderUtil;
import com.cannontech.web.group.NodeAttributeSettingCallback;
import com.cannontech.web.util.JsTreeNode;

public class EventLogTreeUtils {
    private Map<String, Integer> nodeIdHistory = new HashMap<String, Integer>();
    private NodeAttributeSettingCallback<String> nodeCallback;

    public JsTreeNode makeEventCategoryExtTree(EventCategoryHierarchy elh, NodeAttributeSettingCallback<String> nodeCallback) {
    
        this.nodeCallback = nodeCallback;
        return doMakeEventCategoryExtTree(elh, "");
    }
    
    public JsTreeNode doMakeEventCategoryExtTree(EventCategoryHierarchy elh, String parentNodeId) {
        
        EventCategory eventCategory = elh.getEventCategory();
        List<String> eventLogTypes = elh.getEventLogTypes();
        
        // setup node basics
        JsTreeNode node = new JsTreeNode();
        
        // node id
        String nodeId = ExtTreeBuilderUtil.createUniqueNodeId(eventCategory.getName(), nodeIdHistory);
        
        // node path
        // this is the path that Ext uses organize the tree, it can be used in Ext js code to perform certain functions
        String nodePath = parentNodeId + "/" + nodeId;
        node.setNodePath(nodePath);
        
        // node attributes
        node.setAttribute("id", nodeId);
        node.setAttribute("text", eventCategory.getName());
        node.setAttribute("leaf", false);
        
        // Build up event log type node
        for (String eventLogType : eventLogTypes) {
            JsTreeNode child = setupEventLogTypeNode(eventCategory, nodePath, eventLogType);
            node.addChild(child);
        }
        
        // recursively add child groups
        for (EventCategoryHierarchy e : elh.getChildEventCategoryHierarchyList()) {
            JsTreeNode child = doMakeEventCategoryExtTree(e, nodePath);
            node.addChild(child);
        }
        
        return node;
    }

    
    /**
     * @param eventCategory
     * @param parentNodeId
     * @param rootName
     * @param eventLogType
     * @return
     */
    private JsTreeNode setupEventLogTypeNode(EventCategory eventCategory,
                                                     String parentNodeId,
                                                     String eventLogType) {
        JsTreeNode node = new JsTreeNode();
        
        // node id
        String fullEventLogTypeName = eventCategory.getFullName() + "." + eventLogType;
        String eventLogNodeId = ExtTreeBuilderUtil.createUniqueNodeId(eventLogType, nodeIdHistory);
        
        // node path - this is the path that Ext uses organize the tree, 
        // it can be used in Ext js code to perform certain functions
        String nodePath = parentNodeId + "/" + eventLogNodeId;
        node.setNodePath(nodePath);
        
        // node attributes
        node.setAttribute("id", eventLogNodeId);
        node.setAttribute("text", eventLogType);
        node.setAttribute("href", "?eventLogType=" + fullEventLogTypeName);
        node.setAttribute("leaf", true);
        
        // run special callback to set specific attributes if provided
        if (nodeCallback != null) {
            nodeCallback.setAdditionalAttributes(node, fullEventLogTypeName);
        }

        return node;
    }
    
}
