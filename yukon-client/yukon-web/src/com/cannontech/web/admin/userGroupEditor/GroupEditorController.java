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

import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;

@Controller
@RequestMapping("/groupEditor/*")
public class GroupEditorController {
    
    private YukonGroupDao yukonGroupDao;
    private YukonUserDao yukonUserDao;
    private RoleDao roleDao;
    private RolePropertyEditorDao rolePropertyEditorDao;
    
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
    
    /* Group Editor View Page*/
    @RequestMapping
    public String view(ModelMap model, int groupId) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(groupId);
        setupModelMap(model, group);
        
        Set<YukonRole> roles = roleDao.getRolesForGroup(groupId);
        RoleListHelper.addRolesToModel(roles, model);
        
        return "userGroupEditor/group.jsp";
    }
    
    private void setupModelMap(ModelMap model, LiteYukonGroup group) {
        model.addAttribute("group", group);
        model.addAttribute("groupId", group.getGroupID());
        model.addAttribute("groupName", group.getGroupName());
    }

    /* Group Editor Edit Page*/
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(ModelMap model, int groupId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(groupId);
        setupModelMap(model, group);
        
        return "userGroupEditor/group.jsp";
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
    public String cancel(int groupId, ModelMap model) {
        model.addAttribute("groupId", groupId);
        return "redirect:view";
    }
    
    /* Delete Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="delete")
    public String delete(int groupId, ModelMap model, FlashScope flash) {
        yukonGroupDao.delete(groupId);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.deletedSuccessful"));
        return "redirect:/spring/adminSetup/userGroupEditor/home";
    }
    
    /* Add Role */
    @RequestMapping(value="addRole", method=RequestMethod.POST)
    public String addRole(ModelMap model, FlashScope flash, int newRoleId, int groupId) {
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(groupId);
        YukonRole role = YukonRole.getForId(newRoleId);
        
        /* Save the default role properties to the db for this group and role */
        rolePropertyEditorDao.addRoleToGroup(group, role);
        
        model.addAttribute("groupId", groupId);
        model.addAttribute("roleId", newRoleId);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.updateSuccessful"));
        return "redirect:/spring/adminSetup/roleEditor/view";
    }
    
    /* Users Tab */
    @RequestMapping("users")
    public String users(ModelMap model, FlashScope flash, int groupId, Integer itemsPerPage, Integer page) {
        
        if(page == null){
            page = 1;
        }
        if(itemsPerPage == null){
            itemsPerPage = 25;
        }
        
        int startIndex = (page - 1) * itemsPerPage;
        
        LiteYukonGroup group = yukonGroupDao.getLiteYukonGroup(groupId);
        
        SearchResult<LiteYukonUser> searchResult = yukonUserDao.getUsersForGroup(group.getGroupID(), startIndex, itemsPerPage);
        model.addAttribute("searchResult", searchResult);
        List<LiteYukonUser> users = searchResult.getResultList();
        model.addAttribute("users", users);
        
        setupModelMap(model, group);
        
        return "userGroupEditor/users.jsp";
    }
    
    /* Remove User From Group */
    @RequestMapping(value="removeUser", method=RequestMethod.POST, params="remove")
    public String removeUser(ModelMap model, FlashScope flash, int groupId, int remove) {
        yukonUserDao.removeUserFromGroup(remove, groupId);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.updateSuccessful"));
        model.addAttribute("groupId", groupId);
        return "redirect:users";
    }
    
    /* Remove User From Group */
    @RequestMapping(value="addUsers", method=RequestMethod.POST)
    public String addUsers(ModelMap model, FlashScope flash, int groupId, String userIds) {
        List<Integer> userIdList = StringUtils.parseIntStringForList(userIds);
        for (Integer userId : userIdList) {
            yukonUserDao.addUserToGroup(userId, groupId);
        }
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.groupEditor.updateSuccessful"));
        model.addAttribute("groupId", groupId);
        return "redirect:users";
    }
    
    /* Dependencies */
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
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
    public void setRolePropertyEditorDao(RolePropertyEditorDao rolePropertyEditorDao) {
        this.rolePropertyEditorDao = rolePropertyEditorDao;
    }
    
}