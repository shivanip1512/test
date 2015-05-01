package com.cannontech.web.admin.energyCompany.general;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.InUseException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfoValidator;
import com.cannontech.web.admin.energyCompany.general.service.GeneralInfoService;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.csrf.CsrfTokenService;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@RequestMapping("/energyCompany/general/*")
@Controller
public class GeneralInfoController {
    private final static String baseKey = "yukon.web.modules.adminSetup.generalInfo.";

    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private GeneralInfoService generalInfoService;
    @Autowired private GeneralInfoValidator generalInfoValidator;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsEventLogService starsEventLogService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private YukonGroupService yukonGroupService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private CsrfTokenService csrfTokenService;

    /* View Page*/
    @RequestMapping("view")
    public String view(YukonUserContext context, FlashScope flashScope, ModelMap model, int ecId, EnergyCompanyInfoFragment fragment) {
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);
        
        setupModelMap(model, context.getYukonUser(), ecId, fragment);
        model.addAttribute("mode", PageEditMode.VIEW);
        LiteYukonUser user = ecDao.getEnergyCompany(ecId).getUser();
        if (user.getUserGroupId() == null) {
            flashScope.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.missingUserGroup",
                                                             user.getUsername()));
        }
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Edit Page */
    @RequestMapping(value="update", params="edit", method=RequestMethod.POST)
    public String edit(YukonUserContext context, FlashScope flashScope,
                       ModelMap model, int ecId, EnergyCompanyInfoFragment fragment) {

        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);
        
        setupModelMap(model, context.getYukonUser(), ecId, fragment);
        model.addAttribute("mode", PageEditMode.EDIT);
        LiteYukonUser user = ecDao.getEnergyCompany(ecId).getUser();
        if(user.getUserGroupId() == null){
            flashScope.setWarning(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.missingUserGroup",
                                                             user.getUsername()));
        }
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Cancel Edit */
    @RequestMapping(value="update", params="cancel", method=RequestMethod.POST)
    public String cancel(ModelMap model, int ecId) {
        model.clear();
        model.addAttribute("ecId", ecId);
        return "redirect:view";
    }
    
    /* Delete Energy Company */
    @RequestMapping(value="delete")
    public String deleteConfirm(HttpServletRequest request, ModelMap model,
                                YukonUserContext context, int ecId,
                                EnergyCompanyInfoFragment fragment) {

        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);
        setupModelMap(model, context.getYukonUser(), ecId, fragment);

        LiteYukonUser user = context.getYukonUser();
        if (!energyCompanyService.canDeleteEnergyCompany(user, ecId)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to delete energy company with id:" + ecId);
        }
        
        return "energyCompany/deleteEnergyCompanyConfirm.jsp";
    }
    
    /* Delete Energy Company */
    @RequestMapping(value="delete", params="delete")
    public String delete(ModelMap model, YukonUserContext context, FlashScope flashScope, HttpServletRequest request,
                         int ecId, EnergyCompanyInfoFragment fragment) {
        LiteYukonUser user = context.getYukonUser();
        energyCompanyService.verifyViewPageAccess(user, ecId);

        if (!energyCompanyService.canDeleteEnergyCompany(user, ecId)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to delete energy company with id:" + ecId);
        }
        
        starsEventLogService.deleteEnergyCompanyAttempted(user, fragment.getCompanyName(), EventSource.OPERATOR);

        energyCompanyService.deleteEnergyCompany(user, ecId);

        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.deleteEnergyCompanyConfirm.deletedMsg", fragment.getCompanyName()));
        return "redirect:/admin/energyCompany/home";
    }

    /* Cancel Delete Energy Company */
    @RequestMapping(value="delete", params="cancel")
    public String cancelDelete(YukonUserContext context, ModelMap model, int ecId, EnergyCompanyInfoFragment fragment) {
        setupModelMap(model, context.getYukonUser(), ecId, fragment);
        model.addAttribute("mode", PageEditMode.VIEW);
        
        return "redirect:view";
    }
    
    /* Update General Info */
    @RequestMapping(value="update", params="save", method=RequestMethod.POST)
    public String update(@ModelAttribute("generalInfo") GeneralInfo generalInfo, BindingResult binding, FlashScope flash,
                         ModelMap model, YukonUserContext context, int ecId, 
                         EnergyCompanyInfoFragment fragment) throws CommandExecutionException, WebClientException {
        
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);
        
        // Don't validate if the name didn't change
        boolean ecNameChange = !fragment.getCompanyName().equals(generalInfo.getName());
        generalInfoValidator.setEcNameChange(ecNameChange);
        generalInfoValidator.validate(generalInfo, binding);

        if (binding.hasErrors()) {
            EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
            model.addAttribute("mode", PageEditMode.EDIT);
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(binding);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            
            return "energyCompany/generalInfo.jsp";
        }
        
        generalInfoService.save(generalInfo, context.getYukonUser());
        
        /* General Info Update Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "updateSuccessful"));
        return "redirect:view";
    }
    
    /* Add Member */
    @RequestMapping(value="manageMembers", params="add", method=RequestMethod.POST)
    public String addMember(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, int newMemberId, 
                            EnergyCompanyInfoFragment fragment) throws Exception {

        LiteYukonUser user = context.getYukonUser();
        if (!energyCompanyService.canManageMembers(user)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to add member energy companies");
        }
        
        StarsAdminUtil.addMember(starsDatabaseCache.getEnergyCompany(ecId), starsDatabaseCache.getEnergyCompany(newMemberId), -1, user);
        /* Member Added Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "memberAddSuccessful"));
        return "redirect:view";
    }
    
    /* Remove Member */
    @RequestMapping(value="manageMembers", params="remove", method=RequestMethod.POST)
    public String removeMember(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, int remove, 
                               EnergyCompanyInfoFragment fragment) throws NotAuthorizedException, TransactionException {
        
        LiteYukonUser user = context.getYukonUser();
        if (!energyCompanyService.canManageMembers(user)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to remove member energy companies");
        }
        
        StarsAdminUtil.removeMember(starsDatabaseCache.getEnergyCompany(ecId), remove);
        
        /* Member Removed Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "memberRemoveSuccessful"));
        return "redirect:view";
    }
    
    /* Create Member */
    @RequestMapping(value="manageMembers", params="create", method=RequestMethod.POST)
    public String createMember(ModelMap model, YukonUserContext context, FlashScope flash, int ecId) throws NotAuthorizedException {
        
        LiteYukonUser user = context.getYukonUser();
        if (!energyCompanyService.canCreateDeleteMembers(user)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to create member energy companies");
        }
        
        model.addAttribute("parentId", ecId);
        return "redirect:/admin/energyCompany/newMember";
    }
    
    /* Add Operator Group */
    @RequestMapping(value="updateOperatorGroups", params="!removeOperatorUserGroup", method=RequestMethod.POST)
    public String addOperatorUserGroups(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, String operatorUserGroupIds,
                               EnergyCompanyInfoFragment fragment) {
        
        ecMappingDao.addECToOperatorUserGroupMapping(ecId, StringUtils.parseIntStringForList(operatorUserGroupIds));
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "operatorGroupsUpdatedSuccessful"));

        model.clear();
        model.addAttribute("ecId", ecId);
        return "redirect:view";
    }

    /* Remove Operator Group */
    @RequestMapping(value="updateOperatorGroups", params="removeOperatorUserGroup", method=RequestMethod.POST)
    public String removeOperatorUserGroup(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, int removeOperatorUserGroup,
                               EnergyCompanyInfoFragment fragment) {
        /* Make sure they always have at least one operator group */
        if (ecMappingDao.getOperatorUserGroups(ecId).size() < 2) {
            EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
            flash.setError(new YukonMessageSourceResolvable(baseKey + "requireAtLeastOneOpGroup"));
            return "redirect:view";
        }

        try {
            ecMappingDao.deleteECToOperatorUserGroupMapping(ecId, removeOperatorUserGroup);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "operatorGroupsUpdatedSuccessful"));
        } catch (InUseException e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "operatorGroupsInUse"));
        }

        model.clear();
        model.addAttribute("ecId", ecId);
        return "redirect:view";
    }
    
    /* Add Customer Group */
    @RequestMapping(value="updateCustomerGroups", params="!removeCustomerUserGroup", method=RequestMethod.POST)
    public String addCustomerUserGroups(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, String customerUserGroupIds,
                               EnergyCompanyInfoFragment fragment) {
        
        ecMappingDao.addECToResidentialUserGroupMapping(ecId, StringUtils.parseIntStringForList(customerUserGroupIds));
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "customerGroupsUpdatedSuccessful"));

        model.clear();
        model.addAttribute("ecId", ecId);
        return "redirect:view";
    }
    
    /* Remove Customer Group */
    @RequestMapping(value="updateCustomerGroups", params="removeCustomerUserGroup", method=RequestMethod.POST)
    public String removeCustomerUserGroup(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, int removeCustomerUserGroup,
                               EnergyCompanyInfoFragment fragment) {
        try {
            ecMappingDao.deleteECToResidentialUserGroupMapping(ecId, removeCustomerUserGroup);
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "customerGroupsUpdatedSuccessful"));
        } catch (InUseException e) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "customerGroupsInUse"));
        }

        model.clear();
        model.addAttribute("ecId", ecId);
        return "redirect:view";
    }
    
    private void setupModelMap(ModelMap model, LiteYukonUser user, int ecId, EnergyCompanyInfoFragment fragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
      
        EnergyCompany energyCompany = ecDao.getEnergyCompany(ecId);
        GeneralInfo generalInfo = generalInfoService.getGeneralInfo(energyCompany);
        model.addAttribute("generalInfo", generalInfo);
        model.addAttribute("members", starsDatabaseCache.getEnergyCompany(ecId).getChildren());
        model.addAttribute("memberCandidates", energyCompanyService.getMemberCandidates(ecId));
    
        Function<LiteUserGroup, Integer> idFromUserGroup = new Function<LiteUserGroup, Integer>() {
            @Override
            public Integer apply(LiteUserGroup liteUserGroup) {
                return liteUserGroup.getUserGroupId();
            }};

        List<LiteUserGroup> operatorUserGroups = ecMappingDao.getOperatorUserGroups(ecId);
        model.addAttribute("operatorUserGroups", operatorUserGroups);
        List<Integer> operatorUserGroupIds = Lists.newArrayList(Iterables.transform(operatorUserGroups, idFromUserGroup));
        model.addAttribute("operatorUserGroupIds", operatorUserGroupIds);

        List<LiteUserGroup> customerUserGroups = ecMappingDao.getResidentialUserGroups(ecId);
        model.addAttribute("customerUserGroups", customerUserGroups);
        List<Integer> customerUserGroupIds = Lists.newArrayList(Iterables.transform(customerUserGroups, idFromUserGroup));
        model.addAttribute("customerUserGroupIds", customerUserGroupIds);
    }

    /* Model Attributes */
    @ModelAttribute("showParentLogin")
    public boolean getShowParentLogin(int ecId) {
        return starsDatabaseCache.getEnergyCompany(ecId).getParent() != null;
    }
    
    @ModelAttribute("operatorLogins")
    public List<LiteYukonUser> getOperatorLogins(int ecId) {
        List<LiteYukonUser> operators = Lists.newArrayList();
        for (int userId : ecDao.getOperatorUserIds(ecDao.getEnergyCompany(ecId))) {
            operators.add(yukonUserDao.getLiteYukonUser(userId));
        }
        return operators;
    }
    
    @ModelAttribute("none")
    public String getNone(YukonUserContext context) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(context);
        return messageSourceAccessor.getMessage("yukon.web.defaults.none");
    }
    
    @ModelAttribute("routes")
    public List<LiteYukonPAObject> getRoutes(int ecId) {
        return ecDao.getAllRoutes(ecDao.getEnergyCompany(ecId));
    }
    
    @ModelAttribute("canCreateDeleteMembers")
    public boolean getCanCreateDeleteMembers(YukonUserContext context) {
        return energyCompanyService.canCreateDeleteMembers(context.getYukonUser());
    }
    
    @ModelAttribute("canDelete")
    public boolean getCanDelete(YukonUserContext context, int ecId) {
        return energyCompanyService.canDeleteEnergyCompany(context.getYukonUser(), ecId);
    }
    
    @ModelAttribute("canManageMembers")
    public boolean getCanManageMembers (YukonUserContext context, ModelMap modelMap, int ecId) {
        return energyCompanyService.canManageMembers(context.getYukonUser());
    }
    
    @ModelAttribute("canEdit")
    public boolean getCanEdit(YukonUserContext context, int ecId) {
        return energyCompanyService.canEditEnergyCompany(context.getYukonUser(), ecId);
    }
 
}