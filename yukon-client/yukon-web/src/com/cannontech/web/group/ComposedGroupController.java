package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedDao;
import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedGroupDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedCompositionType;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.JsTreeNode;
import com.cannontech.web.util.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping("/composedGroup/*")
public class ComposedGroupController {

    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private DeviceGroupUiService deviceGroupUiService;
    @Autowired private DeviceGroupComposedDao deviceGroupComposedDao;
    @Autowired private DeviceGroupComposedGroupDao deviceGroupComposedGroupDao;
    @Autowired private TransactionOperations transactionTemplate;
   
    @RequestMapping("build")
    public String build(HttpServletRequest request, LiteYukonUser user, ModelMap model)
                throws ServletException, JsonProcessingException {
        
        // parameters
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceGroupComposedCompositionType compositionType = DeviceGroupComposedCompositionType.valueOf(ServletRequestUtils.getStringParameter(request, "compositionType", DeviceGroupComposedCompositionType.UNION.name()));
        
        // triggers
        boolean firstLoad = ServletRequestUtils.getBooleanParameter(request, "firstLoad", true);
        Map<String, String> removeRow = ServletUtil.getStringParameters(request, "removeRow");
        Map<String, String> addRow = ServletUtil.getStringParameters(request, "addRow");
        
        // groups
        StoredDeviceGroup group = deviceGroupEditorDao.getStoredGroup(groupName, false);
        DeviceGroupComposed composedGroup = deviceGroupComposedDao.findForDeviceGroupId(group.getId());
        List<DisplayableComposedGroup> displayableComposedGroups = new ArrayList<DisplayableComposedGroup>(2);
        
        String errorMsg = null;
        try {
            
            // FIRST LOAD
            if (firstLoad) {
                
                displayableComposedGroups = getCurrentGroupsForComposedGroup(composedGroup);
                compositionType = composedGroup.getDeviceGroupComposedCompositionType();
                    
                if (displayableComposedGroups.size() == 0) {
                    
                    displayableComposedGroups.add(new DisplayableComposedGroup(0));
                }
                
            // REMOVE ROW
            } else if (removeRow.size() > 0) {
                
                for (String key : removeRow.keySet()) {
                    String[] keyParts = StringUtils.split(key, ".");
                    int rowOrder = Integer.valueOf(keyParts[0]);
                    displayableComposedGroups = removeGroup(request, rowOrder);
                    break; // remove one row at a time, ignore extra ".x" and ".y" parameters
                }
                
            // ADD ROW
            } else if (addRow.size() > 0) {
                
                displayableComposedGroups = getGroupsFromPage(request);
                displayableComposedGroups.add(new DisplayableComposedGroup(displayableComposedGroups.size()));
            
            // SAVE
            } else {
                
                errorMsg = saveComposedGroup(group.getParent(), composedGroup, compositionType, request);
                
                if (errorMsg == null) {
                    model.addAttribute("groupName", groupName);
                    return "redirect:/group/editor/home";
                } else {
                    displayableComposedGroups = getCurrentGroupsForComposedGroup(composedGroup);
                }
            }
            
        } catch (NotFoundException e) {
            errorMsg = e.getMessage();
            displayableComposedGroups = getCurrentGroupsForComposedGroup(composedGroup);
        }
        
        Collections.sort(displayableComposedGroups);
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("groupName", groupName);
        model.addAttribute("availableCompositionTypes", Arrays.asList(DeviceGroupComposedCompositionType.values()));
        model.addAttribute("selectedCompositionType", compositionType);
        model.addAttribute("groups", displayableComposedGroups);
        
        // tree json
        List<Predicate<DeviceGroup>> predicates = new ArrayList<Predicate<DeviceGroup>>();
        predicates.add(new NonHiddenDeviceGroupPredicate());
        
		/*
		 * Creating predicates to remove some of the groups from the picker,
		 * which should not be added to this composed device group.
		 *  
		 * Example:
		 * 
		 * Alternate/Alternate Composed Group-> Contains Billing
		 * Billing/Billing Composed Group-> Adding Alternate to this group will result in stack overflow.
		 * 
		 * Creating predicate to remove the group "Alternate" from the the picker when adding groups to "Billing/Billing Composed Group".
		 */ 
        DeviceGroup parent = group;
        
        final Set<String> deviceGroupToExcludeNodePath = new HashSet<>();
        //This list contains the current group and its parents. Example:[/Billing/Billing Composed Group, /Billing]
        List<String> parentGroupNames = new ArrayList<>();
        while (!parent.getFullName().equals(DeviceGroupService.ROOT)) {
            parentGroupNames.add(parent.getFullName());
            deviceGroupToExcludeNodePath.add(parent.getFullName());
            parent = parent.getParent();
        }
      
        //Find composed groups the current group or one of its parents are part of
        List<DeviceGroupComposedGroup> composedGroups = deviceGroupComposedGroupDao.getByGroupNames(parentGroupNames);
		for (DeviceGroupComposedGroup deviceGroup : composedGroups) {
			DeviceGroupComposed deviceGroupComposed = deviceGroupComposedDao.getByComposedId(deviceGroup.getDeviceGroupComposedId());
			//Find the group the current group is part of
			DeviceGroup groupToExclude = deviceGroupEditorDao.getGroupById(deviceGroupComposed.getDeviceGroupId());
			
            while (!groupToExclude.getFullName().equals(DeviceGroupService.ROOT)) {
                deviceGroupToExcludeNodePath.add(groupToExclude.getFullName());
                groupToExclude = groupToExclude.getParent();
            }
		}
        
        AggregateAndPredicate<DeviceGroup> aggregatePredicate = new AggregateAndPredicate<DeviceGroup>(predicates);
        
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, aggregatePredicate);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        String groupsLabel = messageSourceResolver.getMessageSourceAccessor(userContext).getMessage("yukon.web.deviceGroups.widget.groupTree.rootName");
        NodeAttributeSettingCallback<DeviceGroup> nodeCallback = new NodeAttributeSettingCallback<DeviceGroup>() {
                @Override
                public void setAdditionalAttributes(JsTreeNode node, DeviceGroup deviceGroup) {
                    if (deviceGroupToExcludeNodePath.contains(deviceGroup.getFullName())) {
                        node.getAttributes().put("unselectable", true);
                    }
                }
            };
        JsTreeNode groupExtRoot = DeviceGroupTreeUtils.makeDeviceGroupJsTree(groupHierarchy, groupsLabel, nodeCallback);         
        String chooseGroupTreeJson = JsonUtils.toJson(groupExtRoot.toMap());
        model.addAttribute("chooseGroupTreeJson", chooseGroupTreeJson);
        
        return "composedGroup/create.jsp";
    }
      
    private List<DisplayableComposedGroup> getGroupsFromPage(HttpServletRequest request) throws ServletRequestBindingException, NotFoundException {
        
        List<DisplayableComposedGroup> newDisplayableComposedGroups = new ArrayList<DisplayableComposedGroup>(2);
        
        Enumeration<?> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            
            String parameterName = (String)parameterNames.nextElement();
            String[] nameParts = StringUtils.split(parameterName, "_");
            if (nameParts.length == 2) {
                
                String parameterType = nameParts[0];
                String order = nameParts[1];
                
                if (parameterType.equals("deviceGroupNameField")) {
                    
                    boolean isNot = ServletRequestUtils.getRequiredBooleanParameter(request, "notSelect_" + order);
                    String groupFullName = ServletRequestUtils.getRequiredStringParameter(request, "deviceGroupNameField_" + order);
                    
                    DeviceGroup deviceGroup = deviceGroupService.findGroupName(groupFullName);
                    if (deviceGroup == null && !StringUtils.isBlank(groupFullName)) {
                        throw new NotFoundException("Group Does Not Exist: " + groupFullName);
                    }
                    newDisplayableComposedGroups.add(new DisplayableComposedGroup(Integer.valueOf(order), deviceGroup, isNot));
                }
            }
        }
        
        Collections.sort(newDisplayableComposedGroups);
        return newDisplayableComposedGroups;
    }
    
    private List<DisplayableComposedGroup> getCurrentGroupsForComposedGroup(DeviceGroupComposed composedGroup) {
        
        List<DeviceGroupComposedGroup> compositionGroups = deviceGroupComposedGroupDao.getComposedGroupsForId(composedGroup.getDeviceGroupComposedId());
        
        int count = 0;
        List<DisplayableComposedGroup> groups = new ArrayList<DisplayableComposedGroup>(2);
        for (DeviceGroupComposedGroup compositionGroup : compositionGroups) {
            
            DisplayableComposedGroup displayableComposedGroup = new DisplayableComposedGroup(count, compositionGroup.getDeviceGroup(), compositionGroup.isNot());
            groups.add(displayableComposedGroup);
            count++;
        }
        
        return groups;
    }
    
    private String saveComposedGroup(final DeviceGroup parentGroup, final DeviceGroupComposed composedGroup, final DeviceGroupComposedCompositionType compositionType, HttpServletRequest request) throws ServletRequestBindingException, NotFoundException {
        
        final List<DisplayableComposedGroup> displayableComposedGroups = getGroupsFromPage(request);
        
        String saveError = (String)transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                
                String saveError = null;
                
                // remove current groups
                deviceGroupComposedGroupDao.removeAllGroups(composedGroup.getDeviceGroupComposedId());
                
                // update composition type
                composedGroup.setDeviceGroupComposedCompositionType(compositionType);
                deviceGroupComposedDao.saveOrUpdate(composedGroup);
                
                // add new groups
                for (DisplayableComposedGroup displayableComposedGroup : displayableComposedGroups) {
                    
                    String groupFullName = displayableComposedGroup.getGroupFullName();
                    DeviceGroup group = deviceGroupService.findGroupName(groupFullName);
                    if (group != null) {
                        
                        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(groupFullName);
                        DeviceGroupComposedGroup compositionGroup = new DeviceGroupComposedGroup();
                        compositionGroup.setDeviceGroupComposedId(composedGroup.getDeviceGroupComposedId());
                        compositionGroup.setDeviceGroup(deviceGroup);
                        compositionGroup.setNot(displayableComposedGroup.isNegate());
                        
                        deviceGroupComposedGroupDao.saveOrUpdate(compositionGroup);
                    }
                }
                
                return saveError;
            }
        });
        
        return saveError;
    }
    
    private List<DisplayableComposedGroup> removeGroup(HttpServletRequest request, int removeRow) throws ServletRequestBindingException, NotFoundException {
        
        List<DisplayableComposedGroup> displayableComposedGroups = getGroupsFromPage(request);
        
        int count = 0;
        List<DisplayableComposedGroup> newDisplayableComposedGroups = new ArrayList<DisplayableComposedGroup>();
        for (DisplayableComposedGroup displayableComposedGroup : displayableComposedGroups) {
            
            if (displayableComposedGroup.getOrder() != removeRow) {
                displayableComposedGroup.setOrder(count);
                newDisplayableComposedGroups.add(displayableComposedGroup);
                count++;
            }
        }
        
        return newDisplayableComposedGroups;
    }
    
    
    public class DisplayableComposedGroup implements Comparable<DisplayableComposedGroup> {
        
        private int order = 0;
        private String groupFullName = "";
        private boolean negate = false;
        
        DisplayableComposedGroup(int order) {
            this.order = order;
        }
        
        DisplayableComposedGroup(int order, DeviceGroup deviceGroup, boolean negate) {
            this.order = order;
            this.groupFullName = deviceGroup == null ? "" : deviceGroup.getFullName();
            this.negate = negate;
        }
        
        public int getOrder() {
            return order;
        }
        public void setOrder(int order) {
            this.order = order;
        }
        public String getGroupFullName() {
            return groupFullName;
        }
        public boolean isNegate() {
            return negate;
        }
        
        @Override
        public int compareTo(DisplayableComposedGroup o) {
            Integer thisOrder = this.getOrder();
            Integer otherOrder = o.getOrder();
            return thisOrder.compareTo(otherOrder);
        }
    }
}
