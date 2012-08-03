package com.cannontech.web.admin.userGroupEditor;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@RequestMapping("/roleGroup/*")
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class RoleGroupEditorController {
    
    @Autowired private AuthenticationService authService;
    @Autowired private RoleDao roleDao;
    @Autowired private RolePropertyEditorDao rolePropertyEditorDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    
    /* Group Editor View Page*/
    @RequestMapping
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        return "userGroupEditor/userGroupHome.jsp";
    }
    
    @RequestMapping
    public String view(ModelMap model, int roleGroupId) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, group);
        
        Set<YukonRole> roles = roleDao.getRolesForGroup(roleGroupId);
        RoleListHelper.addRolesToModel(roles, model);
        
        return "userGroupEditor/group.jsp";
    }
    
    private void setupModelMap(ModelMap model, LiteYukonGroup group) {
        model.addAttribute("group", group);
        model.addAttribute("roleGroupId", group.getGroupID());
        model.addAttribute("groupName", group.getGroupName());
        model.addAttribute("editName", group.getGroupID() > -1 ? true : false);
    }

    /* Group Editor Edit Page*/
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(ModelMap model, int roleGroupId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, group);
        
        return "userGroupEditor/group.jsp";
    }
    
    /* Expire all users in this group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="expireAllPasswords")
    public String expireAllPasswords(ModelMap model, FlashScope flash, int roleGroupId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(roleGroupId);
        setupModelMap(model, group);
        
        authService.expireAllPasswords(group.getGroupID());
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.expiredAllPasswords"));
        return "redirect:view";
    }
    
    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="update")
    public String update(@ModelAttribute("group") LiteYukonGroup group, BindingResult result, ModelMap model, FlashScope flash) {
        
        new YukonGroupValidator().validate(group, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModelMap(model, group);
            return "userGroupEditor/group.jsp";
        }
        
        yukonGroupDao.save(group);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.updateSuccessful"));
        setupModelMap(model, group);
        
        return "redirect:view";
    }
    
    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="cancel")
    public String cancel(int roleGroupId, ModelMap model) {
        model.addAttribute("roleGroupId", roleGroupId);
        return "redirect:view";
    }
    
    /* Delete Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="delete")
    public String delete(int roleGroupId, ModelMap model, FlashScope flash) {
        yukonGroupDao.delete(roleGroupId);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.deletedSuccessful"));
        return "redirect:/spring/adminSetup/userGroupEditor/home";
    }
    
    /* Add Role */
    @RequestMapping(value="addRole", method=RequestMethod.POST)
    public String addRole(ModelMap model, FlashScope flash, int newRoleId, int roleGroupId) {
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(roleGroupId);
        YukonRole role = YukonRole.getForId(newRoleId);
        
        /* Save the default role properties to the db for this group and role */
        rolePropertyEditorDao.addRoleToGroup(group, role);
        
        model.addAttribute("roleGroupId", roleGroupId);
        model.addAttribute("roleId", newRoleId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.updateSuccessful"));
        return "redirect:/spring/adminSetup/roleEditor/view";
    }
    
    private class YukonGroupValidator extends SimpleValidator<LiteYukonGroup> {
        YukonGroupValidator() {
            super(LiteYukonGroup.class);
        }

        @Override
        protected void doValidation(LiteYukonGroup target, Errors errors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", "yukon.web.modules.adminSetup.groupEditor.error.required.groupName");
            YukonValidationUtils.checkExceedsMaxLength(errors, "groupName", target.getGroupName(), 120);
            try {
                LiteYukonGroup duplicate = yukonGroupDao.getLiteYukonGroupByName(target.getGroupName());
                if (duplicate.getGroupID() != target.getGroupID()) {
                    errors.rejectValue("groupName", "yukon.web.modules.adminSetup.groupEditor.error.unavailable.groupName");
                }
            } catch (NotFoundException e) {/* Ignore, name is available */}
            
            YukonValidationUtils.checkExceedsMaxLength(errors, "groupDescription", target.getGroupDescription(), 200);
        }
    }
}