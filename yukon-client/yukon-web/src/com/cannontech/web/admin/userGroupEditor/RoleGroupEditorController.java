package com.cannontech.web.admin.userGroupEditor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.ConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.events.loggers.UsersEventLogService;
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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class RoleGroupEditorController {
    
    @Autowired private AuthenticationService authService;
    @Autowired private RoleDao roleDao;
    @Autowired private RolePropertyEditorDao rolePropertyEditorDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonGroupDao roleGroupDao;
    @Autowired private RoleGroupValidator validator;
    @Autowired private UsersEventLogService usersEventLogService;
    
    private static final String key = "yukon.web.modules.adminSetup.auth.role.group.";
    
    /* VIEW PAGE */
    @RequestMapping("role-groups/{roleGroupId}")
    public String view(ModelMap model, @PathVariable int roleGroupId) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        LiteYukonGroup group = roleGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, group);
        
        Set<YukonRole> roles = roleDao.getRolesForGroup(roleGroupId);
        RoleListHelper.addRolesToModel(roles, model);
        
        return "userGroupEditor/group.jsp";
    }
    
    /* EDIT PAGE */
    @RequestMapping("role-groups/{roleGroupId}/edit")
    public String edit(ModelMap model, @PathVariable int roleGroupId) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonGroup group = roleGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, group);
        
        return "userGroupEditor/group.jsp";
    }
    
    /* EXPIRE PASWORDS */
    @RequestMapping("role-groups/{roleGroupId}/expire-passwords")
    public String expireAllPasswords(ModelMap model, FlashScope flash, @PathVariable int roleGroupId,
            LiteYukonUser user) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonGroup roleGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, roleGroup);
        
        authService.expireAllPasswords(roleGroup.getGroupID());
        usersEventLogService.roleGroupExpirePasswords(roleGroup.getGroupName(), user);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "expiredAllPasswords"));
        
        return "redirect:/admin/role-groups/" + roleGroupId;
    }
    
    /* UPDATE */
    @RequestMapping(value="role-groups/{roleGroupId}", method=RequestMethod.POST, params="update")
    public String update(ModelMap model, FlashScope flash, @PathVariable int roleGroupId,
            @ModelAttribute("group") LiteYukonGroup group, BindingResult result,
            LiteYukonUser user) {

        validator.validate(group, result);
        
        if (result.hasErrors()) {
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModelMap(model, group);
            
            return "userGroupEditor/group.jsp";
        }
        
        roleGroupDao.save(group);
        usersEventLogService.roleGroupUpdated(group.getGroupName(), user);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "updateSuccessful"));
        
        return "redirect:/admin/role-groups/" + roleGroupId;
    }
    
    /* DELETE */
    @RequestMapping(value="role-groups/{roleGroupId}", method=RequestMethod.POST, params="delete")
    public String delete(@PathVariable int roleGroupId, FlashScope flash, LiteYukonUser user) {
        
        LiteYukonGroup roleGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        roleGroupDao.delete(roleGroupId);
        usersEventLogService.roleGroupDeleted(roleGroup.getGroupName(), user);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "deletedSuccessful"));
        
        return "redirect:/admin/users-groups/home";
    }
    
    /* ADD USER GROUPS */
    @RequestMapping(value="role-groups/{roleGroupId}/add-user-groups", method=RequestMethod.POST)
    public void addRoleGroups(ModelMap model, FlashScope flash, 
            @PathVariable int roleGroupId, @RequestParam("groups[]") int[] groups, LiteYukonUser user)
    throws SQLException {
        
        LiteYukonGroup roleGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        List<String> conflicts = new ArrayList<>();
        for (int userGroupId : groups) {
            
            UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
            try {
                userGroup.addRoleGroups(roleGroup);
                userGroup.update();
                usersEventLogService.roleGroupAdded(userGroup.getUserGroup().getUserGroupName(),
                    roleGroup.getGroupName(), user);
            } catch (ConfigurationException e) {
                conflicts.add(userGroup.getUserGroup().getUserGroupName());
            }
        }
        
        if (!conflicts.isEmpty()) {
            flash.setError(new YukonMessageSourceResolvable(key + "roleGroupConflict", 
                    roleGroup.getGroupName(), conflicts));
        } else {
            flash.setConfirm(new YukonMessageSourceResolvable(key + "addedSuccessful"));
        }
    }
    
    /* REMOVE USER GROUP */
    @RequestMapping(value="role-groups/{roleGroupId}/remove-user-group", method=RequestMethod.POST)
    public String removeUserGroups(ModelMap model, FlashScope flash, @PathVariable int roleGroupId, int remove,
            LiteYukonUser user) {
        
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(remove);
        LiteYukonGroup roleGroup = roleGroupDao.getLiteYukonGroup(roleGroupId);
        userGroupDao.deleteUserGroupToYukonGroupMappng(remove, roleGroupId);
        usersEventLogService.roleGroupRemoved(userGroup.getUserGroupName(), roleGroup.getGroupName(), user);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "removedSuccessful", userGroup.getUserGroupName()));
        
        return "redirect:/admin/role-groups/" + roleGroupId;
    }
    
    /* ADD ROLE */
    @RequestMapping(value="role-groups/{roleGroupId}/add-role", method=RequestMethod.POST)
    public String addRole(ModelMap model, FlashScope flash, int newRoleId, @PathVariable int roleGroupId,
            LiteYukonUser user) {
        
        LiteYukonGroup group = roleGroupDao.getLiteYukonGroup(roleGroupId);
        YukonRole role = YukonRole.getForId(newRoleId);
        
        /* Save the default role properties to the db for this group and role */
        try {
            rolePropertyEditorDao.addRoleToGroup(group, role);
            usersEventLogService.roleAdded(group.getGroupName(), role, user);
        } catch (ConfigurationException e) {
            model.addAttribute("roleGroupId", roleGroupId);
            flash.setError(new YukonMessageSourceResolvable(key + "roleConflictingWithUserGroup", 
                    role.name(), group.getGroupName()));
            return "redirect:/admin/role-groups/" + roleGroupId;
        }
        
        model.addAttribute("roleGroupId", roleGroupId);
        model.addAttribute("roleId", newRoleId);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "updateSuccessful"));
        
        return "redirect:/admin/role-groups/" + roleGroupId + "/roles/" + newRoleId;
    }
    
    private void setupModelMap(ModelMap model, LiteYukonGroup group) {
        
        int roleGroupId = group.getGroupID();
        model.addAttribute("group", group);
        model.addAttribute("roleGroupId", group.getGroupID());
        model.addAttribute("roleGroupName", group.getGroupName());
        model.addAttribute("editName", group.getGroupID() > -1 ? true : false);
        
        List<LiteUserGroup> userGroups = userGroupDao.getLiteUserGroupsByRoleGroupId(roleGroupId);
        model.addAttribute("userGroups", userGroups);
        model.addAttribute("alreadyAssignedUserGroupIds", Lists.transform(userGroups, LiteBase.ID_FUNCTION));
    }
    
}