package com.cannontech.web.bulk;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.ModifiableDeviceGroupPredicate;
import com.cannontech.web.bulk.model.DeviceCollectionFactory;
import com.cannontech.web.group.DeviceCollectionDeviceGroupHelper;
import com.cannontech.web.group.DeviceGroupTreeUtils;
import com.cannontech.web.util.ExtTreeNode;


public class AddRemoveCollectionToGroupController extends MultiActionController {
    
    private DeviceGroupService deviceGroupService = null;
    private DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper = null;
    private DeviceCollectionFactory deviceCollectionFactory = null;
    
    public ModelAndView selectGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, Exception {
        
        ModelAndView mav = new ModelAndView("group/selectGroup.jsp");
        
        // pass through device collection
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        String addRemove = ServletRequestUtils.getRequiredStringParameter(request, "addRemove");
        mav.addObject("addRemove", addRemove);
        
        // make a device group hierarchy starting at root, only modifiable groups
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, new ModifiableDeviceGroupPredicate());
        
        ExtTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupExtTree(groupHierarchy, "Groups", null);
        
        JSONObject jsonObj = new JSONObject(root.toMap());
        String dataJson = jsonObj.toString();
        mav.addObject("dataJson", dataJson);
        
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
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Autowired
    public void setDeviceCollectionDeviceGroupHelper(
            DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper) {
        this.deviceCollectionDeviceGroupHelper = deviceCollectionDeviceGroupHelper;
    }
    
    @Autowired
    public void setDeviceCollectionFactory(
            DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }
}
