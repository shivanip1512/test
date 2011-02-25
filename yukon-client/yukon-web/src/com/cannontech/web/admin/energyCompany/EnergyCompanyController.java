package com.cannontech.web.admin.energyCompany;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.EnergyCompanyNameUnavailableException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.energyCompany.model.EnergyCompanyDto;
import com.cannontech.web.admin.energyCompany.model.EnergyCompanyDtoValidator;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.google.common.collect.Lists;

@Controller
public class EnergyCompanyController {
    
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private StarsDatabaseCache starsDatabaseCache;
    private RolePropertyDao rolePropertyDao;
    private EnergyCompanyDtoValidator energyCompanyDtoValidator;
    private EnergyCompanyService energyCompanyService;
    private YukonEnergyCompanyService yukonEnergyCompanyService;
    private PaoDao paoDao;
    private ECMappingDao ecMappingDao;
    
    /* Energy Company Setup Home Page*/
    @RequestMapping("/energyCompany/home")
    public String home(YukonUserContext userContext, ModelMap modelMap) {
        LiteYukonUser user = userContext.getYukonUser();
        List<YukonEnergyCompany> companies = Lists.newArrayList();
        boolean superUser = rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, user);
        
        if (superUser) {
            /* For super users show all energy companies. */
            modelMap.addAttribute("companies", starsDatabaseCache.getAllEnergyCompanies());
            return "energyCompany/home.jsp";
        }
        
        YukonEnergyCompany yukonEnergyCompany = yukonEnergyCompanyService.getEnergyCompanyByOperator(user);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(yukonEnergyCompany);
        
        if (energyCompany != null && energyCompany.getOperatorLoginIDs().contains(user.getUserID())) {
            /* If they belong to an energy company and are an operator, show energy company and all decendants. */
            companies.addAll(ecMappingDao.getChildEnergyCompanies(energyCompany.getEnergyCompanyId()));
        }
        
        modelMap.addAttribute("companies", companies);
        return "energyCompany/home.jsp";
    }
    
    /* Energy Company Creation Page*/
    @RequestMapping("/energyCompany/new")
    public String newEnergyCompany(YukonUserContext userContext, ModelMap modelMap) {
        EnergyCompanyDto energyCompanyDto = new EnergyCompanyDto();
        modelMap.addAttribute("energyCompanyDto", energyCompanyDto);
        
        return "energyCompany/create.jsp";
    }
    
    /* Energy Company Creation Page*/
    @RequestMapping("/energyCompany/newMember")
    public String newMember(YukonUserContext userContext, ModelMap modelMap, int parentId) {
        EnergyCompanyDto energyCompanyDto = new EnergyCompanyDto();
        modelMap.addAttribute("energyCompanyDto", energyCompanyDto);
        modelMap.addAttribute("parentId", parentId);
        
        return "energyCompany/create.jsp";
    }
    
    /* Energy Company Creation Page*/
    @RequestMapping(value="/energyCompany/create", params="save")
    public String create(@ModelAttribute("energyCompanyDto") EnergyCompanyDto energyCompanyDto, BindingResult bindingResult, 
            Integer parentId, FlashScope flashScope, YukonUserContext userContext, ModelMap modelMap) throws Exception {
        
        /* Validate Input */
        energyCompanyDtoValidator.validate(energyCompanyDto, bindingResult);
        if (bindingResult.hasErrors()) {
            return createFailed(bindingResult, flashScope);
        }
        
        try {
            LiteStarsEnergyCompany energyCompany = energyCompanyService.createEnergyCompany(energyCompanyDto, userContext.getYukonUser(), parentId);
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.createEnergyCompany.creationSuccessful", energyCompanyDto.getName()));
            modelMap.addAttribute("ecId", energyCompany.getEnergyCompanyId());
            return "redirect:/spring/adminSetup/energyCompany/general/view";
            
        } catch (EnergyCompanyNameUnavailableException e) {
            bindingResult.rejectValue("name", "yukon.web.modules.adminSetup.createEnergyCompany.name.unavailable");
        } catch (UserNameUnavailableException e) {
            bindingResult.rejectValue(e.getMessage(), "yukon.web.modules.adminSetup.createEnergyCompany.adminUsername.unavailable");
        }
        
        return createFailed(bindingResult, flashScope);
    }

    private String createFailed(BindingResult bindingResult, FlashScope flashScope) {
        List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
        flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
        
        return "energyCompany/create.jsp";
    }
    
    /* Cancel Energy Company Creation */
    @RequestMapping(value="/energyCompany/create", params="cancel")
    public String create() {
        return "redirect:home";
    }
    
    @ModelAttribute("routes")
    public List<LiteYukonPAObject> getRoutes(ModelMap modelMap) {
        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
        return Lists.newArrayList(routes);
    }
    
    @ModelAttribute("none")
    public String getNone(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return messageSourceAccessor.getMessage("yukon.web.defaults.none");
    }

    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setEnergyCompanyDtoValidator(EnergyCompanyDtoValidator energyCompanyDtoValidator) {
        this.energyCompanyDtoValidator = energyCompanyDtoValidator;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setYukonEnergyCompanyService(YukonEnergyCompanyService yukonEnergyCompanyService) {
        this.yukonEnergyCompanyService = yukonEnergyCompanyService;
    }
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
}