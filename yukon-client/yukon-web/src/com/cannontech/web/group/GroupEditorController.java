package com.cannontech.web.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.bulk.collection.device.DeviceCollectionCreationException;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
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
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.predicate.NullPredicate;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.JsTreeBuilderUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsTreeNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableSet;

@RequestMapping("/editor/*")
@Controller
public class GroupEditorController {

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupUiService deviceGroupUiService;
    @Autowired private DeviceGroupProviderDao deviceGroupDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private CopyDeviceGroupService copyDeviceGroupService;
    @Autowired private DeviceCollectionDeviceGroupHelper deviceCollectionDeviceGroupHelper;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceGroupComposedDao deviceGroupComposedDao;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;

    private final int maxToShowImmediately = 10;
    private final int maxGetDevicesSize = 1000;
    
    @GetMapping("home")
    public String home(ModelMap model, HttpServletRequest request, HttpServletResponse response)
            throws Exception, ServletException {
        populateModelForHome(request, model);
        return "home.jsp";
    }
    
    private void populateModelForHome(HttpServletRequest request, ModelMap model) throws ServletRequestBindingException, JsonProcessingException {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        DeviceGroup group = null;
        String errorMessage = ServletRequestUtils.getStringParameter(request, "errorMessage", null);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        model.addAttribute("rootGroup", rootGroup);
        
        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices", false);
        model.addAttribute("showDevices", showDevices);

        DeviceGroupHierarchy everythingHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new NullPredicate<DeviceGroup>());
        
        DeviceGroupHierarchy allGroupsGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(everythingHierarchy, new NonHiddenDeviceGroupPredicate());
        
        if (!StringUtils.isEmpty(groupName)) {
            
            try {
                group = deviceGroupService.resolveGroupName(groupName);
            } catch (NotFoundException e) {
                model.addAttribute("errorMessage", e.getMessage());
                String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
                
                // ALL GROUPS TREE JSON
                HighlightSelectedGroupNodeAttributeSettingCallback callback = new HighlightSelectedGroupNodeAttributeSettingCallback(rootGroup);
                JsTreeNode allGroupsRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(allGroupsGroupHierarchy, groupsLabel, callback);
                
                // selected node Ext path
                String extSelectedNodePath = callback.getJsTreeSelectedNodePath();
                model.addAttribute("extSelectedNodePath", extSelectedNodePath);
                
                String allGroupsDataJson = JsonUtils.toJson(allGroupsRoot.toMap(), true);
                model.addAttribute("allGroupsDataJson", allGroupsDataJson);
                return;
            }
            
        } else {
            group = rootGroup;
        }
        
        final DeviceGroup selectedDeviceGroup = group;
        
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        
        // ALL GROUPS TREE JSON
        HighlightSelectedGroupNodeAttributeSettingCallback callback = new HighlightSelectedGroupNodeAttributeSettingCallback(selectedDeviceGroup);
        JsTreeNode allGroupsRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(allGroupsGroupHierarchy, groupsLabel, callback);
        
        // selected node Ext path
        String extSelectedNodePath = callback.getJsTreeSelectedNodePath();
        model.addAttribute("extSelectedNodePath", extSelectedNodePath);
        
        String allGroupsDataJson = JsonUtils.toJson(allGroupsRoot.toMap(), true);
        model.addAttribute("allGroupsDataJson", allGroupsDataJson);
        
        model.addAttribute("group", group);
        model.addAttribute("groupFullName", group.getFullName());
                
    }
    
    private void populateModelWithGroup(HttpServletRequest request, ModelMap model, YukonUserContext userContext) 
            throws ServletRequestBindingException, JsonProcessingException {
        String errorMessage = ServletRequestUtils.getStringParameter(request, "errorMessage", null);
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }

        DeviceGroup group = getSelectedDeviceGroup(request);
        if (group == null) {
            model.addAttribute("errorMessage", messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.editor.groupNotFoundError"));
            return;
        }
        
        model.addAttribute("group", group);
        model.addAttribute("groupFullName", group.getFullName());
        String nodeId = JsTreeBuilderUtil.createUniqueNodeId(group.getFullName(), new HashMap<String, Integer>());
        model.addAttribute("extSelectedNodePath", nodeId);

        
        // sub groups (child groups)
        List<DeviceGroup> childGroups = deviceGroupDao.getChildGroups(group);
        
        Map<DeviceGroup,Integer> subGroupMap = new TreeMap<>();
        
        for (DeviceGroup childGroup : childGroups) {
            if(!childGroup.isHidden()){
                int deviceCount = deviceGroupDao.getDeviceCount(childGroup);
                subGroupMap.put(childGroup, deviceCount);
            }
        }

        final DeviceGroup selectedDeviceGroup = group;
        
        model.addAttribute("subGroupMap",subGroupMap);
        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices", false);
        model.addAttribute("showDevices", showDevices);
        
        // check the size of the group
        int childDeviceCount = deviceGroupDao.getChildDeviceCount(group);
        model.addAttribute("childDeviceCount", childDeviceCount);
        
        // check the size of the group (recursive)
        int deviceCount = deviceGroupDao.getDeviceCount(group);
        model.addAttribute("deviceCount", deviceCount);
        
        boolean groupModifiable = group.isModifiable() && group.getParent() != null;
        model.addAttribute("groupModifiable", groupModifiable);
        
        boolean isComposedGroup = group.getType().equals(DeviceGroupType.COMPOSED);
        model.addAttribute("isComposedGroup", isComposedGroup);
        
        boolean showImmediately = childDeviceCount <= maxToShowImmediately;
        model.addAttribute("showImmediately", showImmediately);
        model.addAttribute("maxGetDevicesSize", maxGetDevicesSize);
        if (showImmediately || showDevices) {
            List<DisplayablePao> deviceList = deviceGroupUiService.getChildDevicesByGroup(group, maxGetDevicesSize + 1);
            if (deviceList.size() > maxGetDevicesSize) {
                model.addAttribute("limted", true);
                deviceList = deviceList.subList(0, maxGetDevicesSize);
            }
            
            model.addAttribute("deviceList", deviceList);
            model.addAttribute("maxGetDevicesSize", maxGetDevicesSize);
        }
        
        // DEVICE COLLECTION
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(selectedDeviceGroup);
        model.addAttribute("deviceCollection", deviceCollection);

    }
    
    @GetMapping("selectedDeviceGroup")
    public String selectedDeviceGroup(HttpServletRequest request, ModelMap model, YukonUserContext userContext)
            throws Exception, ServletException {
        populateModelWithGroup(request, model, userContext);
        return "selectedDeviceGroup.jsp";
    }
    
    @GetMapping("allGroupsJson")
    public void allGroupsJson(YukonUserContext userContext, HttpServletRequest request, HttpServletResponse resp) 
            throws ServletRequestBindingException, IOException {
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();

        DeviceGroupHierarchy everythingHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new NullPredicate<DeviceGroup>());
        
        DeviceGroupHierarchy allGroupsGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(everythingHierarchy, new NonHiddenDeviceGroupPredicate());
        
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        
        // ALL GROUPS TREE JSON
        HighlightSelectedGroupNodeAttributeSettingCallback callback = new HighlightSelectedGroupNodeAttributeSettingCallback(rootGroup);
        JsTreeNode allGroupsRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(allGroupsGroupHierarchy, groupsLabel, callback);
        
        String allGroupsDataJson = JsonUtils.toJson(allGroupsRoot.toMap(), true);
        
        resp.setContentType("application/json");
        resp.getWriter().write(allGroupsDataJson);
    }
    
    @GetMapping("copyGroupJson")
    public void copyGroupJson(YukonUserContext userContext, HttpServletRequest request, HttpServletResponse resp) 
            throws ServletRequestBindingException, IOException {
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy everythingHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new NullPredicate<DeviceGroup>());
        DeviceGroupHierarchy allGroupsGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(everythingHierarchy, new NonHiddenDeviceGroupPredicate());
        
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        
        String copyGroupJson = "";
        
        DeviceGroup group = getSelectedDeviceGroup(request);
        
        if (group != null) {
        
            final DeviceGroup selectedDeviceGroup = group;
            
            Predicate<DeviceGroup> canCopyIntoPredicate = new Predicate<DeviceGroup>() {
                @Override
                public boolean evaluate(DeviceGroup receivingGroup) {
                    return !receivingGroup.isEqualToOrDescendantOf(selectedDeviceGroup) && receivingGroup.isModifiable();
                }
            };
            DeviceGroupHierarchy copyGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(allGroupsGroupHierarchy, canCopyIntoPredicate);
            JsTreeNode copyExtRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(copyGroupHierarchy, groupsLabel, Collections.emptySet());
            
            copyGroupJson = JsonUtils.toJson(copyExtRoot.toMap(), true);
        }
        
        resp.setContentType("application/json");
        resp.getWriter().write(copyGroupJson);
    }
    
    @GetMapping("moveGroupJson")
    public void moveGroupJson(YukonUserContext userContext, HttpServletRequest request, HttpServletResponse resp) 
            throws ServletRequestBindingException, IOException {
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy everythingHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new NullPredicate<DeviceGroup>());
        DeviceGroupHierarchy allGroupsGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(everythingHierarchy, new NonHiddenDeviceGroupPredicate());
        
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        
        String moveGroupJson = "";
        
        DeviceGroup group = getSelectedDeviceGroup(request);
        
        if (group != null) {
        
            final DeviceGroup selectedDeviceGroup = group;
            
            Predicate<DeviceGroup> canMoveUnderPredicate = deviceGroupDao.getGroupCanMovePredicate(selectedDeviceGroup);
            DeviceGroupHierarchy moveGroupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(allGroupsGroupHierarchy, canMoveUnderPredicate);
            JsTreeNode moveGroupRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(moveGroupHierarchy, groupsLabel, Collections.emptySet());
            
            moveGroupJson = JsonUtils.toJson(moveGroupRoot.toMap(), true);
        }

        resp.setContentType("application/json");
        resp.getWriter().write(moveGroupJson);
    }
    
    private DeviceGroup getSelectedDeviceGroup(HttpServletRequest request) throws ServletRequestBindingException {
        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        DeviceGroup group = null;
        if (!StringUtils.isEmpty(groupName)) {
            try {
                group = deviceGroupService.resolveGroupName(groupName);
            } catch (NotFoundException e) {
                return null;
            }
        } else {
            DeviceGroup rootGroup = deviceGroupService.getRootGroup();
            group = rootGroup;
        }
        return group;
    }
    
    @RequestMapping("getDevicesForGroup")
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

    @RequestMapping("updateGroupName")
    @CheckRoleProperty(YukonRoleProperty.DEVICE_GROUP_EDIT)
    public String updateGroupName(ModelMap model, String groupName, String newGroupName) {

        // Make sure a new name was entered and doesn't contain slashes
        newGroupName = newGroupName.trim();
        if (!DeviceGroupUtil.isValidName(newGroupName)) {
            model.addAttribute("errorMessage",
                          "You must enter a New Group Name.  Group names may not contain slashes or exceed 60 characters.");
            return "redirect:/group/editor/home";
        }

        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        group.setName(newGroupName);

        if(!group.isEditable()){
            model.addAttribute("errorMessage", "Non-editable groups cannot be updated.");
            return "redirect:/group/editor/home";
        }

        try {
            deviceGroupEditorDao.updateGroup(group);
        } catch (DuplicateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/group/editor/home";
        }

        model.addAttribute("groupName", group.getFullName());

        return "redirect:/group/editor/home";

    }

    @RequestMapping("addChild")
    public ModelAndView addChild(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        ModelAndView deviceGroupMav = new ModelAndView("redirect:/group/editor/home");
        ModelAndView composedGroupMav = new ModelAndView("redirect:/group/composedGroup/build");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        DeviceGroupType subGroupType = DeviceGroupType.valueOf(ServletRequestUtils.getStringParameter(request, "subGroupType"));
        
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        if (group.isModifiable()) {

            String childGroupName = ServletRequestUtils.getStringParameter(request, "childGroupName");

            // Make sure a new name was entered and doesn't contain slashes
            childGroupName = childGroupName.trim();
            if (StringUtils.isEmpty(childGroupName) || !DeviceGroupUtil.isValidName(childGroupName)) {
                
                deviceGroupMav.addObject("groupName", groupName);
                deviceGroupMav.addObject("errorMessage", "You must enter a Sub Group Name.  Group names may not contain slashes or exceed 60 characters.");
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

    @RequestMapping("showAddDevicesByCollection")
    public ModelAndView showAddDevicesByCollection(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, JsonProcessingException {

        ModelAndView mav = new ModelAndView("addDevices.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        final DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        mav.addObject("group", group);
        
        mav.addObject("groupName", groupName);
        
        // Ext tree JSON
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new NonHiddenDeviceGroupPredicate());
        
        DisableNodeCallback cb = DisableNodeCallback.of(group);
        Set<? extends NodeAttributeSettingCallback<DeviceGroup>> callbacks = ImmutableSet.of(cb);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        
        JsTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupJsTree(groupHierarchy, groupsLabel, callbacks);
        String dataJson = JsonUtils.toJson(root.toMap(), true);
        
        mav.addObject("groupDataJson", dataJson);
        
        return mav;
    }
    
    @RequestMapping("addDevicesByCollection")
    public ModelAndView addDevicesByCollection(HttpServletRequest request,
                                                 HttpServletResponse response) throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/group/editor/home");
        
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

    @RequestMapping("removeDevice")
    public ModelAndView removeDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/group/editor/home");

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

    @RequestMapping("moveGroup")
    public ModelAndView moveGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_EDIT, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/group/editor/home");

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
    
    @RequestMapping("copyContentsToGroup")
    public ModelAndView copyContentsToGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_MODIFY, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/group/editor/home");
        
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
    
    @RequestMapping(value="removeGroup", method=RequestMethod.POST)
    public ModelAndView removeGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        rolePropertyDao.verifyProperty(YukonRoleProperty.DEVICE_GROUP_EDIT, userContext.getYukonUser());
        
        ModelAndView mav = new ModelAndView("redirect:/group/editor/home");

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
    
    @RequestMapping("removeAllDevicesFromGroup")
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
            if (removeGroup.isModifiable()) {
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

}