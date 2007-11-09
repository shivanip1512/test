package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
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
        Set<? extends DeviceGroup> currentGroupsSet = deviceGroupDao.getGroups(meter);
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
            if (group.isModifiable()) {
                groupList.add(group);
            }
        }

        mav.addObject("currentGroups", currentGroups);
        mav.addObject("addableGroups", groupList);
        mav.addObject("meter", meter);

        return mav;
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
     * This method adds the requested device to a selected device group.  
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

        String groupName = WidgetParameterHelper.getRequiredStringParameter(request, "groupName");
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        deviceGroupMemberEditorDao.addDevices((StoredDeviceGroup) group,
                                              Collections.singletonList(meter));

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