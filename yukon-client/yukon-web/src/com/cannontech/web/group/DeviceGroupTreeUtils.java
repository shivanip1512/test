package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupPredicateEnum;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.web.util.JsTreeNode;

public class DeviceGroupTreeUtils {

    public static JsTreeNode makeDeviceGroupExtTree(DeviceGroupHierarchy dgh, String rootName, NodeAttributeSettingCallback<DeviceGroup> nodeCallback) {
    
        DeviceGroupExtTreeBuilder builder = new DeviceGroupExtTreeBuilder();
        
        return builder.doMakeDeviceGroupExtTree(dgh, rootName, nodeCallback, "");
    }
    
    public static void setupNodeAttributes(JsTreeNode node, DeviceGroup deviceGroup, String nodeId, String rootName, String href) {
        
        // set id
        node.setAttribute("id", nodeId);
        
        // set icon class
        for (SystemGroupEnum systemGroup : SystemGroupEnum.values()) {
            if ((deviceGroup.getFullName() + "/").equals(systemGroup.getFullPath())) {
                node.setAttribute("iconCls", systemGroup.toString());
                break;
            }
        }
        
        // set name
        if (rootName != null) {
            node.setAttribute("text", rootName);
        }
        else {
            node.setAttribute("text", deviceGroup.getName());
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
