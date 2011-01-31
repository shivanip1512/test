package com.cannontech.web.admin.userGroupEditor;

import java.util.List;

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
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.google.common.collect.Lists;

@Controller
public class GroupEditorController {
    
    private YukonGroupDao yukonGroupDao;
    private YukonUserDao yukonUserDao;
    
    /* Group Editor View Page*/
    @RequestMapping("/groupEditor/view")
    public String view(YukonUserContext userContext, ModelMap modelMap, int groupId) {
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(groupId);
        modelMap.addAttribute("group", group);
        modelMap.addAttribute("groupId", group.getGroupID());
        modelMap.addAttribute("groupName", group.getGroupName());
        
        List<Category> categories = Lists.newArrayList();
        for (YukonRoleCategory roleCategory : YukonRoleCategory.values()) {
            List<YukonRole> roles = YukonRole.getForCategory(roleCategory);
            if (roles.isEmpty()) continue;
            
            Category category = new Category();
            category.setCategory(roleCategory);
            for(YukonRole yukonRole : roles) {
                Role role = new Role();
                role.setRole(yukonRole);
                if(role.enabled) {
                    category.selectedCount++;
                }
                category.getRoles().add(role);
            }
            categories.add(category);
        }
        modelMap.addAttribute("categories", categories);
        
        List<LiteYukonUser> members = yukonUserDao.getUsersForGroup(group.getGroupID());
        modelMap.addAttribute("members", members);
        
        return "userGroupEditor/group.jsp";
    }
    
    /* Group Editor Edit Page*/
    @RequestMapping(value="/groupEditor/edit", method=RequestMethod.POST, params="edit")
    public String edit(YukonUserContext userContext, ModelMap modelMap, int groupId) {
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(groupId);
        modelMap.addAttribute("group", group);
        modelMap.addAttribute("groupId", group.getGroupID());
        modelMap.addAttribute("groupName", group.getGroupName());
        
        return "userGroupEditor/group.jsp";
    }
    
    /* Update Group */
    @RequestMapping(value="/groupEditor/edit", method=RequestMethod.POST, params="update")
    public String update(@ModelAttribute("group") LiteYukonGroup group, BindingResult bindingResult,
                         YukonUserContext userContext, ModelMap modelMap, FlashScope flashScope) {
        
        new YukonGroupValidator().validate(group, bindingResult);
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            modelMap.addAttribute("mode", PageEditMode.EDIT);
            modelMap.addAttribute("group", group);
            modelMap.addAttribute("groupId", group.getGroupID());
            modelMap.addAttribute("groupName", group.getGroupName());
            return "userGroupEditor/group.jsp";
        }
        
        yukonGroupDao.save(group);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.updateSuccessful"));
        
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        modelMap.addAttribute("group", group);
        modelMap.addAttribute("groupId", group.getGroupID());
        modelMap.addAttribute("groupName", group.getGroupName());
        
        return "userGroupEditor/group.jsp";
    }
    
    /* Update Group */
    @RequestMapping(value="/groupEditor/edit", method=RequestMethod.POST, params="cancel")
    public String cancel(int groupId, YukonUserContext userContext, ModelMap modelMap) {
        modelMap.addAttribute("groupId", groupId);
        return "redirect:view";
    }
    
    /* Delete Group */
    @RequestMapping(value="/groupEditor/edit", method=RequestMethod.POST, params="delete")
    public String delete(int groupId, YukonUserContext userContext, ModelMap modelMap) {
        /* TODO */
        
        modelMap.addAttribute("groupId", groupId);
        return "redirect:view";
    }
    
    private class YukonGroupValidator extends SimpleValidator<LiteYukonGroup> {

        YukonGroupValidator() {
            super(LiteYukonGroup.class);
        }

        @Override
        protected void doValidation(LiteYukonGroup target, Errors errors) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", "yukon.web.modules.adminSetup.groupEditor.error.required.groupName");
            YukonValidationUtils.checkExceedsMaxLength(errors, "groupName", target.getGroupName(), 120);
            YukonValidationUtils.checkExceedsMaxLength(errors, "groupDescription", target.getGroupDescription(), 200);
        }
    }
    
    public class Category {
        private YukonRoleCategory category;
        private List<Role> roles = Lists.newArrayList();
        private int selectedCount;
        public YukonRoleCategory getCategory() {
            return category;
        }
        public void setCategory(YukonRoleCategory category) {
            this.category = category;
        }
        public List<Role> getRoles() {
            return roles;
        }
        public void setRoles(List<Role> roles) {
            this.roles = roles;
        }
        public String getName() {
            return category.name();
        }
        public int getSelectedCount() {
            return selectedCount;
        }
        public void setSelectedCount(int selectedCount) {
            this.selectedCount = selectedCount;
        }
    }
    
    public class Role {
        private YukonRole role;
        private boolean enabled;
        public YukonRole getRole() {
            return role;
        }
        public void setRole(YukonRole role) {
            this.role = role;
        }
        public boolean isEnabled() {
            return enabled;
        }
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        public String getName() {
            return role.name();
        }
    }
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
}