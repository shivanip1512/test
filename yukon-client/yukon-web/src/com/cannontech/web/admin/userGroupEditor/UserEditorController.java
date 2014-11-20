package com.cannontech.web.admin.userGroupEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
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
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.userGroupEditor.model.Password;
import com.cannontech.web.admin.userGroupEditor.model.RoleAndGroup;
import com.cannontech.web.admin.userGroupEditor.model.User;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.security.csrf.CsrfTokenService;
import com.cannontech.web.stars.service.PasswordResetService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

@Controller
@RequestMapping("/user/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserEditorController {
    
    private UserValidator userValidator = new UserValidator();
    private final static String baseKey = "yukon.web.modules.adminSetup.userEditor.";
    
    @Autowired private AuthenticationService authService;
    @Autowired private RoleDao roleDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private PasswordPolicyService passwordPolicyService;
    @Autowired private PasswordResetService passwordResetService;
    @Autowired private CsrfTokenService csrfTokenService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    
    /* Group Editor View Page */
    @RequestMapping("home")
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        return "userGroupEditor/userHome.jsp";
    }
    
    @RequestMapping("view")
    public String view(YukonUserContext userContext, ModelMap model, int userId) {
        
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(userId);
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(userId);
        User user = new User(yukonUser, userAuthenticationInfo);
        setupModelMap(model, user, PageEditMode.VIEW, userContext);
        
        return "userGroupEditor/user.jsp";
    }
    
    /* User Editor Edit Page */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(HttpServletRequest request, YukonUserContext userContext, ModelMap model, int userId) {
        
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(userId);
        User user = new User(yukonUserDao.getLiteYukonUser(userId), userAuthenticationInfo);
        setupModelMap(model, user, PageEditMode.EDIT, userContext);
        
        return "userGroupEditor/user.jsp";
    }
    
    @RequestMapping("permissions")
    public String permissions(ModelMap model, int userId, YukonUserContext userContext) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, userContext.getYukonUser());
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        
        AuthenticationThrottleDto authThrottleDto = authService.getAuthenticationThrottleData(user.getUsername());
        
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getUserID());
        model.addAttribute("editingUsername", user.getUsername()); // Used by layout controller.
        model.addAttribute("authThrottleDto", authThrottleDto);
        return "userGroupEditor/editUser.jsp";
    }

    @RequestMapping(value="removeLoginWait", method = RequestMethod.POST)
    public String removeLoginWait(ModelMap model, int userId, YukonUserContext userContext) {
        
        rolePropertyDao.verifyProperty(YukonRoleProperty.ADMIN_LM_USER_ASSIGN, userContext.getYukonUser());
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        authService.removeAuthenticationThrottle(user.getUsername());
        model.addAttribute("userId", userId);
        
        return "redirect:permissions";
    }
    
    /* Unlock User */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="unlockUser")
    public String unlock(ModelMap model, FlashScope flash, int userId) {
        
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        authService.removeAuthenticationThrottle(user.getUsername());
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "userUnlocked"));
        
        return redirectToView(model, userId);
    }
    
    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="update")
    public String update(HttpServletRequest request, YukonUserContext userContext, 
            @ModelAttribute User user, BindingResult result, ModelMap model, FlashScope flash) {
        
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(user.getUserId());
        UserAuthenticationInfo userAuthenticationInfo = yukonUserDao.getUserAuthenticationInfo(user.getUserId());
        user.updateForSave(yukonUser, userAuthenticationInfo);
        boolean requiresPasswordChanged = user.isAuthenticationChanged()
                && authService.supportsPasswordSet(user.getAuthCategory());
        if (requiresPasswordChanged) {
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
        
        yukonUserDao.save(yukonUser);
        if (requiresPasswordChanged) {
            authService.setPassword(yukonUser, user.getAuthCategory(), user.getPassword().getPassword());
        } else if (user.isAuthenticationChanged()) {
            authService.setAuthenticationCategory(yukonUser, user.getAuthCategory());
        }
        
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "updateSuccessful"));
        
        return redirectToView(model, user.getUserId());
    }
    
    @RequestMapping(value="change-password", method=RequestMethod.POST)
    public @ResponseBody List<String> changePassword(HttpServletResponse resp, YukonUserContext userContext, 
            FlashScope flash, int userId, @ModelAttribute Password password, BindingResult result) {
        
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(userId);
        PasswordValidator validator = new PasswordValidator(yukonUser,  "password", "confirmPassword");
        validator.validate(password, result);
        
        if (result.hasErrors()) {
            
            MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
            List<MessageSourceResolvable> messages = validator.getMessages();
            List<String> errors = new ArrayList<>();
            for (MessageSourceResolvable message : messages) {
                errors.add(accessor.getMessage(message));
            }
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            
            return errors;
        }
        
        authService.setPassword(yukonUser, password.getPassword());
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "passwordUpdateSuccessful"));
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
        return null;
    }
    
    private String redirectToView(ModelMap model, int userId) {
        model.clear();
        model.addAttribute("userId", userId);
        return "redirect:view";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver(baseKey + "");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
    private void setupModelMap(ModelMap model, User user, PageEditMode mode, YukonUserContext userContext) {
        
        PasswordPolicy passwordPolicy = passwordPolicyService.getPasswordPolicy(userContext.getYukonUser());
        model.addAttribute("passwordPolicy", passwordPolicy);
        model.addAttribute("changePwdKey", passwordResetService.getPasswordKey(userContext.getYukonUser()));
        model.addAttribute("user", user);
        model.addAttribute("password", new Password());
        model.addAttribute("mode", mode);
        
        model.addAttribute("currentUserId",userContext.getYukonUser().getLiteID());
        model.addAttribute("userId", user.getUserId());
        model.addAttribute("editNameAndStatus", user.getUserId() > -1);
        
        model.addAttribute("editingUsername", yukonUserDao.getLiteYukonUser(user.getUserId()).getUsername());
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
        
        model.addAttribute("pwChangeFormMode", PageEditMode.EDIT);
        
        Multimap<YukonRole, LiteYukonGroup> rolesAndGroups = roleDao.getRolesAndGroupsForUser(user.getUserId());
        Multimap<YukonRoleCategory, RoleAndGroup> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
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
        
        private List<MessageSourceResolvable> messages = Lists.newArrayList();
        LiteYukonUser yukonUser;
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
                messages.add(new YukonMessageSourceResolvable(baseKey + "password.mismatch"));
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