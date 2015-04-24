package com.cannontech.web.admin.userGroupEditor;

import static com.cannontech.common.util.StringUtils.parseIntStringForList;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.security.csrf.CsrfTokenService;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/roleGroup/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class RoleGroupEditorController {
    
    @Autowired private AuthenticationService authService;
    @Autowired private RoleDao roleDao;
    @Autowired private RolePropertyEditorDao rolePropertyEditorDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonGroupDao roleGroupDao;
    @Autowired private RoleGroupValidator validator;
    @Autowired private CsrfTokenService csrfTokenService;
    
    private static final String key = "yukon.web.modules.adminSetup.roleGroupEditor.";
    private static final String userGroupKey = "yukon.web.modules.adminSetup.userGroups.";
    
    @RequestMapping("view")
    public String view(ModelMap model, int roleGroupId) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        LiteYukonGroup group = roleGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, group);
        
        Set<YukonRole> roles = roleDao.getRolesForGroup(roleGroupId);
        RoleListHelper.addRolesToModel(roles, model);
        
        return "userGroupEditor/group.jsp";
    }
    
    private void setupModelMap(ModelMap model, LiteYukonGroup group) {
        model.addAttribute("group", group);
        model.addAttribute("roleGroupId", group.getGroupID());
        model.addAttribute("roleGroupName", group.getGroupName());
        model.addAttribute("editName", group.getGroupID() > -1 ? true : false);
    }

    /* Group Editor Edit Page*/
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(ModelMap model, int roleGroupId) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonGroup group = roleGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, group);
        
        return "userGroupEditor/group.jsp";
    }
    
    /* Expire all users in this group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="expireAllPasswords")
    public String expireAllPasswords(ModelMap model, FlashScope flash, int roleGroupId) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonGroup roleGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, roleGroup);
        
        authService.expireAllPasswords(roleGroup.getGroupID());
        flash.setConfirm(new YukonMessageSourceResolvable(key + "expiredAllPasswords"));
        
        return "redirect:view";
    }
    
    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="update")
    public String update(ModelMap model, FlashScope flash, 
            @ModelAttribute("group") LiteYukonGroup group, BindingResult result) {
        
        validator.validate(group, result);
        
        if (result.hasErrors()) {
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModelMap(model, group);
            
            return "userGroupEditor/group.jsp";
        }
        
        roleGroupDao.save(group);
        
        flash.setConfirm(new YukonMessageSourceResolvable(key + "updateSuccessful"));
        model.addAttribute("roleGroupId", group.getGroupID());
        
        return "redirect:view";
    }
    
    /* Cancel */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="cancel")
    public String cancel(int roleGroupId, ModelMap model) {
        model.addAttribute("roleGroupId", roleGroupId);
        return "redirect:view";
    }
    
    /* Delete Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="delete")
    public String delete(int roleGroupId, FlashScope flash) {
        
        roleGroupDao.delete(roleGroupId);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "deletedSuccessful"));
        
        return "redirect:/adminSetup/userEditor/home";
    }
    
    /* User Groups */
    @RequestMapping("userGroups")
    public String userGroups(ModelMap model, int roleGroupId) {
        
        LiteYukonGroup roleGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        List<LiteUserGroup> userGroups = userGroupDao.getLiteUserGroupsByRoleGroupId(roleGroupId);
        
        model.addAttribute("userGroups", userGroups);
        model.addAttribute("alreadyAssignedUserGroupIds", Lists.transform(userGroups, LiteBase.ID_FUNCTION));
        setupModelMap(model, roleGroup);
        
        return "userGroupEditor/userGroups.jsp";
    }
    
    /* Add User Group */
    @RequestMapping(value="addUserGroups", method=RequestMethod.POST)
    public String addUserGroups(ModelMap model, FlashScope flash, int roleGroupId, String userGroupIds) 
    throws SQLException {
        
        List<Integer> userGroupIdsList = parseIntStringForList(userGroupIds);
        
        LiteYukonGroup roleGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        List<String> conflictingUserGroupNames = Lists.newArrayList();
        for (int userGroupId : userGroupIdsList) {
            
            UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
            try {
                userGroup.addRoleGroups(roleGroup);
                userGroup.update();
            } catch (ConfigurationException e) {
                conflictingUserGroupNames.add(userGroup.getUserGroup().getUserGroupName());
            }
        }
        
        // Display the response message.
        if (!conflictingUserGroupNames.isEmpty()) {
            flash.setError(new YukonMessageSourceResolvable(userGroupKey + "roleGroupConflict", 
                    roleGroup.getGroupName(), conflictingUserGroupNames));
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable(userGroupKey + "addedSuccessful"));
        }
        model.addAttribute("roleGroupId", roleGroupId);
        
        return "redirect:userGroups";
    }

    /* Remove User Group */
    @RequestMapping(value="removeUserGroups", method=RequestMethod.POST)
    public String removeUserGroups(ModelMap model, FlashScope flash, int roleGroupId, int remove) {
        
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(remove);
        
        userGroupDao.deleteUserGroupToYukonGroupMappng(remove, roleGroupId);
        flash.setConfirm(new YukonMessageSourceResolvable(userGroupKey + "removedSuccessful", userGroup.getUserGroupName()));
        model.addAttribute("roleGroupId", roleGroupId);
        
        return "redirect:userGroups";
    }
    
    /* Add Role */
    @RequestMapping(value="addRole", method=RequestMethod.POST)
    public String addRole(ModelMap model, FlashScope flash, int newRoleId, int roleGroupId) {
        
        LiteYukonGroup group = roleGroupDao.getLiteYukonGroup(roleGroupId);
        YukonRole role = YukonRole.getForId(newRoleId);
        
        /* Save the default role properties to the db for this group and role */
        try {
            rolePropertyEditorDao.addRoleToGroup(group, role);
        } catch (ConfigurationException e) {
            model.addAttribute("roleGroupId", roleGroupId);
            flash.setError(new YukonMessageSourceResolvable(key + "roleConflictingWithUserGroup", role.name(), group.getGroupName()));
            return "redirect:view";
        }
        
        model.addAttribute("roleGroupId", roleGroupId);
        model.addAttribute("roleId", newRoleId);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "updateSuccessful"));
        
        return "redirect:/adminSetup/roleEditor/view";
    }
    
}