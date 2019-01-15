package com.cannontech.web.admin.userGroupEditor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.user.Password;
import com.cannontech.common.user.User;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.AuthenticationThrottleDto;
import com.cannontech.core.authentication.model.PasswordPolicy;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.authentication.service.PasswordPolicyService;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.userGroupEditor.model.RoleAndGroup;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.service.PasswordResetService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserEditorController {
    
    @Autowired private AuthenticationService authService;
    @Autowired private RoleDao roleDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private UserValidator userValidator;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private UsersEventLogService usersEventLogService; 
    
    private final static String key = "yukon.web.modules.adminSetup.auth.user.";
    
    /* VIEW PAGE */
    @RequestMapping("users/{userId}")
    public String view(YukonUserContext userContext, ModelMap model, @PathVariable int userId) {
        boolean isLoggedInUser = false;
        LiteYukonUser lyu = yukonUserDao.getLiteYukonUser(userId);
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(userId);
        User user = new User(lyu, userAuthenticationInfo);
        setupModelMap(model, user, PageEditMode.VIEW, userContext);
        if (userContext.getYukonUser().getLiteID() == userId) {
            isLoggedInUser = true;
        }
        model.addAttribute("isLoggedInUser", isLoggedInUser);
        LiteYukonUser me = userContext.getYukonUser();
        boolean showPermissions = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, me);
        model.addAttribute("showPermissions", showPermissions);
        AuthenticationThrottleDto throttling = authService.getAuthenticationThrottleData(user.getUsername());
        model.addAttribute("throttling", throttling);
        
        return "userGroupEditor/user.jsp";
    }
    
    /* EDIT PAGE */
    @RequestMapping("users/{userId}/edit")
    public String edit(YukonUserContext userContext, ModelMap model, @PathVariable int userId) {
        
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(userId);
        User user = new User(yukonUserDao.getLiteYukonUser(userId), userAuthenticationInfo);
        setupModelMap(model, user, PageEditMode.EDIT, userContext);
        model.addAttribute("companies", ecDao.getAllEnergyCompanies());
        
        return "userGroupEditor/user.jsp";
    }
    
    @RequestMapping("users/{userId}/remove-login-wait")
    public void reset(HttpServletResponse resp, @PathVariable int userId, YukonUserContext userContext) {
        
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        authService.removeAuthenticationThrottle(user.getUsername());
        resp.setStatus(HttpStatus.NO_CONTENT.value());
        
    }
    
    /* Unlock User */
    @RequestMapping("users/{userId}/unlock")
    public String unlock(ModelMap model, FlashScope flash, @PathVariable int userId, LiteYukonUser loggedUser) {
        
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        authService.removeAuthenticationThrottle(user.getUsername());
        usersEventLogService.userUnlocked(user.getUsername(), loggedUser);
        flash.setConfirm(new YukonMessageSourceResolvable(key + "userUnlocked"));
        
        return redirectToView(model, userId);
    }
    
    /* Update User */
    @RequestMapping(value="users/{userId}", method=RequestMethod.POST, params="update")
    public String update(YukonUserContext userContext, 
            @ModelAttribute User user, BindingResult result, ModelMap model, FlashScope flash) {

        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(user.getUserId());
        int userGroupId = yukonUser.getUserGroupId();
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserId());
        user.updateForSave(yukonUser, userAuthenticationInfo);
        boolean requiresPasswordChanged = user.isAuthenticationChanged()
                && authService.supportsPasswordSet(user.getAuthCategory());
        if (requiresPasswordChanged) {
            PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(yukonUser);
            String generatedPassword = "";
            generatedPassword = passwordPolicy.generatePassword();
            user.getPassword().setConfirmPassword(generatedPassword);
            user.getPassword().setPassword(generatedPassword);
            new PasswordValidator(yukonUser, "password.password", "password.confirmPassword")
            .validate(user.getPassword(), result);
            if (result.hasErrors()) {
                model.addAttribute("hasPasswordError", true);
            }
        }
        userValidator.validate(user, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            setupModelMap(model, user, PageEditMode.EDIT, userContext);
            return "userGroupEditor/user.jsp";
        }
        String userGroupName = userGroupDao.getLiteUserGroup(userGroupId).getUserGroupName();
        String ecName = ecDao.getEnergyCompany(user.getEnergyCompanyId()).getName();
        yukonUserDao.save(yukonUser);
        usersEventLogService.userUpdated(user.getUsername(), userGroupName, ecName, user.getLoginStatus(), userContext.getYukonUser());
        if (userGroupId != yukonUser.getUserGroupId()) {
            usersEventLogService.userRemoved(user.getUsername(), userGroupName, userContext.getYukonUser());
            if (yukonUser.getUserGroupId() != null) {
                LiteUserGroup addedToUserGroup = userGroupDao.getLiteUserGroup(yukonUser.getUserGroupId());
                usersEventLogService.userAdded(user.getUsername(), addedToUserGroup.getUserGroupName(), userContext.getYukonUser());
            }
        }
        
        
        boolean ecMappingExists = ecDao.isEnergyCompanyOperator(yukonUser);
        if (user.getEnergyCompanyId() != null) {
            if (ecMappingExists) {
                ecMappingDao.updateEnergyCompanyOperatorLoginListMapping(user.getUserId(), user.getEnergyCompanyId());
            } else {
                ecMappingDao.addEnergyCompanyOperatorLoginListMapping(user.getUserId(), user.getEnergyCompanyId());
            }
        } else {
            if (ecMappingExists) {
                EnergyCompany existingEC = ecDao.getEnergyCompanyByOperator(yukonUser);
                ecMappingDao.deleteEnergyCompanyOperatorLoginListMapping(user.getUserId(), existingEC.getId());
            }
        }
        
        
        if (requiresPasswordChanged) {
            authService.setPassword(yukonUser, user.getAuthCategory(), user.getPassword().getPassword());
        } else if (user.isAuthenticationChanged()) {
            authService.setAuthenticationCategory(yukonUser, user.getAuthCategory());
        }
        
        flash.setConfirm(new YukonMessageSourceResolvable(key + "updateSuccessful"));
        
        return redirectToView(model, user.getUserId());
    }
    
    /* Delete User */
    @RequestMapping(value="users/{userId}", method=RequestMethod.POST, params="delete")
    public String delete(ModelMap model, @ModelAttribute User user, FlashScope flash, YukonUserContext userContext) {
        
        if (userContext.getYukonUser().getUserID() == user.getUserId()) {
            flash.setError(new YukonMessageSourceResolvable(key + "delete.failed.self"));
            return redirectToView(model, user.getUserId());
        }
        
        yukonUserDao.deleteUser(user.getUserId());
        usersEventLogService.userDeleted(user.getUsername(), userContext.getYukonUser());
        flash.setConfirm(new YukonMessageSourceResolvable(key + "delete.success", user.getUsername()));
        
        return "redirect:/admin/users-groups/home";
    }
    
    @RequestMapping(value = "users/{userId}/change-password", method = RequestMethod.POST)
    public String changePassword(HttpServletResponse resp, ModelMap model, YukonUserContext userContext,
            FlashScope flash, @PathVariable int userId, @ModelAttribute Password password, BindingResult result) {
        
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(userId);
        boolean isOldPasswordRequired = true;
        if (userContext.getYukonUser().getUserID() != userId) {
            isOldPasswordRequired = false;
        }
        if (isOldPasswordRequired) {
            boolean isValidPassword =
                authService.validateOldPassword(yukonUser.getUsername(), password.getOldPassword());
            if (!isValidPassword) {
                flash.setMessage(new YukonMessageSourceResolvable(key + "incorrectPassword"),
                    FlashScopeMessageType.ERROR);
                return null;
            }

        }

        PasswordValidator validator = new PasswordValidator(yukonUser, "password", "confirmPassword");
        validator.validate(password, result);
        
        if (result.hasErrors()) {

            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("passwordPolicy", passwordPolicyService.getPasswordPolicy(yukonUser));
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            
            return "userGroupEditor/userChangePasswordPopup.jsp";
        }
        
        authService.setPassword(yukonUser, password.getPassword());
        if (!isOldPasswordRequired) {
            authService.setForceResetForUser(yukonUser, YNBoolean.YES);
        }
        flash.setConfirm(new YukonMessageSourceResolvable(key + "passwordUpdateSuccessful"));
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
        return null;
    }
    
    private String redirectToView(ModelMap model, int userId) {
        model.clear();
        return "redirect:/admin/users/" + userId + "";
    }

    @RequestMapping(value = "users/{userId}/change-password", method = RequestMethod.GET)
    public String changePassword(ModelMap model, @PathVariable int userId, LiteYukonUser user) {
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(userId);
        if (user.getUserID() != userId) {
            model.addAttribute("otherUser", true);
        }

        model.addAttribute("minPasswordAgeNotMet", !passwordPolicyService.isMinPasswordAgeMet(yukonUser, null));
        Password password = new Password();
        model.addAttribute("password", password);
        model.addAttribute("passwordPolicy", passwordPolicyService.getPasswordPolicy(yukonUser));
        model.addAttribute("userId", yukonUser.getUserID());
        return "userGroupEditor/userChangePasswordPopup.jsp";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(key + "");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
    private void setupModelMap(ModelMap model, User user, PageEditMode mode, YukonUserContext userContext) {
        
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(new LiteYukonUser(user.getUserId()));
        model.addAttribute("passwordPolicy", passwordPolicy);
        model.addAttribute("changePwdKey", passwordResetService.getPasswordKey(userContext.getYukonUser()));
        model.addAttribute("user", user);
        model.addAttribute("password", new Password());
        model.addAttribute("mode", mode);
        
        model.addAttribute("currentUserId", userContext.getYukonUser().getLiteID());
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("editNameAndStatus", user.getUserId() > -1);
        
        model.addAttribute("username", yukonUserDao.getLiteYukonUser(user.getUserId()).getUsername());
        AuthenticationCategory[] categories = AuthenticationCategory.values();
        model.addAttribute("authenticationCategories", categories);
        Map<AuthenticationCategory, Boolean> passwordSettable = Maps.newHashMap();
        for (AuthenticationCategory category : categories) {
            passwordSettable.put(category, authService.supportsPasswordSet(category));
        }
        model.addAttribute("supportsPasswordSet", passwordSettable);
        model.addAttribute("loginStatusTypes", LoginStatusEnum.values());
        model.addAttribute("userGroups", userGroupDao.getAllLiteUserGroups());
        
        if (user.getUserGroupId() != null) {
            LiteUserGroup liteUserGroup = userGroupDao.getLiteUserGroup(user.getUserGroupId());
            model.addAttribute("userGroupName", liteUserGroup.getUserGroupName());
        }
        Multimap<YukonRole, LiteYukonGroup> rolesAndGroups = roleDao.getRolesAndGroupsForUser(user.getUserId());
        Multimap<YukonRoleCategory, RoleAndGroup> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
        model.addAttribute("roles", sortedRoles.asMap());

        boolean ecMappingExists = ecDao.isEnergyCompanyOperator(new LiteYukonUser(user.getUserId()));
        if (ecMappingExists) {
            EnergyCompany ec = ecDao.getEnergyCompanyByOperator(new LiteYukonUser(user.getUserId()));
            user.setEnergyCompanyId(ec.getId());
            model.addAttribute("energyCompanyName", ec.getName());
        } else {
            model.addAttribute("energyCompanyName", "None");
        }
    }
    
    private class PasswordValidator extends SimpleValidator<Password> {
        
        private List<MessageSourceResolvable> messages = Lists.newArrayList();
        private LiteYukonUser yukonUser;
        private String passwordKey;
        private String confirmPasswordKey;
        
        PasswordValidator(LiteYukonUser yukonUser, String passwordKey, String confirmPasswordKey) {
            super(Password.class);
            this.yukonUser = yukonUser;
            this.passwordKey = passwordKey;
            this.confirmPasswordKey = confirmPasswordKey;
        }
        
        @Override
        protected void doValidation(Password target, Errors errors) {
            
            YukonValidationUtils.checkExceedsMaxLength(errors, passwordKey, target.getPassword(), 64);
            YukonValidationUtils.checkExceedsMaxLength(errors, confirmPasswordKey, target.getConfirmPassword(), 64);
            
            if (!target.getPassword().equals(target.getConfirmPassword())) {
                YukonValidationUtils.rejectValues(errors, "password.mismatch", passwordKey, confirmPasswordKey);
                messages.add(new YukonMessageSourceResolvable(key + "password.mismatch"));
                return;
            }
            
            // Check the password against the password policy.
            String password = target.getPassword();
            PasswordPolicyError passwordPolicyError =
                    passwordPolicyService.checkPasswordPolicy(password, yukonUser);
            Object[] errorArgs = {};
            
            if (passwordPolicyError == PasswordPolicyError.PASSWORD_DOES_NOT_MEET_POLICY_QUALITY) {
                
                PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(yukonUser);
                errorArgs = new Object[] { passwordPolicy.numberOfRulesMet(password),
                        passwordPolicy.getPasswordQualityCheck() };
                
            } else if (passwordPolicyError == PasswordPolicyError.INVALID_PASSWORD_LENGTH) {
                
                PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(yukonUser);
                errorArgs = new Object[] { passwordPolicy.getMinPasswordLength() };
                
            } else if (passwordPolicyError == PasswordPolicyError.PASSWORD_USED_TOO_RECENTLY) {
                
                PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(yukonUser);
                errorArgs = new Object[] { passwordPolicy.getPasswordHistory() };
            }
            
            if (passwordPolicyError != null) {
                messages.add(new YukonMessageSourceResolvable(passwordPolicyError.getFormatKey(), errorArgs ));
                YukonValidationUtils.rejectValues(errors, passwordPolicyError.getFormatKey(), errorArgs,
                                                  passwordKey, confirmPasswordKey);
            }
        }
        
        List<MessageSourceResolvable> getMessages() {
            return messages;
        }
        
    }
    
}