package com.cannontech.web.user;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.UserGroup;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.userGroupEditor.RoleListHelper;
import com.cannontech.web.admin.userGroupEditor.model.RoleAndGroup;
import com.cannontech.web.common.ContactDto;
import com.cannontech.web.stars.dr.operator.service.OperatorAccountService;
import com.cannontech.web.stars.service.PasswordResetService;
import com.cannontech.web.user.model.UserProfile;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

@Component
public class UserProfileHelper {

    @Autowired private AuthenticationService authenticationService;
    @Autowired private ContactDao contactDao;
    @Autowired private EventLogDao eventLogDao;
    @Autowired private OperatorAccountService operatorAccountService;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private RoleDao roleDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private YukonUserDao yukonUserDao;

    private static final int passwordExpireDaysToWarn = 30;

    /**
     * Should be called early on in many methods.
     */
    public void isUserAuthorized(LiteYukonUser user, int userId) throws NotAuthorizedException {
        if (user.getUserID() != userId) {
            throw new NotAuthorizedException("User "+ user.getUsername() +" is not authorized to edit user with id ["+ userId +"]");
        }
    }

    public void setupActivityStream(ModelMap model, LiteYukonUser user, int firstRowIndex, int rowsToReturn) {
        SearchResults<EventLog> searchResults =
                eventLogDao.findEventsByStringAndPaginate(user.getUsername(), firstRowIndex, rowsToReturn);
        model.addAttribute("userEvents", searchResults);
    }

    public void setupPasswordData(ModelMap model, LiteYukonUser user) {
        PasswordPolicy pp = passwordPolicyService.getPasswordPolicy(user);
        model.addAttribute("passwordPolicy", pp);
        model.addAttribute("passwordWarning",
                           authenticationService.doesPasswordExpireInDays(user, passwordExpireDaysToWarn));
        model.addAttribute("userGroupName", userGroupDao.getLiteUserGroup(user.getUserGroupId()).getUserGroupName());
        model.addAttribute("changePwdKey", passwordResetService.getPasswordKey(user));
    
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserID());

        model.addAttribute("passwordLastChangeTimestamp", userAuthenticationInfo.getLastChangedDate());
        AuthenticationCategory authCat = userAuthenticationInfo.getAuthenticationCategory();
        model.addAttribute("canResetPassword", authenticationService.supportsPasswordSet(authCat));
    }


    /**
     * Used by {@link #ProfileController.profile} and {@link #ProfileController.edit}
     * 
     * @param model
     * @param context
     * @return LiteYukonUser
     * 
     * @postcondition model["userProfile"] = UserProfile object
     * @postcondition model["notificationTypes"] = ContactNotificationType[]
     */
    public LiteYukonUser setupUserAndNotifications(ModelMap model, LiteYukonUser user, YukonUserContext context) {

        List<LiteContact> contacts = contactDao.getContactsByLoginId(user.getUserID());
        ContactDto contact = null;
        List<LiteContactNotification> nots = null;
        if(!contacts.isEmpty()) {
            contact = operatorAccountService.getContactDto(contacts.get(0).getContactID(), false, false, true, context);
        }
        model.addAttribute("userProfile", new UserProfile(user, contact, nots));
        model.addAttribute("notificationTypes", ContactNotificationType.values());
        
        return user;
    }


    public void setupRoleGroups(ModelMap model, int userGroupId) {
        // COPIED FROM {@link RoleGroupEditorController.view()}, needed or the Roles list won't display
        model.addAttribute("roleGroupId", userGroupId);
        Set<YukonRole> roles = roleDao.getRolesForGroup(userGroupId);
        RoleListHelper.addRolesToModel(roles, model);

        // START roleGroups()
        UserGroup userGroup = userGroupDao.getDBUserGroup(userGroupId);
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
    }

    public void setupModelMap(ModelMap model, UserGroup userGroup) {
        model.addAttribute("userGroupId", userGroup.getUserGroupId());
        model.addAttribute("userGroupName", userGroup.getUserGroupName());
        
        Multimap<YukonRole, LiteYukonGroup> rolesAndGroups = 
                roleDao.getRolesAndRoleGroupsForUserGroup(userGroup.getUserGroupId());
        Multimap<YukonRoleCategory, RoleAndGroup> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
        model.addAttribute("categoryRoleMap", sortedRoles.asMap());
    }

}
