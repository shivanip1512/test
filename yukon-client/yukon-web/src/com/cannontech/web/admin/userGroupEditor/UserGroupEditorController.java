package com.cannontech.web.admin.userGroupEditor;

import static com.cannontech.common.util.StringUtils.parseIntStringForList;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.naming.ConfigurationException;
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

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.userGroupEditor.model.UserGroupValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/userGroup/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserGroupEditorController {

    @Autowired private RoleDao roleDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private UserGroupValidator userGroupValidator;
    
    /* User Editor View Page */
    @RequestMapping
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        return "userGroupEditor/userGroupHome.jsp";
    }

    @RequestMapping
    public String view(ModelMap model, int userGroupId) throws SQLException {
        UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
        
        model.addAttribute("userGroup", userGroup);
        model.addAttribute("mode", PageEditMode.VIEW);
        
        setupModelMap(model, userGroup.getLiteUserGroup());
        return "userGroupEditor/userGroup.jsp";
    }
    
    /* User Editor Edit Page */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(ModelMap model, int userGroupId) throws SQLException {
        UserGroup userGroup = userGroupDao.getUserGroup(userGroupId);
        
        model.addAttribute("userGroupId", userGroupId);
        model.addAttribute("userGroup", userGroup);
        model.addAttribute("isUserGroupDeletable", userGroup.isUserGroupDeletable());
        model.addAttribute("mode", PageEditMode.EDIT);
        
        return "userGroupEditor/userGroup.jsp";
    }

    /* Cancel Edit */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="cancel")
    public String cancel(ModelMap model, int userGroupId) {
        model.addAttribute("userGroupId", userGroupId);
        return "redirect:view";
    }

    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="update")
    public String update(HttpServletRequest request, @ModelAttribute("userGroup") UserGroup userGroup, BindingResult result, ModelMap model, FlashScope flash) throws SQLException {
        
        userGroupValidator.validate(userGroup, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModelMap(model, userGroup.getLiteUserGroup());
            return "userGroupEditor/userGroup.jsp";
        }
        
        userGroupDao.update(userGroup.getLiteUserGroup());
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userGroupEditor.updateSuccessful"));
        setupModelMap(model, userGroup.getLiteUserGroup());
        
        return "redirect:view";
    }

    /* User Group Associations */
    @RequestMapping
    public String association(ModelMap model) {
        
        List<YukonRole> yukonRoles = Arrays.asList(YukonRole.values());
        model.addAttribute("yukonRoles", yukonRoles);
        
        return "userGroupEditor/userGroupAssociations.jsp";
    }
    
//    public String associations(ModelMap model, YukonRole yukonRole) {
//        TreeMultimap<LiteYukonGroup, LiteUserGroup> userGroupRoleAssociations = userGroupService.getAssociations(yukonRole);
//        
//        return "userGroupAssInsert.jsp";
//    }
    
    /* Role Groups Page */
    @RequestMapping
    public String roleGroups(ModelMap model, int userGroupId) {
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
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
    
    @RequestMapping(method=RequestMethod.POST)
    public String addRoleGroups(ModelMap model, FlashScope flash, int userGroupId, String roleGroupIds) throws SQLException {
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

    @RequestMapping(method=RequestMethod.POST)
    public String removeRoleGroup(ModelMap model, FlashScope flash, int userGroupId, int remove) {
        userGroupDao.deleteUserGroupToYukonGroupMappng(userGroupId, remove);
        model.addAttribute("userGroupId", userGroupId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.updateSuccessful"));
        return "redirect:roleGroups";
    }
    
    /* Users Tab */
    @RequestMapping("users")
    public String users(ModelMap model, FlashScope flash, int userGroupId, Integer itemsPerPage, Integer page) {
        
        if(page == null){
            page = 1;
        }
        if(itemsPerPage == null){
            itemsPerPage = 25;
        }
        
        int startIndex = (page - 1) * itemsPerPage;
        
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
        SearchResult<LiteYukonUser> searchResult = yukonUserDao.getUsersForUserGroup(userGroupId, startIndex, itemsPerPage);
        model.addAttribute("searchResult", searchResult);
        List<LiteYukonUser> users = searchResult.getResultList();
        model.addAttribute("users", users);
        
        List<Integer> alreadyAssignedUserIds = 
            Lists.transform(users, new Function<LiteYukonUser, Integer>() {
                @Override
                public Integer apply(LiteYukonUser user) {
                    return user.getUserID();
                }
            });
        model.addAttribute("alreadyAssignedUserIds", alreadyAssignedUserIds);
        setupModelMap(model, userGroup);
        return "userGroupEditor/users.jsp";
    }
    
    /* Remove User From User Group */
    @RequestMapping(value="removeUser", method=RequestMethod.POST, params="remove")
    public String removeUser(ModelMap model, FlashScope flash, int userGroupId, int remove) {
        yukonUserDao.removeUserFromUserGroup(remove);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.roleGroupEditor.updateSuccessful"));
        model.addAttribute("userGroupId", userGroupId);
        return "redirect:users";
    }
    
    /* Add User To User Group */
    @RequestMapping(value="addUsers", method=RequestMethod.POST)
    public String addUsers(ModelMap model, FlashScope flash, int userGroupId, String userIds) {
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
    
    private void setupModelMap(ModelMap model, LiteUserGroup liteUserGroup) {
        model.addAttribute("userGroupId", liteUserGroup.getUserGroupId());
        model.addAttribute("userGroupName", liteUserGroup.getUserGroupName());
        
        Map<YukonRole, LiteYukonGroup> rolesAndGroups = roleDao.getRolesAndRoleGroupsForUserGroup(liteUserGroup.getUserGroupId());
        ImmutableMultimap<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
        model.addAttribute("roles", sortedRoles.asMap());
    }
}