package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.groups.composed.dao.ComposedDeviceGroupService;
import com.cannontech.common.device.groups.composed.dao.ComposedGroup;
import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedDao;
import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedGroupDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedCompositionType;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.web.util.JsTreeNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

@Controller
@RequestMapping("/composedGroup/*")
public class ComposedGroupController {

    @Autowired private ComposedDeviceGroupService composedDeviceGroupService;
    @Autowired private DeviceGroupComposedDao deviceGroupComposedDao;
    @Autowired private DeviceGroupComposedGroupDao deviceGroupComposedGroupDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupUiService deviceGroupUiService;
    @Autowired private TransactionOperations transactionTemplate;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @RequestMapping("build")
    public String build(ModelMap model, String groupName) {

        // groups
        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        DeviceGroupComposed composedGroup = deviceGroupComposedDao.findForDeviceGroupId(group.getId());
        List<ComposedGroup> displayableComposedGroups = new ArrayList<>();

        displayableComposedGroups = composedDeviceGroupService.getGroupsForComposedGroup(composedGroup);

        if (displayableComposedGroups.size() == 0) {

            displayableComposedGroups.add(new ComposedGroup());
        }

        Collections.sort(displayableComposedGroups);

        ComposedGroupRules rules = new ComposedGroupRules();
        rules.setGroupName(groupName);
        rules.setCompositionType(composedGroup.getDeviceGroupComposedCompositionType());
        rules.setGroups(displayableComposedGroups);
        model.addAttribute("rules", rules);

        model.addAttribute("groupName", groupName);
        model.addAttribute("availableCompositionTypes", Arrays.asList(DeviceGroupComposedCompositionType.values()));

        Set<NodeAttributeSettingCallback<DeviceGroup>> callbacks = getComposedGroupCallbacks(group);
        model.addAttribute("callbacks", callbacks);

        return "composedGroup/create.jsp";
    }

    @RequestMapping("save")
    public String save(ModelMap model, @ModelAttribute("rules") ComposedGroupRules rules) {

        // groups
        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(rules.getGroupName(), false);
        final DeviceGroupComposed composedGroup = deviceGroupComposedDao.findForDeviceGroupId(group.getId());

        try {
            composedDeviceGroupService.save(composedGroup, rules.getCompositionType(), rules.getGroups());

            model.addAttribute("groupName", rules.getGroupName());
            return "redirect:/group/editor/home";

        } catch (NotFoundException e) {
            model.addAttribute("errorMsg", e.getMessage());
        }

        Collections.sort(rules.getGroups());
        model.addAttribute("availableCompositionTypes", Arrays.asList(DeviceGroupComposedCompositionType.values()));

        Set<NodeAttributeSettingCallback<DeviceGroup>> callbacks = getComposedGroupCallbacks(group);
        model.addAttribute("callbacks", callbacks);

        return "composedGroup/create.jsp";
    }

    /*
     * Creates callback to disable some of the groups from the picker,
     * which should not be added to this composed device group.
     *
     * Example:
     *
     * Alternate/Alternate Composed Group-> Contains Billing
     * Billing/Billing Composed Group-> Adding Alternate to this group will result in stack overflow.
     *
     * Creating predicate to remove the group "Alternate" from the the picker when adding groups to "Billing/Billing Composed Group".
     */
    private Set<NodeAttributeSettingCallback<DeviceGroup>> getComposedGroupCallbacks(DeviceGroup group) {
        DeviceGroup parent = group;

        Builder<String> deviceGroupsToExcludeBuilder = ImmutableSet.builder();
        //This list contains the current group and its parents. Example:[/Billing/Billing Composed Group, /Billing]
        List<String> parentGroupNames = new ArrayList<>();
        while (!parent.getFullName().equals(DeviceGroupService.ROOT)) {
            parentGroupNames.add(parent.getFullName());
            deviceGroupsToExcludeBuilder.add(parent.getFullName());
            parent = parent.getParent();
        }

        //Find composed groups the current group or one of its parents are part of
        for (DeviceGroupComposedGroup deviceGroup : deviceGroupComposedGroupDao.getByGroupNames(parentGroupNames)) {
            DeviceGroupComposed deviceGroupComposed = deviceGroupComposedDao.getByComposedId(deviceGroup.getDeviceGroupComposedId());
            //Find the group the current group is part of
            DeviceGroup groupToExclude = deviceGroupEditorDao.getGroupById(deviceGroupComposed.getDeviceGroupId());

            while (!groupToExclude.getFullName().equals(DeviceGroupService.ROOT)) {
                deviceGroupsToExcludeBuilder.add(groupToExclude.getFullName());
                groupToExclude = groupToExclude.getParent();
            }
        }

        final ImmutableSet<String> deviceGroupsToExclude = deviceGroupsToExcludeBuilder.build();

        NodeAttributeSettingCallback<DeviceGroup> nodeCallback = new NodeAttributeSettingCallback<DeviceGroup>() {
            @Override
            public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {
                if (deviceGroupsToExclude.contains(deviceGroup.getFullName())) {
                    node.getAttributes().put("unselectable", true);
                    node.setAttribute("disabled", true);
                }
            }

        };

        return ImmutableSet.of(nodeCallback);
    }
}