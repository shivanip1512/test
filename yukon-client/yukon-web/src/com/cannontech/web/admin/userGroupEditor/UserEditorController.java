package com.cannontech.web.admin.userGroupEditor;

import static com.cannontech.common.util.StringUtils.parseIntStringForList;

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
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.userGroupEditor.model.YukonUserValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/userEditor/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class UserEditorController {

    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private RoleDao roleDao;
    private YukonUserValidator yukonUserValidator;
    private YukonGroupService yukonGroupService;
    private AuthenticationService authenticationService;
    
    private class PasswordValidator extends SimpleValidator<PasswordChange>{
        
        public PasswordValidator() {
            super(PasswordChange.class);
        }

        @Override
        protected void doValidation(PasswordChange target, Errors errors) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "password", target.getPassword(), 64);
            YukonValidationUtils.checkExceedsMaxLength(errors, "confirmPassword", target.getConfirmPassword(), 64);
            if (!StringUtils.isBlank(target.getPassword()) || !StringUtils.isBlank(target.getConfirmPassword())) {
                
                if (!target.getPassword().equals(target.getConfirmPassword())) {
                    errors.rejectValue("password", "password.mismatch");
                    errors.rejectValue("confirmPassword", "password.mismatch");
                }
            }
        }
    };
    
    public static class PasswordChange {
        
        public PasswordChange() {}
        
        private String password;
        private String confirmPassword;
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
        public String getConfirmPassword() {
            return confirmPassword;
        }
        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
    
    /* Group Editor View Page */
    @RequestMapping
    public String view(ModelMap model, int userId) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        model.addAttribute("user", user);
        model.addAttribute("passwordChange", new PasswordChange());
        model.addAttribute("mode", PageEditMode.VIEW);
        setupModelMap(model, user);
        
        return "userGroupEditor/user.jsp";
    }
    
    /* User Editor Edit Page */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(ModelMap model, int userId) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        model.addAttribute("user", user);
        model.addAttribute("passwordChange", new PasswordChange());
        model.addAttribute("mode", PageEditMode.EDIT);
        setupModelMap(model, user);
        
        return "userGroupEditor/user.jsp";
    }
    
    /* Cancel Edit */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="cancel")
    public String cancel(ModelMap model, int userId) {
        model.addAttribute("userId", userId);
        return "redirect:view";
    }

    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="update")
    public String update(@ModelAttribute("user") LiteYukonUser user, BindingResult result, ModelMap model, FlashScope flash) {
        
        yukonUserValidator.validate(user, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("mode", PageEditMode.EDIT);
            model.addAttribute("passwordChange", new PasswordChange());
            setupModelMap(model, user);
            return "userGroupEditor/user.jsp";
        }
        
        yukonUserDao.save(user);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.updateSuccessful"));
        setupModelMap(model, user);
        
        return "redirect:view";
    }
    
    /* Login Groups Page */
    @RequestMapping
    public String groups(ModelMap model, int userId) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getUserID());
        model.addAttribute("editingUsername", user.getUsername());
        model.addAttribute("groups", yukonGroupDao.getGroupsForUser(userId, true));
        
        return "userGroupEditor/groups.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String addGroups(ModelMap model, FlashScope flash, int userId, String groupIds) {
        List<Integer> groupIdsList = parseIntStringForList(groupIds);
        List<String> conflictingGroups = checkGroupsForConflicts(groupIdsList, userId);
        
        model.addAttribute("userId", userId);
        
        if (!conflictingGroups.isEmpty()) {
            String groupNames = conflictingGroups.toString();
            flash.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.groupConflict", groupNames));
            return "redirect:groups";
        } else {
            for (int groupId : groupIdsList) {
                yukonGroupService.addUserToGroup(groupId, userId);
            }
            flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.updateSuccessful"));
            return "redirect:groups";
        }
    }
    
    private List<String> checkGroupsForConflicts(List<Integer> groupIds, int userId) {
        List<String> groupNames = Lists.newArrayList();
        for (int groupId : groupIds) {
            if (yukonGroupService.addToGroupWillHaveConflicts(userId, groupId)) {
                groupNames.add(yukonGroupDao.getLiteYukonGroup(groupId).getGroupName());
            }
        }
        return groupNames;
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String removeGroup(ModelMap model, FlashScope flash, int userId, int remove) {
        yukonUserDao.removeUserFromGroup(userId, remove);
        model.addAttribute("userId", userId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.updateSuccessful"));
        return "redirect:groups";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String changePassword(ModelMap model, FlashScope flash, int userId, 
                                 @ModelAttribute PasswordChange passwordChange, BindingResult result) {
        
        new PasswordValidator().validate(passwordChange, result);
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);

        if (result.hasErrors()) {
            model.addAttribute("showChangePasswordErrors", true);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("showChangePwPopup", true);
            model.addAttribute("user", user);
            setupModelMap(model, user);
            model.addAttribute("pwChangeFormMode", PageEditMode.EDIT);
            model.addAttribute("mode", PageEditMode.VIEW);
            return "userGroupEditor/user.jsp";
        }
        
        authenticationService.setPassword(user, passwordChange.getPassword());
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.passwordUpdateSuccessful"));
        setupModelMap(model, user);
        return "redirect:view";
    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver("yukon.web.modules.adminSetup.userEditor.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
    private void setupModelMap(ModelMap model, LiteYukonUser user) {
        model.addAttribute("userId", user.getUserID());
        model.addAttribute("editNameAndStatus", user.getUserID() > -1);
        
        model.addAttribute("editingUsername", yukonUserDao.getLiteYukonUser(user.getUserID()).getUsername());
        model.addAttribute("authTypes", AuthType.values());
        model.addAttribute("loginStatusTypes", LoginStatusEnum.values());
        model.addAttribute("showChangePassword", authenticationService.supportsPasswordSet(user.getAuthType()));
        model.addAttribute("pwChangeFormMode", PageEditMode.EDIT);

        Map<YukonRole, LiteYukonGroup> rolesAndGroups = roleDao.getRolesAndGroupsForUser(user.getUserID());
        ImmutableMultimap<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
        model.addAttribute("roles", sortedRoles.asMap());
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    @Autowired
    public void setYukonUserValidator(YukonUserValidator yukonUserValidator) {
        this.yukonUserValidator = yukonUserValidator;
    }
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    @Autowired
    public void setYukonGroupService(YukonGroupService yukonGroupService) {
        this.yukonGroupService = yukonGroupService;
    }
    
    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
}