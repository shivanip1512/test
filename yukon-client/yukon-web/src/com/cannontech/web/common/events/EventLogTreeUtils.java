package com.cannontech.web.common.events;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventCategoryHierarchy;
import com.cannontech.web.group.NodeAttributeSettingCallback;
import com.cannontech.web.util.ExtTreeNode;

public class EventLogTreeUtils {

    public static ExtTreeNode makeEventCategoryExtTree(EventCategoryHierarchy elh, String rootName,
                                                         NodeAttributeSettingCallback<EventCategory> nodeCallback) {
    
        EventLogExtTreeBuilder builder = new EventLogExtTreeBuilder();
        
        return builder.doMakeEventCategoryExtTree(elh, rootName, nodeCallback, "");
    }
    
    public static void setupNodeAttributes(ExtTreeNode node, EventCategory eventCategory, List<String> eventLogTypes, String nodeId, String rootName, String href) {
        
        // set id
        node.setAttribute("id", nodeId);
        
        // set name
        if (rootName != null) {
            node.setAttribute("text", rootName);
        } else {
            node.setAttribute("text", eventCategory.getName());
        }
        
        // Build up event log type node
        for (String eventLogType : eventLogTypes) {
            ExtTreeNode eventLogtypeNode = setupEventLogTypeNode(eventCategory,
                                                                 nodeId,
                                                                 rootName,
                                                                 eventLogType);

            node.addChild(eventLogtypeNode);
        }
        
    }

    /**
     * @param eventCategory
     * @param nodeId
     * @param rootName
     * @param eventLogType
     * @return
     */
    private static ExtTreeNode setupEventLogTypeNode(EventCategory eventCategory,
                                                     String nodeId,
                                                     String rootName,
                                                     String eventLogType) {
        ExtTreeNode eventLogtypeNode = new ExtTreeNode();
        
        // node id
        String fullEventLogTypeName = eventCategory.getFullName()+"."+eventLogType;
        String eventLogNodeId = EventLogExtTreeBuilder.createUniqueNodeId(fullEventLogTypeName);
        if (rootName != null) {
            eventLogNodeId = rootName;
        }
        
        // node path - this is the path that Ext uses organize the tree, 
        // it can be used in Ext js code to perform certain functions
        String nodePath = nodeId + "/" + eventLogNodeId;
        eventLogtypeNode.setNodePath(nodePath);
        
        // node attributes
        eventLogtypeNode.setAttribute("text", eventLogType);
        
        // node link
        String eventLogTypeStr = StringUtils.removeStart(fullEventLogTypeName, "Categories.");
        eventLogtypeNode.setAttribute("href", "?eventLogType="+eventLogTypeStr);

        // add group name to the list of items in the node's "info" attribute
        ExtTreeNode.addToNodeInfo(eventLogtypeNode, "categoryName", fullEventLogTypeName);
        
        // leaf attribute should only be set after possible child groups have been added
        eventLogtypeNode.setAttribute("leaf", true);
        return eventLogtypeNode;
    }
    
}
