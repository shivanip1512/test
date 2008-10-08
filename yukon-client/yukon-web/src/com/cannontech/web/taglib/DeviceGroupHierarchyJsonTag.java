package com.cannontech.web.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.web.group.DeviceGroupTreeUtils;
import com.cannontech.web.util.ExtTreeNode;

@Configurable("deviceGroupHierarchyJsonPrototype")
public class DeviceGroupHierarchyJsonTag extends YukonTagSupport{

    private DeviceGroupService deviceGroupService;
    
    private String predicates = "";
    private String rootName = "Groups";
    private String var = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        
        AggregateAndPredicate<DeviceGroup> aggregatePredicate = DeviceGroupTreeUtils.getAggregratePredicateFromString(predicates);

        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, aggregatePredicate);
        
        ExtTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupExtTree(groupHierarchy, rootName, null);
        JSONObject jsonObj = new JSONObject(root.toMap());
        
        if (var == null) {
            jsonObj.write(getJspContext().getOut());
        } else {
            this.getJspContext().setAttribute(var, jsonObj.toString());
        }
    }
    
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    public void setPredicates(String predicates) {
        this.predicates = predicates;
    }
    
    public void setRootName(String rootName) {
        if (!StringUtils.isBlank(rootName)) {
            this.rootName = rootName;
        }
    }
    
    public void setVar(String var) {
        if (!StringUtils.isBlank(var)) {
            this.var = var;
        }
    }
}
