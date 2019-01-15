package com.cannontech.web.admin.userGroupEditor;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.userGroupEditor.model.RoleAndGroup;
import com.cannontech.web.admin.userGroupEditor.model.UserGroupValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserGroupEditorController {

    @Autowired private RoleDao roleDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private UserGroupValidator userGroupValidator;
    @Autowired private UsersEventLogService usersEventLogService;
    
    private static final String groupKey = "yukon.web.modules.adminSetup.auth.user.group.";
    private static final String userKey = "yukon.web.modules.adminSetup.auth.user.";
    private static final String roleKey = "yukon.web.modules.adminSetup.auth.role.group.";
    
    /* VIEW PAGE */
    @RequestMapping("user-groups/{userGroupId}")
    public String view(ModelMap model, @PathVariable int userGroupId, LiteYukonUser user,
            @DefaultItemsPerPage(10) PagingParameters paging) {
        
        UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
        model.addAttribute("userGroup", userGroup.getUserGroup());
        model.addAttribute("mode", PageEditMode.VIEW);
        setupModelMap(model, userGroup.getUserGroup(), user, paging);
        
        return "userGroupEditor/userGroup.jsp";
    }
    
    /* EDIT PAGE */
    @RequestMapping("user-groups/{userGroupId}/edit")
    public String edit(ModelMap model, @PathVariable int userGroupId) {
        
        UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
        model.addAttribute("userGroupId", userGroupId);
        model.addAttribute("userGroup", userGroup.getUserGroup());
        model.addAttribute("isUserGroupDeletable", userGroup.isUserGroupDeletable());
        model.addAttribute("mode", PageEditMode.EDIT);
        model.addAttribute("userGroupName", userGroup.getUserGroup().getUserGroupName());
        
        return "userGroupEditor/userGroup.jsp";
    }
    
    /* UPDATE */
    @RequestMapping(value="user-groups/{userGroupId}", method=RequestMethod.POST, params="update")
    public String update(@ModelAttribute("userGroup") com.cannontech.database.db.user.UserGroup userGroup, 
            BindingResult result, ModelMap model, FlashScope flash, LiteYukonUser user,
            @DefaultItemsPerPage(10) PagingParameters paging) {

        userGroupValidator.validate(userGroup, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModelMap(model, userGroup, user, paging);
            return "userGroupEditor/userGroup.jsp";
        }
        
        userGroupDao.update(userGroup);
        usersEventLogService.userGroupUpdated(userGroup.getUserGroupName(), user);
        flash.setConfirm(new YukonMessageSourceResolvable(groupKey + "updateSuccessful"));
        int userGroupId = userGroup.getLiteUserGroup().getUserGroupId();
        model.addAttribute("userGroupId", userGroupId);
        
        return "redirect:/admin/user-groups/" + userGroupId;
    }
    
    /* DELETE */
    @RequestMapping(value="user-groups/{userGroupId}", method=RequestMethod.POST, params="delete")
    public String delete(ModelMap model, FlashScope flash, @PathVariable int userGroupId, LiteYukonUser user) {
        
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
        
        int numberOfUsers = userGroupDao.getNumberOfUsers(userGroupId);
        if (numberOfUsers > 0) {
            flash.setError(new YukonMessageSourceResolvable(groupKey + "usersCurrentlyAttached"));
            model.addAttribute("userGroupId", userGroupId);
            return "redirect:/admin/user-groups/" + userGroupId;
        }
        
        userGroupDao.delete(userGroupId);
        usersEventLogService.userGroupDeleted(userGroup.getUserGroupName(), user);
        flash.setConfirm(new YukonMessageSourceResolvable(groupKey + "deletedSuccessful", userGroup.getUserGroupName()));
        
        return "userGroupEditor/home.jsp";
    }
    
    /* USERS */
    @RequestMapping("user-groups/{userGroupId}/users")
    public String users(ModelMap model, @PathVariable int userGroupId, @DefaultItemsPerPage(10) PagingParameters paging) {
        
        SearchResults<LiteYukonUser> users = yukonUserDao.getUsersForUserGroup(userGroupId, paging);
        model.addAttribute("users", users);
        
        List<LiteYukonUser> resultList = users.getResultList();
        Iterable<Integer> pageIds = Iterables.transform(resultList, LiteYukonUser.USER_ID_FUNCTION);
        Map<Integer, UserAuthenticationInfo> authInfo = yukonUserDao.getUserAuthenticationInfo(pageIds);
        model.addAttribute("authInfo", authInfo);
        
        return "userGroupEditor/users.jsp";
    }
    
    /* ADD USERS */
    @RequestMapping(value="user-groups/{userGroupId}/add-users", method=RequestMethod.POST)
    public void addUsers(ModelMap model, FlashScope flash, HttpServletResponse resp, 
            @PathVariable int userGroupId, @RequestParam("users[]") int[] users,
            LiteYukonUser user) {
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
        for (int userId : users) {
            yukonUserDao.updateUserGroupId(userId, userGroupId);
            LiteYukonUser addedUser = yukonUserDao.getLiteYukonUser(userId);
            usersEventLogService.userAdded(addedUser.getUsername(), userGroup.getUserGroupName(), user);
        }
        
        flash.setConfirm(new YukonMessageSourceResolvable(roleKey + "updateSuccessful"));
        model.addAttribute("userGroupId", userGroupId);
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    /* REMOVE USER */
    @RequestMapping(value="user-groups/{userGroupId}/remove-user", method=RequestMethod.POST, params="remove")
    public String removeUser(ModelMap model, FlashScope flash, @PathVariable int userGroupId, int remove,
            LiteYukonUser user) {
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
        LiteYukonUser removedUser = yukonUserDao.getLiteYukonUser(remove);
        yukonUserDao.removeUserFromUserGroup(remove);
        usersEventLogService.userRemoved(removedUser.getUsername(), userGroup.getUserGroupName(), user);
        flash.setConfirm(new YukonMessageSourceResolvable(roleKey + "updateSuccessful"));
        
        return "redirect:/admin/user-groups/" + userGroupId;
    }
    
    /* ADD ROLE GROUPS */
    @RequestMapping(value="user-groups/{userGroupId}/add-role-groups", method=RequestMethod.POST)
    public void addRoleGroups(ModelMap model, FlashScope flash, 
            @PathVariable int userGroupId, @RequestParam("groups[]") int[] groups,
            LiteYukonUser user)
    throws SQLException {
        
        UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
        Map<Integer, LiteYukonGroup> liteYukonGroupsMap = yukonGroupDao.getLiteYukonGroups(Ints.asList(groups));
        List<LiteYukonGroup> liteYukonGroups = Lists.newArrayList(liteYukonGroupsMap.values());
        List<String> conflictingRoleGroupNames = Lists.newArrayList();
        
        // Try adding the role groups to the user group.
        for (LiteYukonGroup liteYukonGroup : liteYukonGroups) {
            try {
                userGroup.addRoleGroups(liteYukonGroup);
                usersEventLogService.roleGroupAdded(userGroup.getUserGroup().getUserGroupName(),
                    liteYukonGroup.getGroupName(), user);
            } catch (ConfigurationException e) {
                conflictingRoleGroupNames.add(liteYukonGroup.getGroupName());
            }
        }
        
        if (!conflictingRoleGroupNames.isEmpty()) {
            flash.setError(new YukonMessageSourceResolvable(userKey + "groupConflict", conflictingRoleGroupNames));
        } else {
            // Update the user group if we have no conflicts.
            userGroup.update();
            flash.setConfirm(new YukonMessageSourceResolvable(groupKey + "updateSuccessful"));
        }
    }
    
    /* REMOVE ROLE GROUP */
    @RequestMapping(value="user-groups/{userGroupId}/remove-role-group", method=RequestMethod.POST)
    public String removeRoleGroup(ModelMap model, FlashScope flash, @PathVariable int userGroupId, int remove,
            LiteYukonUser user) {
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
        LiteYukonGroup roleGroup = yukonGroupDao.getLiteYukonGroup(remove);
        userGroupDao.deleteUserGroupToYukonGroupMappng(userGroupId, remove);
        usersEventLogService.roleGroupRemoved(userGroup.getUserGroupName(), roleGroup.getGroupName(), user);
        flash.setConfirm(new YukonMessageSourceResolvable(groupKey + "updateSuccessful"));
        model.addAttribute("userGroupId", userGroupId);
        
        return "redirect:/admin/user-groups/" + userGroupId;
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(userKey);
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
    private void setupModelMap(ModelMap model, com.cannontech.database.db.user.UserGroup userGroup,
            LiteYukonUser user, PagingParameters paging) {
        
        Integer userGroupId = userGroup.getUserGroupId();
        model.addAttribute("userGroupId", userGroupId);
        model.addAttribute("userGroupName", userGroup.getUserGroupName());
        
        Multimap<YukonRole, LiteYukonGroup> rolesAndGroups = 
                roleDao.getRolesAndRoleGroupsForUserGroup(userGroupId);
        Multimap<YukonRoleCategory, RoleAndGroup> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
        model.addAttribute("roles", sortedRoles.asMap());
        
        boolean showPermissions = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, user);
        model.addAttribute("showPermissions", showPermissions);
        
        // Users
        List<Integer> userIds = yukonUserDao.getUserIdsForUserGroup(userGroupId);
        model.addAttribute("alreadyAssignedUserIds", userIds);
        
        SearchResults<LiteYukonUser> users = yukonUserDao.getUsersForUserGroup(userGroupId, paging);
        model.addAttribute("users", users);
        
        List<LiteYukonUser> resultList = users.getResultList();
        Iterable<Integer> pageIds = Iterables.transform(resultList, LiteYukonUser.USER_ID_FUNCTION);
        Map<Integer, UserAuthenticationInfo> authInfo = yukonUserDao.getUserAuthenticationInfo(pageIds);
        model.addAttribute("authInfo", authInfo);
        
        // Role Groups
        List<LiteYukonGroup> roleGroups = yukonGroupDao.getRoleGroupsForUserGroupId(userGroupId);
        List<Integer> alreadyAssignedRoleGroupIds = 
                Lists.transform(roleGroups, new Function<LiteYukonGroup, Integer>() {
                    @Override
                    public Integer apply(LiteYukonGroup roleGroup) {
                        return roleGroup.getGroupID();
                    }
                });
        model.addAttribute("groups", roleGroups);
        model.addAttribute("alreadyAssignedRoleGroupIds", alreadyAssignedRoleGroupIds);
    }
    
}