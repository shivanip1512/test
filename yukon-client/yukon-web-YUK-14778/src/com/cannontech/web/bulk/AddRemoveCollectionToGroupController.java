package com.cannontech.web.bulk;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.group.DeviceCollectionDeviceGroupHelper;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY)
@Controller
@RequestMapping("/group/*")
public class AddRemoveCollectionToGroupController {
    
    @Autowired private DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    
    @RequestMapping("selectGroup")
    public String selectGroup(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        // pass through device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        String addRemove = ServletRequestUtils.getRequiredStringParameter(request, "addRemove");
        model.addAttribute("addRemove", addRemove);
        
        return "group/selectGroup.jsp";
    }
    
    @RequestMapping("addToGroup")
    public String addToGroup(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        // get groupNmae and deviceCollection from request
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        
        // add devices to group
        deviceCollectionDeviceGroupHelper.addCollectionToGroup(groupName, deviceCollection);
        
        model.addAttribute("groupName", groupName);
        
        return "redirect:/group/editor/home";
    }
    
    @RequestMapping("removeFromGroup")
    public String removeFromGroup(ModelMap model, HttpServletRequest request) throws ServletRequestBindingException {
        
        // get groupNmae and deviceCollection from request
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        
        // remove devices from group
        deviceCollectionDeviceGroupHelper.removeCollectionFromGroup(groupName, deviceCollection);
        
        model.addAttribute("groupName", groupName);
        model.addAttribute("t", (new Date()).getTime());
        
        return "redirect:/group/editor/home";
    }
    
}