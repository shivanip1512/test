package com.cannontech.web.admin.userGroupEditor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.Pair;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
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
import com.cannontech.core.service.YukonUserService;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.userGroupEditor.model.Password;
import com.cannontech.web.admin.userGroupEditor.model.User;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/user/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserEditorController {
    @Autowired private AuthenticationService authenticationService;
    @Autowired private RoleDao roleDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonUserService yukonUserService;
    @Autowired private PasswordPolicyService passwordPolicyService;

    private UserValidator userValidator = new UserValidator();

    /* Group Editor View Page */
    @RequestMapping
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        return "userGroupEditor/userHome.jsp";
    }
    
    @RequestMapping
    public String view(ModelMap model, int userId) {
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(userId);
        User user = new User(yukonUser);

        if (yukonUser.getUserGroupId() != null) {
            LiteUserGroup liteUserGroup = userGroupDao.getLiteUserGroup(yukonUser.getUserGroupId());
            model.addAttribute("userGroupName", liteUserGroup.getUserGroupName());
        }

        setupModelMap(model, user, PageEditMode.VIEW);
        return "userGroupEditor/user.jsp";
    }
    
    /* User Editor Edit Page */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(ModelMap model, int userId) {
        User user = new User(yukonUserDao.getLiteYukonUser(userId));
        setupModelMap(model, user, PageEditMode.EDIT);
        return "userGroupEditor/user.jsp";
    }

    /* Unlock User */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="unlockUser")
    public String unlock(ModelMap model, FlashScope flash, int userId) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        authenticationService.removeAuthenticationThrottle(user.getUsername());
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.loginUnlocked"));
        return redirectToView(model, userId);
    }

    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="update")
    public String update(@ModelAttribute User user, BindingResult result, ModelMap model, FlashScope flash) {
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(user.getUserId());
        user.updateForSave(yukonUser);
        userValidator.validate(user, result);
        boolean requiresPasswordChanged = user.isAuthenticationChanged()
                && authenticationService.supportsPasswordSet(yukonUser.getAuthType());
        if (requiresPasswordChanged) {
            new PasswordValidator(yukonUser).validate(user.getPassword(), result);
        }

        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            setupModelMap(model, user, PageEditMode.EDIT);
            return "userGroupEditor/user.jsp";
        }

        if (requiresPasswordChanged) {
            yukonUserService.saveAndSetPassword(yukonUser, user.getPassword().getPassword());
        } else {
            yukonUserDao.save(yukonUser);
        }

        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.updateSuccessful"));

        return redirectToView(model, user.getUserId());
    }

    @RequestMapping(method=RequestMethod.POST)
    public String changePassword(ModelMap model, FlashScope flash, int userId, 
                                 @ModelAttribute Password password, BindingResult result) {
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(userId);
        new PasswordValidator(yukonUser).validate(password, result);
        User user = new User(yukonUser);

        if (result.hasErrors()) {
            model.addAttribute("showChangePasswordErrors", true);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("showChangePwPopup", true);
            setupModelMap(model, user, PageEditMode.VIEW);
            model.addAttribute("pwChangeFormMode", PageEditMode.EDIT);
            return "userGroupEditor/user.jsp";
        }
        
        authenticationService.setPassword(yukonUser, password.getPassword());
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.passwordUpdateSuccessful"));
        return redirectToView(model, userId);
    }

    private String redirectToView(ModelMap model, int userId) {
        model.clear();
        model.addAttribute("userId", userId);
        return "redirect:view";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver("yukon.web.modules.adminSetup.userEditor.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
    private void setupModelMap(ModelMap model, User user, PageEditMode mode) {
        model.addAttribute("user", user);
        model.addAttribute("password", new Password());
        model.addAttribute("mode", mode);

        model.addAttribute("userId", user.getUserId());
        model.addAttribute("editNameAndStatus", user.getUserId() > -1);

        model.addAttribute("editingUsername", yukonUserDao.getLiteYukonUser(user.getUserId()).getUsername());
        AuthenticationCategory[] authenticationCategories = AuthenticationCategory.values();
        model.addAttribute("authenticationCategories", authenticationCategories);
        Map<AuthenticationCategory, Boolean> supportsPasswordSet = Maps.newHashMap();
        for (AuthenticationCategory authenticationCategory : authenticationCategories) {
            AuthType authType = authenticationCategory.getSupportingAuthType();
            supportsPasswordSet.put(authenticationCategory, authenticationService.supportsPasswordSet(authType));
        }
        model.addAttribute("supportsPasswordSet", supportsPasswordSet);
        model.addAttribute("loginStatusTypes", LoginStatusEnum.values());
        model.addAttribute("userGroups", userGroupDao.getAllLiteUserGroups());
        model.addAttribute("pwChangeFormMode", PageEditMode.EDIT);

        Map<YukonRole, LiteYukonGroup> rolesAndGroups = roleDao.getRolesAndGroupsForUser(user.getUserId());
        ImmutableMultimap<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
        model.addAttribute("roles", sortedRoles.asMap());
    }

    private class UserValidator extends SimpleValidator<User> {
        public UserValidator() {
            super(User.class);
        }

        @Override
        public void doValidation(User user, Errors errors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username");
            YukonValidationUtils.checkExceedsMaxLength(errors, "username", user.getUsername(), 64);
            LiteYukonUser possibleDuplicate = yukonUserDao.findUserByUsername(user.getUsername());
            if (possibleDuplicate != null && user.getUserId() != possibleDuplicate.getUserID()) {
                errors.rejectValue("username", "unavailable.username");
            }
        }
    }

    private class PasswordValidator extends SimpleValidator<Password> {
        LiteYukonUser yukonUser;

        PasswordValidator(LiteYukonUser yukonUser) {
            super(Password.class);
            this.yukonUser = yukonUser;
        }

        @Override
        protected void doValidation(Password target, Errors errors) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "password", target.getPassword(), 64);
            YukonValidationUtils.checkExceedsMaxLength(errors, "confirmPassword", target.getConfirmPassword(), 64);
            if (!StringUtils.isBlank(target.getPassword()) || !StringUtils.isBlank(target.getConfirmPassword())) {
                if (!target.getPassword().equals(target.getConfirmPassword())) {
                    YukonValidationUtils.rejectValues(errors, "password.mismatch", "password", "confirmPassword");
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
                }

                if (passwordPolicyError != null) {
                    YukonValidationUtils.rejectValues(errors, passwordPolicyError.getFormatKey(), errorArgs,
                        "password", "confirmPassword");
                }
            }
        }
    }
}
