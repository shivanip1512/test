package com.cannontech.web.widget;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.ModifiableDeviceGroupPredicate;
import com.cannontech.web.util.ExtTreeNode;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class DeviceGroupWidget extends WidgetControllerBase {

    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupProviderDao deviceGroupDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private MeterDao meterDao;

    /**
     * This method renders the default deviceGroupWidget
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView render(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("deviceGroupWidget/render.jsp");

        // Grabs the deviceId from the request
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        Meter meter = meterDao.getForId(deviceId);

        // Gets all the current groups of the device
        Set<? extends DeviceGroup> currentGroupsSet = deviceGroupDao.getGroupMembership(meter);
        List<DeviceGroup> currentGroups = new ArrayList<DeviceGroup>(currentGroupsSet);

        Collections.sort(currentGroups);

        // Make a list of all the groups the device is not in and that are
        // modifiable

        // Get all groups
        List<? extends DeviceGroup> allGroups = deviceGroupDao.getAllGroups();
        
        // Remove the groups the device is already in
        allGroups.removeAll(currentGroupsSet);

        // Iterate the list and take the modifiable groups
        List<DeviceGroup> groupList = new ArrayList<DeviceGroup>();
        for (DeviceGroup group : allGroups) {
            // parent check enforces no-devices-under-root rule
            if (group.isModifiable() && group.getParent() != null) {
                groupList.add(group);
            }
        }

        mav.addObject("currentGroups", currentGroups);
        mav.addObject("addableGroups", groupList);
        mav.addObject("meter", meter);
        mav.addObject("deviceId", deviceId);
        
        return mav;
    }
    
    
    /**
     * Returns a JSON string representing the group heirarchy.
     * Uses makeGroupTreeNode() to create  tree nodes with attributes expected by Ext-js TreePanel,
     * those nodes are converted maps and then converted to JSON.
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView deviceGroupHierarchyJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        Set<? extends DeviceGroup> currentGroupsSet = deviceGroupDao.getGroupMembership(meter);
        List<DeviceGroup> currentGroups = new ArrayList<DeviceGroup>(currentGroupsSet);
        
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, new ModifiableDeviceGroupPredicate());
        
        // recursively create a tree when this node is the root
        ExtTreeNode root = makeDeviceGroupExtTree(groupHierarchy, currentGroups);
        
        // make a list containing maps which represents each group node
        List<Map<String, Object>> groupList = new ArrayList<Map<String, Object>>();
        for (ExtTreeNode n : root.getChildren()) {
            groupList.add(n.toMap());
        }
        
        // convert list to JSON array
        JSONArray jsonArray = new JSONArray(groupList);
        
        // write JSON to response
        PrintWriter writer = response.getWriter();
        String responseJsonStr = jsonArray.toString();
        writer.write(responseJsonStr);

        return null;
        
    }
    
    public static ExtTreeNode makeDeviceGroupExtTree(DeviceGroupHierarchy dgh, List<DeviceGroup> currentGroups) throws Exception{
        
        DeviceGroup deviceGroup = dgh.getGroup();
        
        ExtTreeNode node = new ExtTreeNode();
        //String nodeId = deviceGroup.getFullName().replaceAll("[^a-zA-Z0-9]","");
        int groupId = ((StoredDeviceGroup)deviceGroup).getId();
        node.setAttribute("id", groupId);
        
        // display name
        node.setAttribute("text", StringEscapeUtils.escapeHtml(deviceGroup.getName()));
        
        // disabled groups we already belong to
        if (currentGroups.contains(deviceGroup)) {
            node.setAttribute("disabled", true);
        }
        
        // recursively add child groups
        for (DeviceGroupHierarchy d : dgh.getChildGroupList()) {
            node.addChild(makeDeviceGroupExtTree(d, currentGroups));
        }
        
        // leaf? (must be after child groups are added)
        if (node.hasChildren()) {
            node.setAttribute("leaf", false);
        }
        else {
            node.setAttribute("leaf", true);
        }
        
        // set the icon class depending on group
        setIconCls(node, deviceGroup);
        
        return node;
    }
    
    /**
     * Check if the device group is one of the system groups. If it is, set the iconCls
     * attribute on the node to the enum value of the system group. The enum name should match
     * a css class name that will set the icon image of the node.
     * @param node
     * @param deviceGroup
     */
    public static void setIconCls(ExtTreeNode node, DeviceGroup deviceGroup) {
        
        for (SystemGroupEnum systemGroup : SystemGroupEnum.values()) {
            if ((deviceGroup.getFullName() + "/").equals(systemGroup.getFullPath())) {
                node.setAttribute("iconCls", systemGroup.toString());
                break;
            }
        }
    }
    
    /**
     * This method removes the requested device from a certain device group.  
     * After doing so it renders the default device group widget action
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView remove(HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        // Gets the parameters from the request
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        List<Meter> devices = Collections.singletonList(meter);

        int storedDeviceGroupId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                                "groupId");
        StoredDeviceGroup storedDeviceGroup = deviceGroupEditorDao.getGroupById(storedDeviceGroupId);        
        deviceGroupMemberEditorDao.removeDevices(storedDeviceGroup, devices);

        return this.render(request, response);
    }
    
    /**
     * This method adds the requested device to a selected device groups.  
     * After doing so it renders the default device group widget action
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // Gets the parameters from the request
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);

        String groupIdsStr = WidgetParameterHelper.getRequiredStringParameter(request, "groupIds");
        
        String groupIds[] = groupIdsStr.split(",");
        
        for(String groupId : groupIds) {
            StoredDeviceGroup storedDeviceGroup = deviceGroupEditorDao.getGroupById(Integer.parseInt(groupId));
            deviceGroupMemberEditorDao.addDevices(storedDeviceGroup, meter);
        }

        return this.render(request, response);
    }
    
    
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    @Required
    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }

    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }

    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

}