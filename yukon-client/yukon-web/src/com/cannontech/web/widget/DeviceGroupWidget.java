package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.AnyDeviceGroupPredicate;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Widget used to display basic device information
 */
public class DeviceGroupWidget extends WidgetControllerBase {

    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupUiService deviceGroupUiService;
    @Autowired private DeviceGroupProviderDao deviceGroupDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private MeterDao meterDao;
    @Autowired private RolePropertyDao rolePropertyDao;
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
        final List<DeviceGroup> currentGroups = getCurrentGroups(meter);

        DeviceGroupHierarchy groupHierarchy =
            deviceGroupUiService.getDeviceGroupHierarchy(deviceGroupService.getRootGroup(),
                                                         new AnyDeviceGroupPredicate());
        
        final Map<DeviceGroup,DeviceGroup> groupsToExpand = getGroupsToExpand(currentGroups);
        DeviceGroupHierarchy hierarchyCurrentGroups = getCurrentGroupsHierarchy(groupHierarchy, groupsToExpand);
  
        class ExpandAndSelectCurrentGroups implements NodeAttributeSettingCallback<DeviceGroup> {

            public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {
                
                // expands group
                if (groupsToExpand.containsKey(deviceGroup) && deviceGroup.isModifiable()) {
                    node.setAttribute("expand", true);
                }
                // selects current group
                if (currentGroups.contains(deviceGroup)) {
                    node.setAttribute("select", true);
                }
                node.setAttribute("unselectable", true);
            }
        }

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String currentGroupsDataJson =
            getGroupDataJson(userContext,
                             hierarchyCurrentGroups,
                             new ExpandAndSelectCurrentGroups());
        
        mav.addObject("currentGroupsDataJson", currentGroupsDataJson);
        mav.addObject("meter", meter);
        mav.addObject("deviceId", deviceId);

        return mav;
    }
    
    /**
     * This method renders edit device groups pop-up.
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping
    public ModelAndView edit(HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("deviceGroupWidget/editGroupTree.jsp");

        // Grabs the deviceId from the request
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request,
                                                                     "deviceId");
        Meter meter = meterDao.getForId(deviceId);

        // Gets all the current groups of the device
        final List<DeviceGroup> currentGroups = getCurrentGroups(meter);
        final Map<DeviceGroup,DeviceGroup> groupsToExpand = getGroupsToExpand(currentGroups);
        
        DeviceGroupHierarchy hierarchyAllGroups =
            deviceGroupUiService.getDeviceGroupHierarchy(deviceGroupService.getRootGroup(),
                                                         new ModifiableDeviceGroupPredicate());
        
        class ExpandAndSelectCurrentGroups implements NodeAttributeSettingCallback<DeviceGroup> {
            
            public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {

                String groupId = String.valueOf(((StoredDeviceGroup) deviceGroup).getId());
                JsTreeNode.addToNodeInfo(node, "groupId", groupId);
                
                // selects current group
                if (currentGroups.contains(deviceGroup)) {
                    node.setAttribute("select", true);
                }
                // expands group
                if (groupsToExpand.containsKey(deviceGroup)) {
                    node.setAttribute("expand", true);
                }
            }
        }

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String allGroupsDataJson =
            getGroupDataJson(userContext,
                             hierarchyAllGroups,
                             new ExpandAndSelectCurrentGroups());
        
        mav.addObject("allGroupsDataJson", allGroupsDataJson);
        mav.addObject("meter", meter);
        mav.addObject("deviceId", deviceId);
                
        return mav;
    }
       
    private List<DeviceGroup> getCurrentGroups(Meter meter){
        Set<? extends DeviceGroup> currentGroupsSet = deviceGroupDao.getGroupMembership(meter);
        final List<DeviceGroup> currentGroups = new ArrayList<DeviceGroup>(currentGroupsSet);

        Collections.sort(currentGroups); 
        return currentGroups;
        
    }
    private String getGroupDataJson(YukonUserContext userContext,
                                    DeviceGroupHierarchy groupHierarchy,
                                    NodeAttributeSettingCallback<DeviceGroup> callback) {
        String groupsLabel =
                messageSourceResolver.getMessageSourceAccessor(userContext)
                    .getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        JsTreeNode root =
            DeviceGroupTreeUtils.makeDeviceGroupJsTree(groupHierarchy,
                                                       groupsLabel,
                                                       callback,
                                                       userContext);
        JSONObject jsonObj = new JSONObject(root.toMap());
        String dataJson = jsonObj.toString();
        return dataJson;
    }
    
    /**
     * This method finds all the groups to expand and their parent groups.
     * 
     * @param currentGroups
     * @return
     */
    private Map<DeviceGroup,DeviceGroup> getGroupsToExpand(List<DeviceGroup> currentGroups){
        Map<DeviceGroup,DeviceGroup> groupsToExpand = Maps.newHashMap();
        DeviceGroup parentGroup = null;
        for(DeviceGroup currentGroup:currentGroups){
            parentGroup = currentGroup.getParent();
            while(!groupsToExpand.containsKey(parentGroup)){
                groupsToExpand.put(parentGroup, parentGroup);
                if(parentGroup != null){
                    parentGroup = parentGroup.getParent();
                }
            }
            groupsToExpand.put(currentGroup, currentGroup);
        }
        return groupsToExpand; 
    }
   
    /**
     * This method return hierarchy of the groups that should be expanded.
     * 
     * @param hierarchy
     * @param expandedGroups
     * @return
     */
    private DeviceGroupHierarchy getCurrentGroupsHierarchy(DeviceGroupHierarchy hierarchy,
                                                           Map<DeviceGroup, DeviceGroup> expandedGroups) {
        // recurse through children
        List<DeviceGroupHierarchy> childGroupList =
            Lists.newArrayListWithExpectedSize(hierarchy.getChildGroupList().size());
        DeviceGroupHierarchy tempResult = new DeviceGroupHierarchy();
        tempResult.setGroup(hierarchy.getGroup());
        tempResult.setChildGroupList(childGroupList);

        for (DeviceGroupHierarchy childHierarchy : hierarchy.getChildGroupList()) {
            if (expandedGroups.containsKey(childHierarchy.getGroup())) {
                DeviceGroupHierarchy filteredChildHierarchy =
                    getCurrentGroupsHierarchy(childHierarchy, expandedGroups);
                childGroupList.add(filteredChildHierarchy);
            }
        }
        return tempResult;
    }
    
    /**
     * This method updates selected device groups.  
     * After doing so it renders the default device group widget action
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        // Gets the parameters from the request
        int deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        Meter meter = meterDao.getForId(deviceId);
        
        // Gets all the current groups of the device
        Set<? extends DeviceGroup> currentGroupsSet = deviceGroupDao.getGroupMembership(meter);
        
        String groupIdsStr = WidgetParameterHelper.getRequiredStringParameter(request, "groupIds");
        
        String groupIds[] = groupIdsStr.split(",");
        
        Set<String> selectedGroupIds = Sets.newHashSet(groupIds);

        Set<String> currentGroupIds = Sets.newHashSet();
        for (DeviceGroup group : currentGroupsSet) {
            if(group.isModifiable()){
                String groupId = String.valueOf(((StoredDeviceGroup)group).getId());
                currentGroupIds.add(groupId);
            }
         }
                
        Set<String> commonGroupIds = Sets.intersection(currentGroupIds, selectedGroupIds);
       
        //groups to remove
        HashSet<String> idsToRemove = Sets.newHashSet(currentGroupIds);
        idsToRemove.removeAll(commonGroupIds);

        //groups to add
        HashSet<String> idsToAdd = Sets.newHashSet(selectedGroupIds);
        idsToAdd.removeAll(commonGroupIds);
        
        
        for(String groupId : idsToAdd) {
            StoredDeviceGroup storedDeviceGroup = deviceGroupEditorDao.getGroupById(Integer.parseInt(groupId));
            deviceGroupMemberEditorDao.addDevices(storedDeviceGroup, meter);
        }
        
        List<Meter> devices = Collections.singletonList(meter);
        for(String groupId : idsToRemove) {
            StoredDeviceGroup storedDeviceGroup = deviceGroupEditorDao.getGroupById(Integer.parseInt(groupId));
            deviceGroupMemberEditorDao.removeDevices(storedDeviceGroup, devices);
        }
        String successMsg =
                messageSourceResolver.getMessageSourceAccessor(userContext)
                    .getMessage("yukon.web.widgets.deviceGroupWidget.saveSuccessful");
        
        request.setAttribute("deviceId", deviceId);
        if (!idsToRemove.isEmpty() || !idsToAdd.isEmpty()) {
            request.setAttribute("successMsg", successMsg);
        }
        
        return render(request, response);
    }
}