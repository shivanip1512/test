package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.web.group.DeviceGroupTreeUtils;
import com.cannontech.web.group.HighlightSelectedGroupNodeAttributeSettingCallback;
import com.cannontech.web.util.JsTreeNode;

@Configurable("deviceGroupHierarchyJsonPrototype")
public class DeviceGroupHierarchyJsonTag extends YukonTagSupport{

    private DeviceGroupService deviceGroupService;
    private DeviceGroupUiService deviceGroupUiService;
    
    private String predicates = "";
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
        
        if(StringUtils.isBlank(rootName)){
        	rootName = getMessageSource().getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        }
        
        JsTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupJsTree(groupHierarchy, rootName, nodeCallback);
        JSONObject jsonObj = new JSONObject(root.toMap());
        
        String extSelectedNodePath = null;
        if (nodeCallback != null) {
        	extSelectedNodePath = nodeCallback.getJsTreeSelectedNodePath();
        	if (!StringUtils.isBlank(selectedNodePathVar)) {
        		this.getJspContext().setAttribute(selectedNodePathVar, extSelectedNodePath);
        	}
        }
        
        if (var == null) {
            jsonObj.write(getJspContext().getOut());
        } else {
            this.getJspContext().setAttribute(var, jsonObj.toString());
        }
    }
    
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    public void setDeviceGroupUiService(DeviceGroupUiService deviceGroupUiService) {
		this.deviceGroupUiService = deviceGroupUiService;
	}
    
    public void setPredicates(String predicates) {
        this.predicates = predicates;
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
