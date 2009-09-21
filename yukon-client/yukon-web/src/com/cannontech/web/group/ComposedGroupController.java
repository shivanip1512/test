package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.groups.dao.DeviceGroupComposedDao;
import com.cannontech.common.device.groups.dao.DeviceGroupComposedGroupDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedCompositionType;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupUiService;
import com.cannontech.common.device.groups.service.NotEqualToOrDecendantOfGroupsPredicate;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.ExtTreeNode;

@Controller
@RequestMapping("/composedGroup/*")
public class ComposedGroupController {
    
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupUiService deviceGroupUiService;
    private DeviceGroupComposedDao deviceGroupComposedDao;
    private DeviceGroupComposedGroupDao deviceGroupComposedGroupDao;
    
    @RequestMapping
    public String build(HttpServletRequest request, LiteYukonUser user, ModelMap model) throws ServletException {
        
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
            
            saveComposedGroup(composedGroup, compositionType, request);
            model.addAttribute("groupName", groupName);
            return "redirect:/spring/group/editor/home";
        }
        
        Collections.sort(displayableComposedGroups);
        model.addAttribute("groupName", groupName);
        model.addAttribute("availableCompositionTypes", Arrays.asList(DeviceGroupComposedCompositionType.values()));
        model.addAttribute("selectedCompositionType", compositionType);
        model.addAttribute("groups", displayableComposedGroups);
        
        // tree json
        List<Predicate<DeviceGroup>> predicates = new ArrayList<Predicate<DeviceGroup>>();
        predicates.add(new NonHiddenDeviceGroupPredicate());
        predicates.add(new NotEqualToOrDecendantOfGroupsPredicate(group));
        AggregateAndPredicate<DeviceGroup> aggregatePredicate = new AggregateAndPredicate<DeviceGroup>(predicates);
        
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy allGroupsGroupHierarchy = deviceGroupUiService.getDeviceGroupHierarchy(rootGroup, new NonHiddenDeviceGroupPredicate());
        DeviceGroupHierarchy groupHierarchy = deviceGroupUiService.getFilteredDeviceGroupHierarchy(allGroupsGroupHierarchy, aggregatePredicate);
        ExtTreeNode groupRoot = DeviceGroupTreeUtils.makeDeviceGroupExtTree(groupHierarchy, "Groups", null);
        
        JSONObject chooseGrouptreeJsonObj = new JSONObject(groupRoot.toMap());
        String chooseGroupTreeJson = chooseGrouptreeJsonObj.toString();
        model.addAttribute("chooseGroupTreeJson", chooseGroupTreeJson);
        
        return "composedGroup/create.jsp";
    }
    
    @SuppressWarnings("unchecked")
    private List<DisplayableComposedGroup> getGroupsFromPage(HttpServletRequest request) throws ServletRequestBindingException {
        
        List<DisplayableComposedGroup> newDisplayableComposedGroups = new ArrayList<DisplayableComposedGroup>(2);
        
        Enumeration parameterNames = request.getParameterNames();
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
    
    private void saveComposedGroup(DeviceGroupComposed composedGroup, DeviceGroupComposedCompositionType compositionType, HttpServletRequest request) throws ServletRequestBindingException {

        // remove current groups
        deviceGroupComposedGroupDao.removeAllGroups(composedGroup.getDeviceGroupComposedId());
        
        // update composition type
        composedGroup.setDeviceGroupComposedCompositionType(compositionType);
        deviceGroupComposedDao.saveOrUpdate(composedGroup);
        
        // add new groups
        List<DisplayableComposedGroup> displayableComposedGroups = getGroupsFromPage(request);
        for (DisplayableComposedGroup displayableComposedGroup : displayableComposedGroups) {
            
            String groupFullName = displayableComposedGroup.getGroupFullName();
            String[] groupFullNameParts = groupFullName.split("/");
            if (groupFullNameParts.length > 0 && CtiUtilities.isValidGroupName(groupFullNameParts[groupFullNameParts.length - 1])) {
                
                DeviceGroup deviceGroup = deviceGroupService.findGroupName(groupFullName);
                if(deviceGroup == null) {
                    continue;
                }
                
                DeviceGroupComposedGroup compositionGroup = new DeviceGroupComposedGroup();
                compositionGroup.setDeviceGroupComposedId(composedGroup.getDeviceGroupComposedId());
                compositionGroup.setDeviceGroup(deviceGroup);
                compositionGroup.setNot(displayableComposedGroup.isNegate());
                
                deviceGroupComposedGroupDao.saveOrUpdate(compositionGroup);
            }
        }
    }
    
    private List<DisplayableComposedGroup> removeGroup(HttpServletRequest request, int removeRow) throws ServletRequestBindingException {
        
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
    
    @Autowired
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupUiService(DeviceGroupUiService deviceGroupUiService) {
        this.deviceGroupUiService = deviceGroupUiService;
    }
    
    @Autowired
    public void setDeviceGroupComposedDao(DeviceGroupComposedDao deviceGroupComposedDao) {
        this.deviceGroupComposedDao = deviceGroupComposedDao;
    }
    
    @Autowired
    public void setDeviceGroupComposedGroupDao(DeviceGroupComposedGroupDao deviceGroupComposedGroupDao) {
        this.deviceGroupComposedGroupDao = deviceGroupComposedGroupDao;
    }
}
