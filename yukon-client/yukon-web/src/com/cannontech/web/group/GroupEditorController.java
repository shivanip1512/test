package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsonOLD.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedDao;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.CopyDeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.predicate.NullPredicate;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.util.JsTreeNode;

public class GroupEditorController extends MultiActionController {

	@Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
	
    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupUiService deviceGroupUiService = null;
    private DeviceGroupProviderDao deviceGroupDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private CopyDeviceGroupService copyDeviceGroupService = null;
    private DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper;
    private RolePropertyDao rolePropertyDao;
    private DeviceGroupComposedDao deviceGroupComposedDao;
    
    private DeviceCollectionFactory deviceCollectionFactory = null;

    private DeviceGroupCollectionHelper deviceGroupCollectionHelper = null;

    private final int maxToShowImmediately = 10;
    private final int maxGetDevicesSize = 1000;
    
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException {

        ModelAndView mav = new ModelAndView("home.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        DeviceGroup group = null;
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        mav.addObject("rootGroup", rootGroup);

        if (!StringUtils.isEmpty(groupName)) {
        	
        	try {
        		group = deviceGroupService.resolveGroupName(groupName);
        	} catch (NotFoundException e) {
        		mav.setViewName("redirect:/spring/group/editor/home");
        		mav.addObject("errorMessage", e.getMessage());
        		return mav;
        	}
            
        } else {
            group = rootGroup;
        }
        mav.addObject("group", group);
        
        // sub groups (child groups)
        List<DeviceGroup> childGroups = deviceGroupDao.getChildGroups(group);
        mav.addObject("subGroups", childGroups);

        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices", false);
        mav.addObject("showDevices", showDevices);
        
        // check the size of the group
        int childDeviceCount = deviceGroupDao.getChildDeviceCount(group);
        mav.addObject("childDeviceCount", childDeviceCount);
        
        // check the size of the group (recursive)
        int deviceCount = deviceGroupDao.getDeviceCount(group);
        mav.addObject("deviceCount", deviceCount);
        
        boolean groupModifiable = group.isModifiable() && group.getParent() != null;
        mav.addObject("groupModifiable", groupModifiable);
        
        boolean isComposedGroup = group.getType().equals(DeviceGroupType.COMPOSED);
        mav.addObject("isComposedGroup", isComposedGroup);
        
        boolean showImmediately = childDeviceCount <= maxToShowImmediately;
        mav.addObject("showImmediately", showImmediately);
        mav.addObject("maxGetDevicesSize", maxGetDevicesSize);
        if (showImmediately || showDevices) {
            addMaxDevicesToMav(mav, group);
        }
                
        // ALL GROUPS HIERARCHY
        DeviceGroupHierarchy everythingHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new NullPredicate<DeviceGroup>());
        
        DeviceGroupHierarchy allGroupsGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(everythingHierarchy, new NonHiddenDeviceGroupPredicate());
        
        // NodeAttributeSettingCallback to highlight node for selected group
        // This one has been given an additional responsibility of recording the node path of the selected node,
        // this path will be used to expand the tree to the selected node and ensure it is visible.
        final DeviceGroup selectedDeviceGroup = group;
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        
        // ALL GROUPS TREE JSON
        HighlightSelectedGroupNodeAttributeSettingCallback callback = new HighlightSelectedGroupNodeAttributeSettingCallback(selectedDeviceGroup);
        JsTreeNode allGroupsRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(allGroupsGroupHierarchy, groupsLabel, callback);
        
        // selected node Ext path
        String extSelectedNodePath = callback.getJsTreeSelectedNodePath();
        mav.addObject("extSelectedNodePath", extSelectedNodePath);
        
        JSONObject allGroupsJsonObj = new JSONObject(allGroupsRoot.toMap());
        String allGroupsDataJson = allGroupsJsonObj.toString();
        mav.addObject("allGroupsDataJson", allGroupsDataJson);
        
        // MOVE GROUPS TREE JSON
        Predicate<DeviceGroup> canMoveUnderPredicate = deviceGroupDao.getGroupCanMovePredicate(selectedDeviceGroup);
        DeviceGroupHierarchy moveGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(allGroupsGroupHierarchy, canMoveUnderPredicate);
        JsTreeNode moveGroupRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(moveGroupHierarchy, groupsLabel, null);
        
        JSONObject moveGroupJsonObj = new JSONObject(moveGroupRoot.toMap());
        mav.addObject("moveGroupDataJson", moveGroupJsonObj.toString()); 
        
        // COPY GROUPS TREE JSON
        Predicate<DeviceGroup> canCopyIntoPredicate = new Predicate<DeviceGroup>() {
            public boolean evaluate(DeviceGroup receivingGroup) {
                return !receivingGroup.isEqualToOrDescendantOf(selectedDeviceGroup) && receivingGroup.isModifiable();
            }
        };
        DeviceGroupHierarchy copyGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(allGroupsGroupHierarchy, canCopyIntoPredicate);
        JsTreeNode copyExtRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(copyGroupHierarchy, groupsLabel, null);
        
        JSONObject copyGroupJson = new JSONObject(copyExtRoot.toMap());
        mav.addObject("copyGroupDataJson", copyGroupJson.toString()); 
        
        // DEVICE COLLECTION
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(selectedDeviceGroup);
        mav.addObject("deviceCollection", deviceCollection);
        
        return mav;
    }
    
    public ModelAndView getDevicesForGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("deviceMembers.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        addMaxDevicesToMav(mav, group);

        mav.addObject("group", group);
        
        return mav;

    }
    
    private void addMaxDevicesToMav(ModelAndView mav, DeviceGroup group) {
        List<DisplayablePao> deviceList = deviceGroupUiService.getChildDevicesByGroup(group, maxGetDevicesSize + 1);
        if (deviceList.size() > maxGetDevicesSize) {
            mav.addObject("limted", true);
            deviceList = deviceList.subList(0, maxGetDevicesSize);
        }
        
        mav.addObject("deviceList", deviceList);
        mav.addObject("maxGetDevicesSize", maxGetDevicesSize);
    }

    public ModelAndView updateGroupName(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_EDIT, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        String newGroupName = ServletRequestUtils.getStringParameter(request, "newGroupName");

        // Make sure a new name was entered and doesn't contain slashes
        newGroupName = newGroupName.trim();
        if (StringUtils.isEmpty(newGroupName) || CtiUtilities.isContainsInvalidDeviceGroupNameCharacters(newGroupName)) {
            mav.addObject("errorMessage",
                          "You must enter a New Group Name.  Group names may not contain slashes.");
            return mav;
        }

        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        group.setName(newGroupName.trim());
        
        if(!group.isEditable()){
            mav.addObject("errorMessage", "Non-editable groups cannot be updated.");
            return mav;
        }
        
        try {
            deviceGroupEditorDao.updateGroup(group);
        } catch (DuplicateException e){
            mav.addObject("errorMessage", e.getMessage());
            return mav;
        }

        mav.addObject("groupName", group.getFullName());

        return mav;

    }

    public ModelAndView addChild(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        ModelAndView deviceGroupMav = new ModelAndView("redirect:/spring/group/editor/home");
        ModelAndView composedGroupMav = new ModelAndView("redirect:/spring/group/composedGroup/build");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        DeviceGroupType subGroupType = DeviceGroupType.valueOf(ServletRequestUtils.getStringParameter(request, "subGroupType"));
        
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        if (group.isModifiable()) {

            String childGroupName = ServletRequestUtils.getStringParameter(request, "childGroupName");

            // Make sure a new name was entered and doesn't contain slashes
            childGroupName = childGroupName.trim();
            if (StringUtils.isEmpty(childGroupName) || CtiUtilities.isContainsInvalidDeviceGroupNameCharacters(childGroupName)) {
                
                deviceGroupMav.addObject("groupName", groupName);
                deviceGroupMav.addObject("errorMessage", "You must enter a Sub Group Name.  Group names may not contain slashes.");
                return deviceGroupMav;
            }

            try {
                
                StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(group);
                
                // PLAIN STATIC GROUP
                if (subGroupType.equals(DeviceGroupType.STATIC)) {
                    
                    StoredDeviceGroup newGroup = deviceGroupEditorDao.addGroup(storedGroup,
                                                  DeviceGroupType.STATIC,
                                                  childGroupName);
                    
                    
                    
                    deviceGroupMav.addObject("groupName", newGroup.getFullName());
                    return deviceGroupMav;
                    
                // COMPOSED GROUP
                } else if (subGroupType.equals(DeviceGroupType.COMPOSED)) {
                    
                    StoredDeviceGroup newGroup = deviceGroupEditorDao.addGroup(storedGroup,
                                                                               DeviceGroupType.COMPOSED,
                                                                               childGroupName);
                    
                    DeviceGroupComposed deviceGroupComposed = new DeviceGroupComposed(newGroup.getId());
                    deviceGroupComposedDao.saveOrUpdate(deviceGroupComposed);
                    
                    composedGroupMav.addObject("groupName", newGroup.getFullName());
                    return composedGroupMav;
                
                } else {
                    
                    deviceGroupMav.addObject("groupName", groupName);
                    deviceGroupMav.addObject("errorMessage", "Invalid sub group type: " + subGroupType.name());
                    return deviceGroupMav;
                }
                
            } catch (DuplicateException e) {
                deviceGroupMav.addObject("groupName", groupName);
                deviceGroupMav.addObject("errorMessage", e.getMessage());
                return deviceGroupMav;
            } catch (NotFoundException e) {
                deviceGroupMav.addObject("groupName", groupName);
                deviceGroupMav.addObject("errorMessage", e.getMessage());
                return deviceGroupMav;
            }

        } else {
            deviceGroupMav.addObject("groupName", groupName);
            deviceGroupMav.addObject("errorMessage", "Cannot add sub group to " + group.getFullName());
            return deviceGroupMav;
        }
    }

    public ModelAndView showAddDevicesByCollection(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("addDevices.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        final DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        mav.addObject("group", group);
        
        mav.addObject("groupName", groupName);
        
        // Ext tree JSON
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new NonHiddenDeviceGroupPredicate());
        
        // NodeAttributeSettingCallback to highlight node fo selected group
        class DisableCurrentGroup implements NodeAttributeSettingCallback<DeviceGroup> {
            public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {
                
                if (group.equals(deviceGroup)) {
                    node.setAttribute("disabled", true);
                }
            }
        }
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        
        JsTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupJsTree(groupHierarchy, groupsLabel, new DisableCurrentGroup());
        JSONObject jsonObj = new JSONObject(root.toMap());
        String dataJson = jsonObj.toString();
        
        mav.addObject("groupDataJson", dataJson);

        return mav;
    }

    public ModelAndView addDevicesByCollection(HttpServletRequest request,
                                                 HttpServletResponse response) throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");
        
        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);
        try {
        	DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        	deviceCollectionDeviceGroupHelper.addCollectionToGroup(groupName, deviceCollection);
        } catch (DeviceCollectionCreationException e) {
            mav.addObject("errorMessage", e.getMessage());
            return mav;
        }

        return mav;
    }

    public ModelAndView removeDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices");
        mav.addObject("showDevices", showDevices);

        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);

        if (group.isModifiable()) {

            Integer deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");

            deviceGroupMemberEditorDao.removeDevicesById(group, Collections.singleton(deviceId));
        } else {
            mav.addObject("errorMessage", "Cannot remove devices from " + group.getFullName());
            return mav;
        }

        return mav;
    }

    public ModelAndView moveGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_EDIT, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        String parentGroupName = ServletRequestUtils.getStringParameter(request, "parentGroupName");

        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        StoredDeviceGroup parentGroup = deviceGroupEditorDao.getStoredGroup(parentGroupName, false);

        // Make sure we can move the group
        try {
            
            if (deviceGroupDao.isGroupCanMoveUnderGroup(group, parentGroup) && group.isEditable()) {
                
                group.setParent(parentGroup);
                deviceGroupEditorDao.updateGroup(group);
    
                mav.addObject("groupName", group.getFullName());
    
            } else {
                mav.addObject("errorMessage", "Cannot move Group: " + group.getFullName());
                mav.addObject("groupName", groupName);
            }
        }
        catch(DuplicateException e) {
            mav.addObject("errorMessage", "Group '" + group.getName() + "' already exists in group '" + parentGroup.getName() +
                          "'. Please rename the group using a unqiue name and try again.");
            mav.addObject("groupName", groupName);
        }

        return mav;
    }
    
    public ModelAndView copyContentsToGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");
        
        // names
        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        String copyContentsToGroupName = ServletRequestUtils.getStringParameter(request, "copyContentsToGroupName");
        
        // get our group and move to group
        DeviceGroup fromGroup = deviceGroupService.resolveGroupName(groupName);
        StoredDeviceGroup toParentGroup = deviceGroupEditorDao.getStoredGroup(copyContentsToGroupName, false);
        
        // ui should prevent invalid groups from being selected, but double check before doing copy
        if (toParentGroup.isModifiable() 
            && !toParentGroup.equals(fromGroup) 
            && !toParentGroup.isDescendantOf(fromGroup)
            ) {
            
            List<DeviceGroup> parentsGroupChildGroups = deviceGroupDao.getChildGroups(toParentGroup);
            List<String> parentsGroupChildGroupNames = new ArrayList<String>();
            for (DeviceGroup d : parentsGroupChildGroups) {
                parentsGroupChildGroupNames.add(d.getName());
            }
            
            List<DeviceGroup> fromGroupChildGroups = deviceGroupDao.getChildGroups(fromGroup);
            for (DeviceGroup d : fromGroupChildGroups) {
                
                if (parentsGroupChildGroupNames.contains(d.getName())) {
                    
                    mav.addObject("errorMessage", "Group '" + d.getName() + "' already exists in group '" +
                                  toParentGroup.getName() + "'. Please rename the group using a unique name and try again.");
                    mav.addObject("groupName", groupName);
                    return mav;
                }
            }
            
            // make copies of contents (devices and child groups and their devices) to parent group
            copyDeviceGroupService.copyGroupAndDevicesToGroup(fromGroup, toParentGroup);
        
            // take you to newly created copied group on return
            mav.addObject("groupName", toParentGroup.getFullName());
            
        }
        else {
            
            mav.addObject("errorMessage", "Cannot copy devices to group: " + toParentGroup.getFullName());
            mav.addObject("groupName", groupName);
        }
        
        return mav;
    }
    
    public ModelAndView removeGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_EDIT, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        String removeGroupName = ServletRequestUtils.getStringParameter(request, "removeGroupName");
        StoredDeviceGroup removeGroup = deviceGroupEditorDao.getStoredGroup(removeGroupName, false);

        // Make sure we can remove the group
        if (removeGroup.isEditable()) {
            deviceGroupEditorDao.removeGroup(removeGroup);
        } else {
            mav.addObject("errorMessage", "Cannot remove Group: " + removeGroup.getFullName());
        }

        return mav;
    }
    
    public ModelAndView removeAllDevicesFromGroup(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("deviceMembers.jsp");
        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        StoredDeviceGroup removeGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
        String membersErrorMessage = "";
        
        try {
            
            YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
            rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
            
            // Make sure we can remove the group
            if (removeGroup.isEditable()) {
                deviceGroupMemberEditorDao.removeAllChildDevices(removeGroup);
            } else {
                membersErrorMessage = "Cannot remove Group: " + removeGroup.getFullName();
            }
        } catch (NotAuthorizedException e) {
            membersErrorMessage = "User not authorized to remove devices.";
            addMaxDevicesToMav(mav, removeGroup);
        }

        mav.addObject("membersErrorMessage", membersErrorMessage);
        mav.addObject("group", removeGroup);

        return mav;

    }
    
    @Required
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }

    @Required
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }
    
    @Required
    public void setCopyDeviceGroupService(
            CopyDeviceGroupService copyDeviceGroupService) {
        this.copyDeviceGroupService = copyDeviceGroupService;
    }
    
    @Required
    public void setDeviceCollectionFactory(DeviceCollectionFactory deviceCollectionFactory) {
        this.deviceCollectionFactory = deviceCollectionFactory;
    }
    
    @Required
    public void setDeviceGroupCollectionHelper(
            DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }
    
    @Required
    public void setDeviceCollectionDeviceGroupHelper(
            DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper) {
        this.deviceCollectionDeviceGroupHelper = deviceCollectionDeviceGroupHelper;
    }
    
    @Autowired
    public void setDeviceGroupUiService(DeviceGroupUiService deviceGroupUiService) {
        this.deviceGroupUiService = deviceGroupUiService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setDeviceGroupComposedDao(DeviceGroupComposedDao deviceGroupComposedDao) {
        this.deviceGroupComposedDao = deviceGroupComposedDao;
    }
}
