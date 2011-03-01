package com.cannontech.web.admin.energyCompany.general;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.YukonGroupService;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.web.util.StarsAdminUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfoValidator;
import com.cannontech.web.admin.energyCompany.general.service.GeneralInfoService;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.google.common.collect.Lists;

@RequestMapping("/energyCompany/general/*")
@Controller
public class GeneralInfoController {

    private StarsDatabaseCache starsDatabaseCache;
    private GeneralInfoService generalInfoService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private GeneralInfoValidator generalInfoValidator;
    private EnergyCompanyService energyCompanyService;
    private ECMappingDao ecMappingDao;
    private YukonUserDao yukonUserDao;
    private RolePropertyDao rolePropertyDao;
    private YukonGroupService yukonGroupService;
    
    /* View Page*/
    @RequestMapping
    public String view(YukonUserContext context, ModelMap model, int ecId, EnergyCompanyInfoFragment fragment) {
        energyCompanyService.verifyViewPageAccess(context.getYukonUser(), ecId);
        
        setupModelMap(model, context.getYukonUser(), ecId, fragment);
        model.addAttribute("mode", PageEditMode.VIEW);
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Edit Page */
    @RequestMapping(value="update", params="edit", method=RequestMethod.POST)
    public String edit(YukonUserContext context, ModelMap model, int ecId, EnergyCompanyInfoFragment fragment) {
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);
        
        setupModelMap(model, context.getYukonUser(), ecId, fragment);
        model.addAttribute("mode", PageEditMode.EDIT);
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Cancel Edit */
    @RequestMapping(value="update", params="cancel", method=RequestMethod.POST)
    public String cancel(YukonUserContext context, ModelMap model, int ecId, EnergyCompanyInfoFragment fragment) {
        setupModelMap(model, context.getYukonUser(), ecId, fragment);
        model.addAttribute("mode", PageEditMode.VIEW);
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Delete Energy Company */
    @RequestMapping(value="update", params="delete", method=RequestMethod.POST)
    public String delete(ModelMap model, YukonUserContext context, int ecId) {
        LiteYukonUser user = context.getYukonUser();
        if(!energyCompanyService.canDeleteEnergyCompany(user, ecId)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to delete energy company with id:" + ecId);
        }
        
        /* TODO */
        return "TODO";
    }
    
    /* Update General Info */
    @RequestMapping(value="update", params="save", method=RequestMethod.POST)
    public String update(@ModelAttribute("generalInfo") GeneralInfo generalInfo, BindingResult binding, FlashScope flash,
                         ModelMap model, YukonUserContext context, int ecId, 
                         EnergyCompanyInfoFragment fragment) throws Exception {
        
        energyCompanyService.verifyEditPageAccess(context.getYukonUser(), ecId);
        
        generalInfoValidator.validate(generalInfo, binding);
        if(binding.hasErrors()) {
            setupModelMap(model, context.getYukonUser(), ecId, fragment);
            
            model.addAttribute("generalInfo", generalInfo);
            model.addAttribute("mode", PageEditMode.EDIT);
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(binding);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);
            
            return "energyCompany/generalInfo.jsp";
        }
        
        generalInfoService.save(generalInfo, context.getYukonUser());
        
        /* General Info Update Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.updateSuccessful"));
        return "redirect:view";
    }
    
    /* Add Member */
    @RequestMapping(value="manageMembers", params="add", method=RequestMethod.POST)
    public String addMember(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, int newMemberId, 
                            EnergyCompanyInfoFragment fragment) throws Exception {

        LiteYukonUser user = context.getYukonUser();
        if(!energyCompanyService.canManageMembers(user)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to add member energy companies");
        }
        
        StarsAdminUtil.addMember(starsDatabaseCache.getEnergyCompany(ecId), starsDatabaseCache.getEnergyCompany(newMemberId), -1);
        /* Member Added Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.memberAddSuccessful"));
        return "redirect:view";
    }
    
    /* Remove Member */
    @RequestMapping(value="manageMembers", params="remove", method=RequestMethod.POST)
    public String removeMember(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, int remove, 
                               EnergyCompanyInfoFragment fragment) throws NotAuthorizedException, TransactionException {
        
        LiteYukonUser user = context.getYukonUser();
        if(!energyCompanyService.canManageMembers(user)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to remove member energy companies");
        }
        
        StarsAdminUtil.removeMember(starsDatabaseCache.getEnergyCompany(ecId), remove);
        
        /* Member Removed Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.memberRemoveSuccessful"));
        return "redirect:view";
    }
    
    /* Create Member */
    @RequestMapping(value="manageMembers", params="create", method=RequestMethod.POST)
    public String createMember(ModelMap model, YukonUserContext context, FlashScope flash, int ecId) throws NotAuthorizedException {
        
        LiteYukonUser user = context.getYukonUser();
        if(!energyCompanyService.canCreateMembers(user)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to create member energy companies");
        }
        
        model.addAttribute("parentId", ecId);
        return "redirect:/spring/adminSetup/energyCompany/newMember";
    }
    
    /* Add Operator Group */
    @RequestMapping(value="updateOperatorGroups", params="!removeOperatorGroup", method=RequestMethod.POST)
    public String addOperatorGroups(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, String operatorGroupIds,
                               EnergyCompanyInfoFragment fragment) {
        
        ecMappingDao.addECToOperatorGroupMapping(ecId, StringUtils.parseIntStringForList(operatorGroupIds));
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.operatorGroupsUpdatedSuccessful"));
        return "redirect:view";
    }

    /* Remove Operator Group */
    @RequestMapping(value="updateOperatorGroups", params="removeOperatorGroup", method=RequestMethod.POST)
    public String removeOperatorGroup(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, int removeOperatorGroup,
                               EnergyCompanyInfoFragment fragment) {
        
        ecMappingDao.deleteECToOperatorGroupMapping(ecId, removeOperatorGroup);
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.operatorGroupsUpdatedSuccessful"));
        return "redirect:view";
    }
    
    /* Add Customer Group */
    @RequestMapping(value="updateCustomerGroups", params="!removeCustomerGroup", method=RequestMethod.POST)
    public String addCustomerGroups(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, String customerGroupIds,
                               EnergyCompanyInfoFragment fragment) {
        
        ecMappingDao.addECToResidentialGroupMapping(ecId, StringUtils.parseIntStringForList(customerGroupIds));
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.customerGroupsUpdatedSuccessful"));
        return "redirect:view";
    }
    
    /* Remove Customer Group */
    @RequestMapping(value="updateCustomerGroups", params="removeCustomerGroup", method=RequestMethod.POST)
    public String removeCustomerGroup(ModelMap model, YukonUserContext context, FlashScope flash, int ecId, int removeCustomerGroup,
                               EnergyCompanyInfoFragment fragment) {
        
        ecMappingDao.deleteECToResidentialGroupMapping(ecId, removeCustomerGroup);
        
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
        flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.customerGroupsUpdatedSuccessful"));
        return "redirect:view";
    }
    
    private void setupModelMap(ModelMap model, LiteYukonUser user, int ecId, EnergyCompanyInfoFragment fragment) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(fragment, model);
      
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        GeneralInfo generalInfo = generalInfoService.getGeneralInfo(energyCompany);
        model.addAttribute("generalInfo", generalInfo);
        
        if (energyCompanyService.canManageMembers(user)) {
            model.addAttribute("members", starsDatabaseCache.getEnergyCompany(ecId).getChildren());
            model.addAttribute("memberCandidates", energyCompanyService.getMemberCandidates(ecId));
        }
        
        model.addAttribute("operatorGroups", ecMappingDao.getOperatorGroups(ecId));
        model.addAttribute("customerGroups", ecMappingDao.getResidentialGroups(ecId));
    }
    
    /* Model Attributes */
    
    @ModelAttribute("showParentLogin")
    public boolean getShowParentLogin(int ecId) {
        return starsDatabaseCache.getEnergyCompany(ecId).getParent() != null;
    }
    
    @ModelAttribute("operatorLogins")
    public List<LiteYukonUser> getOperatorLogins(int ecId) {
        List<LiteYukonUser> operators = Lists.newArrayList();
        for (int userId : starsDatabaseCache.getEnergyCompany(ecId).getOperatorLoginIDs()) {
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
    public LiteYukonPAObject[] getRoutes(int ecId) {
        return starsDatabaseCache.getEnergyCompany(ecId).getAllRoutes();
    }
    
    @ModelAttribute("canCreateMembers")
    public boolean getCanCreateMembers(YukonUserContext context) {
        return energyCompanyService.canCreateMembers(context.getYukonUser());
    }
    
    @ModelAttribute("canDelete")
    public boolean getCanDelete(YukonUserContext context, int ecId) {
        return energyCompanyService.canDeleteEnergyCompany(context.getYukonUser(), ecId);
    }
    
    @ModelAttribute("canManageMembers")
    public boolean getCanManageMembers (YukonUserContext context, ModelMap modelMap, int ecId) {
        return energyCompanyService.canManageMembers(context.getYukonUser());
    }
    
    @ModelAttribute("canEditRoles")
    public boolean getCanEditRoles(ModelMap model, YukonUserContext context, int ecId) {
        LiteYukonUser user = context.getYukonUser();
        boolean superUser = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_SUPER_USER, user);
        if (superUser) {
            LiteYukonGroup group = yukonGroupService.getGroupByYukonRoleAndUser(YukonRole.ENERGY_COMPANY, user.getUserID());
            model.addAttribute("groupId", group.getGroupID());
            model.addAttribute("roleId", YukonRole.ENERGY_COMPANY.getRoleId());
        }
        return superUser;
    }
    
    @ModelAttribute("canEdit")
    public boolean getCanEdit(YukonUserContext context, int ecId) {
        return energyCompanyService.canEditEnergyCompany(context.getYukonUser(), ecId);
    }
    
    /* Dependencies */
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setGeneralInfoService(GeneralInfoService generalInfoService) {
        this.generalInfoService = generalInfoService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setGeneralInfoValidator(GeneralInfoValidator generalInfoValidator) {
        this.generalInfoValidator = generalInfoValidator;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setYukonGroupService(YukonGroupService yukonGroupService) {
        this.yukonGroupService = yukonGroupService;
    }
    
}