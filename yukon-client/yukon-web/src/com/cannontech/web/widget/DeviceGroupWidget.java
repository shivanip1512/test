package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.device.groups.service.ModifiableDeviceGroupPredicate;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.group.DeviceGroupTreeUtils;
import com.cannontech.web.group.NodeAttributeSettingCallback;
import com.cannontech.web.util.JsTreeNode;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

/**
 * Widget used to display basic device information
 */
public class DeviceGroupWidget extends WidgetControllerBase {

    private DeviceGroupService deviceGroupService;
    private DeviceGroupUiService deviceGroupUiService;
    private DeviceGroupProviderDao deviceGroupDao;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private MeterDao meterDao;
    private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

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
        final List<DeviceGroup> currentGroups = new ArrayList<DeviceGroup>(currentGroupsSet);

        Collections.sort(currentGroups);

        // Make a list of all the groups the device is not in and that are
        // modifiable

        mav.addObject("currentGroups", currentGroups);
        mav.addObject("meter", meter);
        mav.addObject("deviceId", deviceId);
        
        // Ext tree JSON
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new ModifiableDeviceGroupPredicate());
        
        // NodeAttributeSettingCallback to highlight node fo selected group
        class AddGroupIdInfoAndDisableCurrentGroups implements NodeAttributeSettingCallback<DeviceGroup> {
            public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {
                
                String groupId = ((Integer)((StoredDeviceGroup)deviceGroup).getId()).toString();
                JsTreeNode.addToNodeInfo(node, "groupId", groupId);
                
                if (currentGroups.contains(deviceGroup)) {
                    node.setAttribute("disabled", true);
                }
            }
        }
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        JsTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupJsTree(groupHierarchy, groupsLabel, new AddGroupIdInfoAndDisableCurrentGroups(), userContext);
        
        JSONObject jsonObj = new JSONObject(root.toMap());
        String dataJson = jsonObj.toString();
        mav.addObject("groupDataJson", dataJson);
        
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

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        // Gets the parameters from the request
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        List<Meter> devices = Collections.singletonList(meter);

        int storedDeviceGroupId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                                "groupId");
        StoredDeviceGroup storedDeviceGroup = deviceGroupEditorDao.getGroupById(storedDeviceGroupId);        
        deviceGroupMemberEditorDao.removeDevices(storedDeviceGroup, devices);

        ModelAndView mav = this.render(request, response);
        mav.setViewName("deviceGroupWidget/currentGroups.jsp");
        return mav;
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

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        // Gets the parameters from the request
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);

        String groupIdsStr = WidgetParameterHelper.getRequiredStringParameter(request, "groupIds");
        
        String groupIds[] = groupIdsStr.split(",");
        
        for(String groupId : groupIds) {
            StoredDeviceGroup storedDeviceGroup = deviceGroupEditorDao.getGroupById(Integer.parseInt(groupId));
            deviceGroupMemberEditorDao.addDevices(storedDeviceGroup, meter);
        }

        ModelAndView mav = this.render(request, response);
        mav.setViewName("deviceGroupWidget/currentGroups.jsp");
        return mav;
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

    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }

    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
    @Autowired
    public void setDeviceGroupUiService(DeviceGroupUiService deviceGroupUiService) {
		this.deviceGroupUiService = deviceGroupUiService;
	}
}