package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.CopyDeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.ModifiableDeviceGroupPredicate;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.roles.operator.DeviceActionsRole;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.bulk.model.DeviceCollectionFactory;
import com.cannontech.web.util.ExtTreeNode;

public class GroupEditorController extends MultiActionController {

    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupProviderDao deviceGroupDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private CopyDeviceGroupService copyDeviceGroupService = null;
    private DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper;
    private AuthDao authDao;
    
    private DeviceCollectionFactory deviceCollectionFactory = null;

    private MeterDao meterDao = null;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper = null;
    
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
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
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
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException {

        ModelAndView mav = new ModelAndView("home.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        DeviceGroup group = null;
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        mav.addObject("rootGroup", rootGroup);

        if (!StringUtils.isEmpty(groupName)) {
            group = deviceGroupService.resolveGroupName(groupName);
        } else {
            group = rootGroup;
        }
        mav.addObject("group", group);
        
        // all groups
        List<DeviceGroup> groups = deviceGroupDao.getAllGroups();

        // move to groups
        // Create a list of groups the current group could move to excluding the
        // current group itself, any descendant groups of the current group and
        // any groups that are not modifiable
        final List<DeviceGroup> moveGroups = getMoveGroups(groups, group);
        mav.addObject("moveGroups", moveGroups);
        
        // copy to groups
        List<DeviceGroup> copyToGroups = getCopyToGroups(groups, group);
        mav.addObject("copyToGroups", copyToGroups);

        // sub groups (child groups)
        MapQueue<DeviceGroup, DeviceGroup> childList = getChildList(groups);
        List<DeviceGroup> childGroups = childList.get(group);
        mav.addObject("subGroups", childGroups);

        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices", false);
        mav.addObject("showDevices", showDevices);
        
        List<? extends YukonDevice> deviceList = null;
        // check the size of the group
        final int maxToShowImmediately = 10;
        int childDeviceCount = deviceGroupDao.getChildDeviceCount(group);
        mav.addObject("deviceCount", childDeviceCount);
        
        boolean groupModifiable = group.isModifiable() && group.getParent() != null;
        mav.addObject("groupModifiable", groupModifiable);
        
        boolean showImmediately = childDeviceCount < maxToShowImmediately;
        mav.addObject("showImmediately", showImmediately);
        
        if (showImmediately || showDevices) {
            deviceList = meterDao.getChildMetersByGroup(group);
            mav.addObject("deviceList", deviceList);
        }
                
        if (groupModifiable) {
            if (deviceList == null) {
                // it might have been loaded in the showImmediately block, if not we can load a slimmer version
                Set<YukonDevice> childDevices = deviceGroupDao.getChildDevices(group);
                deviceList = new ArrayList<YukonDevice>(childDevices);
            }
            List<Integer> deviceIdList = new MappingList<YukonDevice, Integer>(deviceList, new YukonDeviceToIdMapper());
            mav.addObject("deviceIdsInGroup", StringUtils.join(deviceIdList, ","));
        }
        
        // ALL GROUPS HIERARCHY
        DeviceGroupHierarchy allGroupsGroupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, new NonHiddenDeviceGroupPredicate());
        
        // NodeAttributeSettingCallback to highlight node fo selected group
        final DeviceGroup selectedDeviceGroup = group;
        class HighlightSelectedGroup implements NodeAttributeSettingCallback {
            public void setAdditionalAttributes(ExtTreeNode node, DeviceGroup deviceGroup) {
                if (selectedDeviceGroup != null && selectedDeviceGroup.getFullName().equals(deviceGroup.getFullName())) {
                    node.setAttribute("cls", "highlighted");
                }
            }
        }
        
        // ALL GROUPS TREE JSON
        ExtTreeNode allGroupsRoot = DeviceGroupTreeUtils.makeDeviceGroupExtTree(allGroupsGroupHierarchy, "Groups", new HighlightSelectedGroup());
        
        JSONObject allGroupsJsonObj = new JSONObject(allGroupsRoot.toMap());
        String allGroupsDataJson = allGroupsJsonObj.toString();
        mav.addObject("allGroupsDataJson", allGroupsDataJson);
        
        
        // MODIFIABLE NO-CHILDREN HIERARCHY
        Predicate<DeviceGroup> noChildrenPredicate = new Predicate<DeviceGroup>() {
            
            @Override
            public boolean evaluate(DeviceGroup deviceGroup) {
                
                return moveGroups.contains(deviceGroup);
            };
        };
        
        List<Predicate<DeviceGroup>> predicatesToCheck = new ArrayList<Predicate<DeviceGroup>>();
        predicatesToCheck.add(new ModifiableDeviceGroupPredicate());
        predicatesToCheck.add(noChildrenPredicate);
        
        AggregateAndPredicate<DeviceGroup> modifiableNoChildrenPredicate = new AggregateAndPredicate<DeviceGroup>(predicatesToCheck);
        DeviceGroupHierarchy modifiableNoChildrenGroupHierarchy = deviceGroupService.getFilteredDeviceGroupHierarchy(allGroupsGroupHierarchy, modifiableNoChildrenPredicate);
        
        // MODIFIABLE NO-CHILDREN GROUPS TREE JSON
        ExtTreeNode modifiableNoChildrenGroupsRoot = DeviceGroupTreeUtils.makeDeviceGroupExtTree(modifiableNoChildrenGroupHierarchy, "Groups", null);
        
        JSONObject modifiableNoChildrenGroupsJsonObj = new JSONObject(modifiableNoChildrenGroupsRoot.toMap());
        String modifiableNoChildrenGroupsDataJson = modifiableNoChildrenGroupsJsonObj.toString();
        mav.addObject("modifiableNoChildrenGroupsDataJson", modifiableNoChildrenGroupsDataJson);
        
        // DEVICE COLLECTION
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(selectedDeviceGroup);
        mav.addObject("deviceCollection", deviceCollection);
        
        return mav;

    }
    
    private MapQueue<DeviceGroup, DeviceGroup> getChildList(List<DeviceGroup> groups) {
        
        MapQueue<DeviceGroup, DeviceGroup> childList = new MapQueue<DeviceGroup, DeviceGroup>();
        for (DeviceGroup deviceGroup : groups) {
            if (deviceGroup.getParent() != null) {
                childList.add(deviceGroup.getParent(), deviceGroup);
            }
        }
        
        return childList;
    }
    
    /**
     * Narrow list of all groups to those that are eligible to have the current group moved to.
     * Can't move to self.
     * Can't move to a decendant.
     * Can't move to parent.
     * @param groups All available groups
     * @param group The current group (the one to be moved)
     * @return
     */
    private List<DeviceGroup> getMoveGroups(List<DeviceGroup> groups, DeviceGroup group) {
        
        List<DeviceGroup> moveGroups = new ArrayList<DeviceGroup>();
        for (DeviceGroup deviceGroup : groups) {
            if (deviceGroup.isModifiable() 
                && !deviceGroup.equals(group) 
                && !deviceGroup.isDescendantOf(group)
                && !deviceGroup.equals(group.getParent())) {
                moveGroups.add(deviceGroup);
            }
        }
        
        return moveGroups;
    }
    
    private List<DeviceGroup> getCopyToGroups(List<DeviceGroup> allGroups, DeviceGroup fromGroup) {
        
        List<DeviceGroup> okCopyToGroups = new ArrayList<DeviceGroup>();
        
        for (DeviceGroup possibleCopyToGroup : allGroups) {
            if (possibleCopyToGroup.isModifiable() 
                && !possibleCopyToGroup.equals(fromGroup) 
                && !possibleCopyToGroup.isDescendantOf(fromGroup)
                ) {
                
                okCopyToGroups.add(possibleCopyToGroup);
            }
        }
        return okCopyToGroups; 
    }
    
    public ModelAndView getDevicesForGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("deviceMembers.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        List<Meter> deviceList = meterDao.getChildMetersByGroup(group);
        
        Collections.sort(deviceList, meterDao.getMeterComparator());
        
        mav.addObject("deviceList", deviceList);

        mav.addObject("group", group);

        return mav;

    }

    public ModelAndView updateGroupName(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_EDIT);
        
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
        
        try {
            deviceGroupEditorDao.updateGroup(group);
        } catch (DuplicateException e){
            mav.addObject("errorMessage", e.getMessage());
            return mav;
        }

        mav.addObject("groupName", group.getFullName());

        return mav;

    }

    public ModelAndView addChild(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_MODIFY);
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        if (group.isModifiable()) {

            String childGroupName = ServletRequestUtils.getStringParameter(request,
                                                                           "childGroupName");

            // Make sure a new name was entered and doesn't contain slashes
            childGroupName = childGroupName.trim();
            if (StringUtils.isEmpty(childGroupName) || CtiUtilities.isContainsInvalidDeviceGroupNameCharacters(childGroupName)) {
                mav.addObject("errorMessage",
                              "You must enter a Sub Group Name.  Group names may not contain slashes.");
                return mav;
            }

            try {
                StoredDeviceGroup newGroup = deviceGroupEditorDao.addGroup((StoredDeviceGroup) group,
                                              DeviceGroupType.STATIC,
                                              childGroupName);
                
                
                
                mav.addObject("groupName", newGroup.getFullName());
            } catch (DuplicateException e) {
                mav.addObject("errorMessage", e.getMessage());
                return mav;
            }

        } else {
            mav.addObject("errorMessage", "Cannot add sub group to " + group.getFullName());
            return mav;
        }

        return mav;
    }

    public ModelAndView addDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_MODIFY);
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        if (group.isModifiable()) {

            String deviceId = ServletRequestUtils.getStringParameter(request, "deviceId");

            List<Integer> deviceList = convertStringToIdList(deviceId);

            deviceGroupMemberEditorDao.addDevicesById(group, deviceList.iterator());
        } else {
            mav.addObject("errorMessage", "Cannot add devices to " + group.getFullName());
            return mav;
        }

        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices");
        mav.addObject("showDevices", showDevices);

        return mav;
    }

    private List<Integer> convertStringToIdList(String deviceId) {
        String[] ids = StringUtils.split(deviceId, ",");

        List<Integer> deviceList = new ArrayList<Integer>();
        for (String id : ids) {
            id = id.trim();
            
            if (!StringUtils.isEmpty(id)) {
                Integer idInt = Integer.parseInt(id);
                deviceList.add(idInt);
            }
        }
        return deviceList;
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
        DeviceGroupHierarchy groupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, new NonHiddenDeviceGroupPredicate());
        
        // NodeAttributeSettingCallback to highlight node fo selected group
        class DisableCurrentGroup implements NodeAttributeSettingCallback {
            public void setAdditionalAttributes(ExtTreeNode node, DeviceGroup deviceGroup) {
                
                if (group.equals(deviceGroup)) {
                    node.setAttribute("disabled", true);
                }
            }
        }
        
        ExtTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupExtTree(groupHierarchy, "Groups", new DisableCurrentGroup());
        JSONObject jsonObj = new JSONObject(root.toMap());
        String dataJson = jsonObj.toString();
        
        mav.addObject("groupDataJson", dataJson);

        return mav;
    }

    public ModelAndView addDevicesByCollection(HttpServletRequest request,
                                                 HttpServletResponse response) throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_MODIFY);
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");
        
        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);
        
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);

        deviceCollectionDeviceGroupHelper.addCollectionToGroup(groupName, deviceCollection);

        return mav;
    }

    public ModelAndView removeDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_MODIFY);
        
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
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_EDIT);
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        String parentGroupName = ServletRequestUtils.getStringParameter(request, "parentGroupName");

        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        StoredDeviceGroup parentGroup = deviceGroupEditorDao.getStoredGroup(parentGroupName, false);

        // Make sure we can move the group
        try {
            
            if (group.isEditable()) {
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
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_MODIFY);
        
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
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_EDIT);
        
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
    
    public ModelAndView removeAllDevicesFromGroup(HttpServletRequest request, HttpServletResponse response)
        throws ServletException {
    
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        authDao.verifyRole(userContext.getYukonUser(), DeviceActionsRole.ROLEID);
        authDao.verifyTrueProperty(userContext.getYukonUser(), DeviceActionsRole.DEVICE_GROUP_MODIFY);
        
        ModelAndView mav = new ModelAndView("redirect:/spring/group/editor/home");
        
        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);
        
        StoredDeviceGroup removeGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
        
        // Make sure we can remove the group
        if (removeGroup.isEditable()) {
            
            List<? extends YukonDevice> deviceList = meterDao.getChildMetersByGroup(removeGroup);
            deviceGroupMemberEditorDao.removeDevices(removeGroup, deviceList);
        } else {
            mav.addObject("errorMessage", "Cannot remove Group: " + removeGroup.getFullName());
        }
        
        return mav;
    }

}
