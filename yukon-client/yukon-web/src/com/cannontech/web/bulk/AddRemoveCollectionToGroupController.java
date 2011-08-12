package com.cannontech.web.bulk;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.group.DeviceCollectionDeviceGroupHelper;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY)
public class AddRemoveCollectionToGroupController extends MultiActionController {
    
    private DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper = null;
    private DeviceCollectionFactory deviceCollectionFactory = null;
    
    public ModelAndView selectGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
        
        ModelAndView mav = new ModelAndView("group/selectGroup.jsp");
        
        // pass through device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        String addRemove = ServletRequestUtils.getRequiredStringParameter(request, "addRemove");
        mav.addObject("addRemove", addRemove);
        
        return mav;
    }
    
    public ModelAndView addToGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        // get groupNmae and deviceCollection from request
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        
        // add devices to group
        deviceCollectionDeviceGroupHelper.addCollectionToGroup(groupName, deviceCollection);
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");
        mav.addObject("groupName", groupName);
        
        return mav;
    }
    
    public ModelAndView removeFromGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        // get groupNmae and deviceCollection from request
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        
        // remove devices from group
        deviceCollectionDeviceGroupHelper.removeCollectionFromGroup(groupName, deviceCollection);
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");
        mav.addObject("groupName", groupName);
        mav.addObject("t", (new Date()).getTime());
        
        return mav;
    }
    
    @Autowired
    public void setDeviceCollectionDeviceGroupHelper(
            DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper) {
        this.deviceCollectionDeviceGroupHelper = deviceCollectionDeviceGroupHelper;
    }
    
    @Required
    public void setDeviceCollectionFactory(
            DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }
}
