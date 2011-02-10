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
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.util.ECUtils;
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

@RequestMapping("/energyCompany/general/*")
@Controller
public class GeneralInfoController {

    private StarsDatabaseCache starsDatabaseCache;
    private GeneralInfoService generalInfoService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private GeneralInfoValidator generalInfoValidator;
    private RolePropertyDao rolePropertyDao;
    private ECMappingDao ecMappingDao;
    private EnergyCompanyService energyCompanyService;
    
    /* View Page*/
    @RequestMapping
    public String view(YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        verifyPageAccess(ecId, userContext.getYukonUser());
        setupModelMap(modelMap, ecId, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Verify the user can view this energy company page. */
    private void verifyPageAccess(int ecId, LiteYukonUser user) {
        if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user)) return;
        /* Check my own and all my anticendants operator login list for this user's id. */
        for (int energyCompanyId : ecMappingDao.getParentEnergyCompanyIds(ecId)) {
            LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
            if(energyCompany.getOperatorLoginIDs().contains(user.getUserID())) return;
        }
        throw new NotAuthorizedException("User " + user.getUsername() + " is not authorized to view energy company with id " + ecId);
    }

    /* Edit Page */
    @RequestMapping(value="update", params="edit", method=RequestMethod.POST)
    public String edit(YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, ecId, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Cancel Edit */
    @RequestMapping(value="update", params="cancel", method=RequestMethod.POST)
    public String cancel(YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, ecId, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Delete Energy Company */
    @RequestMapping(value="update", params="delete", method=RequestMethod.POST)
    public String delete(ModelMap modelMap, int ecId) {
        /* TODO */
        return "TODO";
    }
    
    /* Update General Info */
    @RequestMapping(value="update", params="save", method=RequestMethod.POST)
    public String update(@ModelAttribute("generalInfo") GeneralInfo generalInfo, BindingResult bindingResult, FlashScope flashScope,
                         YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {
        
        generalInfoValidator.validate(generalInfo, bindingResult);
        if(bindingResult.hasErrors()) {
            setupModelMap(modelMap, ecId, energyCompanyInfoFragment, userContext);
            
            modelMap.addAttribute("generalInfo", generalInfo);
            modelMap.addAttribute("mode", PageEditMode.EDIT);
            
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            
            return "energyCompany/generalInfo.jsp";
        }
        
        generalInfoService.save(generalInfo, userContext.getYukonUser());
        
        /* General Info Update Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.updateSuccessful"));
        return "redirect:view";
    }
    
    /* Add Member */
    @RequestMapping(value="manageMembers", params="add", method=RequestMethod.POST)
    public String addMember(int newMemberId, FlashScope flashScope, ModelMap modelMap, int ecId, 
                            EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {
        
        StarsAdminUtil.addMember(starsDatabaseCache.getEnergyCompany(ecId), starsDatabaseCache.getEnergyCompany(newMemberId), -1);
        /* Member Added Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.memberAddSuccessful"));
        return "redirect:view";
    }
    
    /* Remove Member */
    @RequestMapping(value="manageMembers", params="remove", method=RequestMethod.POST)
    public String removeMember(int remove, FlashScope flashScope, ModelMap modelMap, int ecId, 
                               EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {
        StarsAdminUtil.removeMember(starsDatabaseCache.getEnergyCompany(ecId), remove);
        /* Member Removed Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.memberRemoveSuccessful"));
        return "redirect:view";
    }
    
    /* Create Member */
    @RequestMapping(value="manageMembers", params="create", method=RequestMethod.POST)
    public String createMember(FlashScope flashScope, ModelMap modelMap, int ecId, 
                            EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {
        modelMap.addAttribute("parentId", ecId);
        return "redirect:/spring/adminSetup/energyCompany/newMember";
    }
    
    private void setupModelMap(ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment, 
                               YukonUserContext userContext) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
      
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        GeneralInfo generalInfo = generalInfoService.getGeneralInfo(energyCompany);
        modelMap.addAttribute("generalInfo", generalInfo);
    }
    
    @ModelAttribute("none")
    public String getNone(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return messageSourceAccessor.getMessage("yukon.web.defaults.none");
    }
    
    @ModelAttribute("routes")
    public LiteYukonPAObject[] getRoutes(int ecId) {
        return starsDatabaseCache.getEnergyCompany(ecId).getAllRoutes();
    }
    
    @ModelAttribute("canCreateMembers")
    public boolean getCanCreateMembers(YukonUserContext userContext) {
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, 
                                                                    userContext.getYukonUser());
        boolean manageMembers = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, userContext.getYukonUser());
        
        boolean createAndDelete = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_CREATE_DELETE_ENERGY_COMPANY, 
                                                                          userContext.getYukonUser());
        return superUser || (manageMembers && createAndDelete);
    }
    
    @ModelAttribute("canDelete")
    public boolean getCanDelete(YukonUserContext userContext, int ecId) {
        int userId = userContext.getYukonUser().getUserID();
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, 
                                                                    userContext.getYukonUser());
        boolean createAndDelete = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_CREATE_DELETE_ENERGY_COMPANY, 
                                                                          userContext.getYukonUser());
        boolean manageMembers = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, userContext.getYukonUser());
        boolean isOperator = starsDatabaseCache.getEnergyCompany(ecId).getOperatorLoginIDs().contains(userId); 
        
        /* Can delete
         * IF user is a 'super user'
         * OR user is operator of one of my parent energy companies and has manage members role property
         * OR user is energy companies operator and has create/delete energy company role property */
        return superUser 
            || (isParentOperator(userId, ecId) && manageMembers && createAndDelete)
            || (isOperator && createAndDelete);
    }
    
    @ModelAttribute("canManageMembers")
    public boolean getCanManageMembers (YukonUserContext userContext, ModelMap modelMap, int ecId) {
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, 
            userContext.getYukonUser());
        boolean canManageMembers = superUser
            || rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, userContext.getYukonUser());
        
        if (canManageMembers) {
            modelMap.addAttribute("members", starsDatabaseCache.getEnergyCompany(ecId).getChildren());
            modelMap.addAttribute("memberCandidates", energyCompanyService.getMemberCandidates(ecId));
        }
        return canManageMembers;
    }
    
    @ModelAttribute("canEdit")
    public boolean getCanEdit(YukonUserContext userContext, int ecId) {
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, 
                                                                    userContext.getYukonUser());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        
        /* Can edit 
         * IF user is a 'super user'
         * OR user is energy companies operator and has edit energy company role property
         * OR user is operator of one of my parent energy companies and has manage members role property  */
        boolean canEdit = superUser 
            || (energyCompany.getOperatorLoginIDs().contains(userContext.getYukonUser().getUserID())
                && rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_EDIT_ENERGY_COMPANY, userContext.getYukonUser()))
            || (isParentOperator(userContext.getYukonUser().getUserID(), ecId) 
                    &&  rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, userContext.getYukonUser()));
        
        return canEdit;
    }
    
    /* Helper Methods */
    
    /**
     * Returns true if the user is an operator of one of the energy companies parents.
     */
    private boolean isParentOperator(int userId, int ecId) {
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        List<LiteStarsEnergyCompany> allAscendants = ECUtils.getAllAscendants(energyCompany);
        allAscendants.remove(energyCompany); /* Remove this ec since that is a different rp check */
        for (LiteStarsEnergyCompany parentEc : allAscendants) {
            if (parentEc.getOperatorLoginIDs().contains(userId)) {
                return true;
            }
        }
        return false;
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
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
}