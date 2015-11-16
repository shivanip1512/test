package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.web.group.DeviceGroupTreeUtils;
import com.cannontech.web.group.HighlightSelectedGroupNodeAttributeSettingCallback;
import com.cannontech.web.group.NodeAttributeSettingCallback;
import com.cannontech.web.util.JsTreeNode;

@Configurable(value="deviceGroupHierarchyJsonPrototype", autowire=Autowire.BY_NAME)
public class DeviceGroupHierarchyJsonTag extends YukonTagSupport {

    private @Autowired DeviceGroupService deviceGroupService;
    private @Autowired DeviceGroupUiService deviceGroupUiService;
    
    private String predicates = "";
    private Set<NodeAttributeSettingCallback<DeviceGroup>> callbacks = new HashSet<>();
    private String rootName = "";
    private String selectGroupName = null;
    private String selectedNodePathVar = null;
    private String var = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        AggregateAndPredicate<DeviceGroup> aggregatePredicate = DeviceGroupTreeUtils.getAggregratePredicateFromString(predicates);
        
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, aggregatePredicate);
        
        HighlightSelectedGroupNodeAttributeSettingCallback nodeCallback = null;
        if (!StringUtils.isBlank(selectGroupName)) {
            DeviceGroup selectedDeviceGroup = deviceGroupService.resolveGroupName(selectGroupName);
            nodeCallback = new HighlightSelectedGroupNodeAttributeSettingCallback(selectedDeviceGroup);
        }
        
        if (StringUtils.isBlank(rootName)) {
            try {
                rootName = getMessageSource().getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
            } catch(Exception e) {
                rootName = "Groups";
            }
        }
        
        Set<NodeAttributeSettingCallback<DeviceGroup>> allCallbacks = new HashSet<>();
        if (nodeCallback != null) {
            allCallbacks.add(nodeCallback);
        }
        if (callbacks != null) {
            allCallbacks.addAll(callbacks);
        }

        JsTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupJsTree(groupHierarchy, rootName, allCallbacks);
        
        String extSelectedNodePath = null;
        if (nodeCallback != null) {
            extSelectedNodePath = nodeCallback.getJsTreeSelectedNodePath();
            if (!StringUtils.isBlank(selectedNodePathVar)) {
                getJspContext().setAttribute(selectedNodePathVar, extSelectedNodePath);
            }
        }
        
        if (var == null) {
            getJspContext().getOut().write(JsonUtils.toJson(root.toMap()));
        } else {
            getJspContext().setAttribute(var, JsonUtils.toJson(root.toMap()));
        }
    }
    
    public void setPredicates(String predicates) {
        this.predicates = predicates;
    }
    
    public void setCallbacks(Set<NodeAttributeSettingCallback<DeviceGroup>> callbacks) {
        this.callbacks = callbacks;
    }

    public void setRootName(String rootName) {
        if (!StringUtils.isBlank(rootName)) {
            this.rootName = rootName;
        }
    }
    
    public void setSelectGroupName(String selectGroupName) {
        this.selectGroupName = selectGroupName;
    }
    
    public void setSelectedNodePathVar(String selectedNodePathVar) {
        this.selectedNodePathVar = selectedNodePathVar;
    }
    
    public void setVar(String var) {
        if (!StringUtils.isBlank(var)) {
            this.var = var;
        }
    }
    
}