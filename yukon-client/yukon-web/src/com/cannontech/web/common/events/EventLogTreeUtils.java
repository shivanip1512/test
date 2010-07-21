package com.cannontech.web.common.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventCategoryHierarchy;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.model.EventLogPredicateEnum;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.common.util.predicate.Predicate;
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
        EventLogTreeUtils.addToNodeInfo(eventLogtypeNode, "categoryName", fullEventLogTypeName);
        
        // leaf attribute should only be set after possible child groups have been added
        eventLogtypeNode.setAttribute("leaf", true);
        return eventLogtypeNode;
    }
    
    /**
     * To be called on node after tree hierarchy has been constructed
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
    
    public static AggregateAndPredicate<EventLog> getAggregratePredicateFromString(String predicatesStr) {
        
        String[] predicateStrs = StringUtils.split(predicatesStr, ",");
        List<Predicate<EventLog>> predicates = new ArrayList<Predicate<EventLog>>();
        for (String predicateStr : predicateStrs) {
            Predicate<EventLog> predicate = EventLogPredicateEnum.valueOf(predicateStr.trim()).getPredicate();
            predicates.add(predicate);
        }
        AggregateAndPredicate<EventLog> aggregatePredicate = new AggregateAndPredicate<EventLog>(predicates);
        
        return aggregatePredicate;
    }
    
    
    
}
