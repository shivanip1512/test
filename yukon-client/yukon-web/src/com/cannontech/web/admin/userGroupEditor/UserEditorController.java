package com.cannontech.web.admin.userGroupEditor;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

import com.cannontech.common.model.UpdatableYukonUser;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.YukonMessageCodeResolver;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.userGroupEditor.model.YukonUserValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/userEditor/*")
public class UserEditorController {

    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private RoleDao roleDao;
    private YukonUserValidator yukonUserValidator;
    private YukonGroupService yukonGroupService;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    
    /* Group Editor View Page*/
    @RequestMapping
    public String view(ModelMap model, int userId) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        setupModelMap(model, new UpdatableYukonUser(user));
        
        Map<YukonRole, LiteYukonGroup> rolesAndGroups = roleDao.getRolesAndGroupsForUser(userId);
        ImmutableMultimap<YukonRoleCategory, Pair<YukonRole, LiteYukonGroup>> sortedRoles = RoleListHelper.sortRolesByCategory(rolesAndGroups);
        model.addAttribute("roles", sortedRoles.asMap());
        
        return "userGroupEditor/user.jsp";
    }
    
    /* User Editor Edit Page*/
    @RequestMapping(value="edit", method=RequestMethod.POST, params="edit")
    public String edit(ModelMap model, int userId) {
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        setupModelMap(model, new UpdatableYukonUser(user));
        
        return "userGroupEditor/user.jsp";
    }
    

    /* Update Group */
    @RequestMapping(value="edit", method=RequestMethod.POST, params="update")
    public String update(@ModelAttribute("updatableUser") UpdatableYukonUser updatableUser, BindingResult result, ModelMap model, FlashScope flash) {
        
        yukonUserValidator.validate(updatableUser, result);
        
        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            model.addAttribute("mode", PageEditMode.EDIT);
            setupModelMap(model, updatableUser);
            return "userGroupEditor/user.jsp";
        }
        
        yukonUserDao.save(updatableUser);
        
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.userEditor.updateSuccessful"));
        setupModelMap(model, updatableUser);
        
        return "redirect:view";
    }
    
    /* Login Groups Page */
    @RequestMapping
    public String groups(ModelMap model, int userId) {
        LiteYukonUser user = yukonUserDao.getLiteYukonUser(userId);
        model.addAttribute("user", user);
        model.addAttribute("userId", user.getUserID());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("groups", yukonGroupDao.getGroupsForUser(userId));
        
        return "userGroupEditor/groups.jsp";
    }
    
    @RequestMapping(method=RequestMethod.POST)
    public String addGroups(ModelMap model, FlashScope flash, int userId, String groupIds) {
        List<Integer> groupIdsList = StringUtils.parseIntStringForList(groupIds);
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
    
    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        if (binder.getTarget() != null) {
            MessageCodesResolver msgCodesResolver = new YukonMessageCodeResolver("yukon.web.modules.adminSetup.userEditor.");
            binder.setMessageCodesResolver(msgCodesResolver);
        }
    }
    
    private void setupModelMap(ModelMap model, UpdatableYukonUser updatableUser) {
        model.addAttribute("updatableUser", updatableUser);
        model.addAttribute("userId", updatableUser.getUserID());
        model.addAttribute("username", updatableUser.getUsername());
        model.addAttribute("authTypes", AuthType.values());
        model.addAttribute("loginStatusTypes", LoginStatusEnum.values());
        model.addAttribute("energyCompanies", yukonEnergyCompanyService.getAllEnergyCompanies());
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
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
}