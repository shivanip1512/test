package com.cannontech.web.admin.energyCompany;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.ConfigurationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.EnergyCompanyNameUnavailableException;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.EnergyCompanyDto;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.energyCompany.model.EnergyCompanyDtoValidator;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.login.LoginService;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.util.SavedSession;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
public class EnergyCompanyController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private EnergyCompanyDtoValidator energyCompanyDtoValidator;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private PaoDao paoDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private LoginService loginService;
    @Autowired private ConfigurationSource configurationSource;
    
    /* Energy Company Setup Home Page*/
    @RequestMapping("/energyCompany/home")
    public String home(HttpServletRequest request, YukonUserContext userContext, ModelMap modelMap) {
        
        HttpSession session = request.getSession(false);
        SavedSession p = (SavedSession)session.getAttribute(LoginController.SAVED_YUKON_USERS);
        
        // p != null indicates there is a saved user.
        if (p != null) {
            Properties oldContext = (Properties) p.getProperties();
            LiteYukonUser previousUser = (LiteYukonUser) oldContext.get(LoginController.YUKON_USER);
            int previousUserId = previousUser.getUserID();
            modelMap.addAttribute("previousUserId", previousUserId);
        }
        
        LiteYukonUser user = userContext.getYukonUser();
        boolean isSuperUser = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_SUPER_USER, user);
        List<EnergyCompany> companies = null;
        
        if (isSuperUser) {
            /* For super users show all energy companies. */
            companies = Lists.newArrayList(ecDao.getAllEnergyCompanies());
            
            if (!configurationSource.getBoolean(MasterConfigBoolean.DEFAULT_ENERGY_COMPANY_EDIT)) {
                Iterator<EnergyCompany> iter = companies.iterator();
                while (iter.hasNext()) {
                    YukonEnergyCompany ec = iter.next();
                    if (ecDao.isDefaultEnergyCompany(ec)) {
                        iter.remove();
                        break;
                    }
                }
            }
        } else {
            companies = Lists.newArrayList();
            EnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
            
            if (ecDao.getOperatorUserIds(energyCompany).contains(user.getUserID())) {
                /* If they belong to an energy company and are an operator, show energy company and all descendants. */
                companies.addAll(energyCompany.getDescendants(true));
            }
        }
        
        setupHomeModelMap(modelMap, user, companies);
        
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
            return "redirect:/adminSetup/energyCompany/general/view";
            
        } catch (EnergyCompanyNameUnavailableException e) {
            bindingResult.rejectValue("name", "yukon.web.modules.adminSetup.createEnergyCompany.name.unavailable");
        } catch (UserNameUnavailableException e) {
            bindingResult.rejectValue(e.getMessage(), "yukon.web.modules.adminSetup.createEnergyCompany.adminUsername.unavailable");
        } catch (ConfigurationException e){
            bindingResult.reject("yukon.web.modules.adminSetup.createEnergyCompany.primaryOperatorGroup.roleGroupConflict");
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
    
    /* Parent login to member energy company */
    @RequestMapping(value="/energyCompany/parentLogin", params="loginAsUserId")
    public String parentLogin(HttpServletRequest request, HttpSession session, YukonUserContext userContext, int loginAsUserId) {
        
        LiteYukonUser user = userContext.getYukonUser();
        LiteYukonUser memberOperator = yukonUserDao.getLiteYukonUser(loginAsUserId);
        EnergyCompany memberEc;
        
        if (!energyCompanyService.canManageMembers(user)) {
            throw new NotAuthorizedException("User " + user.getUsername() + " not authorized to manage members.");
        }
        
        try {
            memberEc = ecDao.getEnergyCompanyByOperator(memberOperator);
        } catch (EnergyCompanyNotFoundException e) {
            throw new NotAuthorizedException("User " + memberOperator.getUsername() + " is not an energy company operator.");
        }
        
        boolean isParentOperator = energyCompanyService.isParentOperator(user.getUserID(), memberEc.getId());
        if (!isParentOperator) {
            throw new NotAuthorizedException("User " + user.getUsername() + " not authorized to manage this member.");
        }
        
        /* Do internal login as operator of member energy company */
        CtiNavObject nav = (CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE);
        memberOperator = loginService.internalLogin(request, session, memberOperator.getUsername(), true);
        
        /* Set new CtiNavObject */
        nav = new CtiNavObject();
        nav.setMemberECAdmin(true);
        HttpSession newSession = request.getSession(false);
        if (newSession != null) {
            newSession.setAttribute(ServletUtils.NAVIGATE, nav);
        }
        
        return "redirect:/home";
    }
    
    /* Model Attributes */
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
    
    /* Helper Methods */
    private void setupHomeModelMap(ModelMap modelMap, LiteYukonUser user, List<EnergyCompany> companies) {
        modelMap.addAttribute("companies", companies);
        modelMap.addAttribute("parentLogins", getParentLogins(companies));
        modelMap.addAttribute("canManageMembers", energyCompanyService.canManageMembers(user));
        modelMap.addAttribute("loggedInUserId", user.getUserID());
    }
    
    private Map<Integer, Integer> getParentLogins(Iterable<EnergyCompany> companies) {
        
        Map<Integer, Integer> parentLogins = Maps.newHashMap();
        for (EnergyCompany company : companies) {
            if(company.getParent() != null) {
                int energyCompanyId = company.getId();
                LiteYukonUser parentLogin = ecMappingDao.findParentLogin(energyCompanyId);
                if (parentLogin != null) {
                    parentLogins.put(energyCompanyId, parentLogin.getUserID());
                }
            }
        }
        
        return parentLogins;
    }
    
}