package com.cannontech.web.admin.energyCompany.general;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.util.ECUtils;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@RequestMapping("/energyCompany/general/*")
@Controller
public class GeneralInfoController {

    private StarsDatabaseCache starsDatabaseCache;
    private GeneralInfoService generalInfoService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private GeneralInfoValidator generalInfoValidator;
    private RolePropertyDao rolePropertyDao;
    
    /* View Page*/
    @RequestMapping
    public String view(YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, ecId, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Edit Page */
    @RequestMapping(value="update", params="edit", method=RequestMethod.POST)
    public String edit(YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, ecId, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        
        return "energyCompany/generalInfo.jsp";
    }
    
    /* Edit Page */
    @RequestMapping(value="update", params="cancel", method=RequestMethod.POST)
    public String cancel(YukonUserContext userContext, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, ecId, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        return "energyCompany/generalInfo.jsp";
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
    @RequestMapping(value="addMember", method=RequestMethod.POST)
    public String addMember(int memberId, FlashScope flashScope, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {
        StarsAdminUtil.addMember(starsDatabaseCache.getEnergyCompany(ecId), starsDatabaseCache.getEnergyCompany(memberId), -1);
        /* Member Added Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.memberAddSuccessful"));
        return "redirect:view";
    }
    
    /* Remove Member */
    @RequestMapping(value="removeMember", method=RequestMethod.POST)
    public String removeMember(int memberId, FlashScope flashScope, ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment) throws Exception {
        StarsAdminUtil.removeMember(starsDatabaseCache.getEnergyCompany(ecId), memberId);
        /* Member Removed Successfully */
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.generalInfo.memberRemoveSuccessful"));
        return "redirect:view";
    }
    
    private void setupModelMap(ModelMap modelMap, int ecId, EnergyCompanyInfoFragment energyCompanyInfoFragment, YukonUserContext userContext) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String none = messageSourceAccessor.getMessage("yukon.web.modules.adminSetup.generalInfo.none");
        modelMap.addAttribute("none", none);
        
        GeneralInfo generalInfo = generalInfoService.getGeneralInfo(energyCompany);
        modelMap.addAttribute("generalInfo", generalInfo);
        
        LiteYukonPAObject[] routes = energyCompany.getAllRoutes();
        modelMap.addAttribute("routes", routes);
        
        boolean canManageMembers = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_MANAGE_MEMBERS, userContext.getYukonUser());
        
        if (canManageMembers) {
            Iterable<LiteStarsEnergyCompany> memberCompanies = energyCompany.getChildren();
            List<MemberCompany> members = Lists.newArrayList(); 
            for (LiteStarsEnergyCompany ec : memberCompanies) {
                MemberCompany member = new MemberCompany();
                member.setEcId(ec.getEnergyCompanyId());
                member.setName(ec.getName());
                members.add(member);
            }
            modelMap.addAttribute("members", members);
    
            /* Build set to exclude from candidates */
            Set<LiteStarsEnergyCompany> excludeSet = Sets.newHashSet(memberCompanies);
            excludeSet.add(starsDatabaseCache.getDefaultEnergyCompany());
            List<LiteStarsEnergyCompany> energyCompanies = StarsDatabaseCache.getInstance().getAllEnergyCompanies();
            for (LiteStarsEnergyCompany company : energyCompanies) {
                if(ECUtils.getAllDescendants(company).contains(energyCompany)) {
                    excludeSet.add(company);
                }
            }
            
            Set<LiteStarsEnergyCompany> allSet = Sets.newHashSet(energyCompanies);
            
            Set<LiteStarsEnergyCompany> memberCandidates = Sets.difference(allSet, excludeSet);
            modelMap.addAttribute("memberCandidates", memberCandidates);
            
            /* Show Right Side Column */
            modelMap.addAttribute("cols", 2);
        } else {
            modelMap.addAttribute("cols", 1);
        }
        
    }
    
    public class MemberCompany {
        private int ecId;
        private String name;
        public int getEcId() {
            return ecId;
        }
        public void setEcId(int ecId) {
            this.ecId = ecId;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    
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
    
}