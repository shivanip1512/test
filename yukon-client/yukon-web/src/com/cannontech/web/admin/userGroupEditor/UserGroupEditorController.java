package com.cannontech.web.admin.userGroupEditor;

import static com.cannontech.common.util.StringUtils.parseIntStringForList;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.util.StringUtils;
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
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.userGroupEditor.model.RoleAndGroup;
import com.cannontech.web.admin.userGroupEditor.model.UserGroupValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.security.csrf.CsrfTokenService;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Controller
@RequestMapping("/userGroup/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserGroupEditorController {

    @Autowired private RoleDao roleDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private UserGroupValidator userGroupValidator;
    @Autowired private CsrfTokenService csrfTokenService;
    
    /* User Editor View Page */
    @RequestMapping("home")
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        return "userGroupEditor/userGroupHome.jsp";
    }
    
    @RequestMapping("view")
    public String view(ModelMap model, int userGroupId) throws SQLException {
        
        UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
        model.addAttribute("userGroup", userGroup.getUserGroup());
        model.addAttribute("mode", PageEditMode.VIEW);
        setupModelMap(model, userGroup.getUserGroup());
        
        return "userGroupEditor/userGroup.jsp";
    }
    
    /* User Editor Edit Page */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(HttpServletRequest request, ModelMap model, int userGroupId) throws SQLException {
        
        UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
        model.addAttribute("userGroupId", userGroupId);
        model.addAttribute("userGroup", userGroup.getUserGroup());
        model.addAttribute("isUserGroupDeletable", userGroup.isUserGroupDeletable());
        model.addAttribute("mode", PageEditMode.EDIT);
        model.addAttribute("userGroupName",userGroup.getUserGroup().getUserGroupName());
        
        return "userGroupEditor/userGroup.jsp";
    }

    @RequestMapping("permissions")
    public String permissions(ModelMap model, int userGroupId, YukonUserContext userContext) throws ServletException {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, userContext.getYukonUser());
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
        model.addAttribute("userGroup", userGroup);
        model.addAttribute("userGroupId", userGroupId);
        model.addAttribute("userGroupName", userGroup.getUserGroupName());
        
        return "userGroupEditor/editGroup.jsp";
    }

    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="update")
    public String update(HttpServletRequest request, @ModelAttribute("userGroup") com.cannontech.database.db.user.UserGroup userGroup, BindingResult result, ModelMap model, FlashScope flash) throws SQLException {
        
        userGroupValidator.validate(userGroup, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModelMap(model, userGroup);
            return "userGroupEditor/userGroup.jsp";
        }
        
        userGroupDao.update(userGroup);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userGroupEditor.updateSuccessful"));
        model.addAttribute("userGroupId", userGroup.getLiteUserGroup().getUserGroupId());
        
        return "redirect:view";
    }
    
    /**
     * TODO Uncomment this when we implement the role group and user delete functionality.
     * 
    
    @RequestMapping(value="edit", method=RequestMethod.POST, params="delete")
     */
    public String delete(ModelMap model, FlashScope flash, int userGroupId) {
        
        int numberOfUsers = userGroupDao.getNumberOfUsers(userGroupId);
        if (numberOfUsers > 0) {
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userGroupEditor.usersCurrentlyAttached"));
            model.addAttribute("userGroupId", userGroupId);
            return "redirect:view";
        }
        
        userGroupDao.delete(userGroupId);
        
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userGroupEditor.deletedSuccessful", userGroup.getUserGroupName()));
        return "userGroupEditor/home.jsp";
    }
    
    /* User Group Associations */
    @RequestMapping("association")
    public String association(ModelMap model) {
        
        List<YukonRole> yukonRoles = Arrays.asList(YukonRole.values());
        model.addAttribute("yukonRoles", yukonRoles);
        
        return "userGroupEditor/userGroupAssociations.jsp";
    }
    
    /* Role Groups Page */
    @RequestMapping("roleGroups")
    public String roleGroups(ModelMap model, int userGroupId) {
        com.cannontech.database.db.user.UserGroup userGroup = userGroupDao.getDBUserGroup(userGroupId);
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
        setupModelMap(model, userGroup);
        return "userGroupEditor/groups.jsp";
    }
    
    @RequestMapping(value="addRoleGroups", method=RequestMethod.POST)
    public String addRoleGroups(HttpServletRequest request, ModelMap model, FlashScope flash, int userGroupId, String roleGroupIds) throws SQLException {
        List<Integer> roleGroupIdsList = parseIntStringForList(roleGroupIds);
        
        if (!roleGroupIdsList.isEmpty()) {
            UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
            Map<Integer, LiteYukonGroup> liteYukonGroupsMap = yukonGroupDao.getLiteYukonGroups(roleGroupIdsList);
            List<LiteYukonGroup> liteYukonGroups = Lists.newArrayList(liteYukonGroupsMap.values());
            List<String> conflictingRoleGroupNames = Lists.newArrayList();

            // Try adding the role groups to the user group.
            for (LiteYukonGroup liteYukonGroup : liteYukonGroups) {
                try {
                    userGroup.addRoleGroups(liteYukonGroup);
                } catch (ConfigurationException e) {
                    conflictingRoleGroupNames.add(liteYukonGroup.getGroupName());
                }
            }
            
            // Update the user group if we have no conflicts.
            if (conflictingRoleGroupNames.size() == 0) {
                userGroup.update();
            }
            
            // Display the response message.
            if (!conflictingRoleGroupNames.isEmpty()) {
                flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.groupConflict", conflictingRoleGroupNames));
            } else {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.updateSuccessful"));
            }
        }
        
        model.addAttribute("userGroupId", userGroupId);
        return "redirect:roleGroups";
    }
    
    @RequestMapping(value="removeRoleGroup", method=RequestMethod.POST)
    public String removeRoleGroup(HttpServletRequest request, ModelMap model, FlashScope flash, int userGroupId, int remove) {
        userGroupDao.deleteUserGroupToYukonGroupMappng(userGroupId, remove);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.updateSuccessful"));
        model.addAttribute("userGroupId", userGroupId);
        return "redirect:roleGroups";
    }
    
    /* Users Tab */
    @RequestMapping("users")
    public String users(ModelMap model, FlashScope flash, int userGroupId,
            @DefaultItemsPerPage(10) PagingParameters paging) {
        
        com.cannontech.database.db.user.UserGroup userGroup = userGroupDao.getDBUserGroup(userGroupId);
        SearchResults<LiteYukonUser> users = yukonUserDao.getUsersForUserGroup(userGroupId, paging.getStartIndex(), paging.getItemsPerPage());
        model.addAttribute("users", users);
        Map<Integer, UserAuthenticationInfo> userAuthenticationInfo =
                yukonUserDao.getUserAuthenticationInfo(Iterables.transform(users.getResultList(), LiteYukonUser.USER_ID_FUNCTION));
        model.addAttribute("userAuthenticationInfo", userAuthenticationInfo);

        List<Integer> alreadyAssignedUserIds = Lists.transform(users.getResultList(), LiteBase.ID_FUNCTION);
        model.addAttribute("alreadyAssignedUserIds", alreadyAssignedUserIds);
        setupModelMap(model, userGroup);
        
        return "userGroupEditor/users.jsp";
    }
    
    /* Remove User From User Group */
    @RequestMapping(value="removeUser", method=RequestMethod.POST, params="remove")
    public String removeUser(HttpServletRequest request, ModelMap model, FlashScope flash, int userGroupId, int remove) {
        yukonUserDao.removeUserFromUserGroup(remove);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.roleGroupEditor.updateSuccessful"));
        model.addAttribute("userGroupId", userGroupId);
        return "redirect:users";
    }
    
    /* Add User To User Group */
    @RequestMapping(value="addUsers", method=RequestMethod.POST)
    public String addUsers(HttpServletRequest request, ModelMap model, FlashScope flash, int userGroupId, String userIds) {
        List<Integer> userIdList = StringUtils.parseIntStringForList(userIds);
        for (Integer userId : userIdList) {
            yukonUserDao.updateUserGroupId(userId, userGroupId);
        }
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.roleGroupEditor.updateSuccessful"));
        model.addAttribute("userGroupId", userGroupId);
        return "redirect:users";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver("yukon.web.modules.adminSetup.userEditor.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
    private void setupModelMap(ModelMap model, com.cannontech.database.db.user.UserGroup userGroup) {
        model.addAttribute("userGroupId", userGroup.getUserGroupId());
        model.addAttribute("userGroupName", userGroup.getUserGroupName());
        
        Multimap<YukonRole, LiteYukonGroup> rolesAndGroups = 
                roleDao.getRolesAndRoleGroupsForUserGroup(userGroup.getUserGroupId());
        Multimap<YukonRoleCategory, RoleAndGroup> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
        model.addAttribute("roles", sortedRoles.asMap());
    }
}