package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupPredicateEnum;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.web.util.JsTreeNode;
import com.google.common.collect.ImmutableSet;

public class DeviceGroupTreeUtils {

    public static JsTreeNode makeDeviceGroupJsTree(DeviceGroupHierarchy dgh, String rootName, NodeAttributeSettingCallback<DeviceGroup> nodeCallback) {

        DeviceGroupJsTreeBuilder builder = new DeviceGroupJsTreeBuilder();

        return builder.doMakeDeviceGroupJsTree(dgh, rootName, ImmutableSet.of(nodeCallback), "");
    }

    public static JsTreeNode makeDeviceGroupJsTree(DeviceGroupHierarchy dgh, String rootName, Set<? extends NodeAttributeSettingCallback<DeviceGroup>> callbacks) {
    
        DeviceGroupJsTreeBuilder builder = new DeviceGroupJsTreeBuilder();
        
        return builder.doMakeDeviceGroupJsTree(dgh, rootName, callbacks, "");
    }
    
    public static void setupNodeAttributes(JsTreeNode node, DeviceGroup deviceGroup, String nodeId, String rootName, String href) {
        
        // set id
        node.setAttribute("id", nodeId);
        
        // set icon class
        if(deviceGroup.getSystemGroupEnum() != null){
            node.setAttribute("iconCls", deviceGroup.getSystemGroupEnum().name());
        }
        
        // set name
        if (rootName != null) {
            node.setAttribute("text", rootName);
        }
        else {
            node.setAttribute("text",deviceGroup.getName());
        }
        
        // set href
        if (!StringUtils.isBlank(href)) {
        	node.setAttribute("href", href);
        }
    }
    
    public static AggregateAndPredicate<DeviceGroup> getAggregratePredicateFromString(String predicatesStr) {
        
        String[] predicateStrs = StringUtils.split(predicatesStr, ",");
        List<Predicate<DeviceGroup>> predicates = new ArrayList<Predicate<DeviceGroup>>();
        for (String predicateStr : predicateStrs) {
            Predicate<DeviceGroup> predicate = DeviceGroupPredicateEnum.valueOf(predicateStr.trim()).getPredicate();
            predicates.add(predicate);
        }
        AggregateAndPredicate<DeviceGroup> aggregatePredicate = new AggregateAndPredicate<DeviceGroup>(predicates);
        
        return aggregatePredicate;
    }

}
