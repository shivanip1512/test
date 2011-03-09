package com.cannontech.web.admin.energyCompany.general;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.model.Substation;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteSubstation;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;
import com.cannontech.web.admin.energyCompany.general.service.GeneralInfoService;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyService;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.base.Function;
import com.google.common.collect.Lists;


@RequestMapping("/energyCompany/routesAndSubstations/*")
@Controller
public class RouteAndSubstationController { 

    private EnergyCompanyService energyCompanyService;
    private GeneralInfoService generalInfoService;
    private PaoDao paoDao;
    private RolePropertyDao rolePropertyDao;
    private StarsDatabaseCache starsDatabaseCache;
    private SubstationDao substationDao;
    
    /**
     * This method handles the view version of the route and substation controller.
     */
    @RequestMapping
    public String view(YukonUserContext userContext, ModelMap modelMap, int ecId, 
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        // Validate Access
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        boolean routeAccess = checkRoutePermission(energyCompanyInfoFragment, userContext.getYukonUser());
        modelMap.addAttribute("routeAccess", routeAccess);
        
        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        List<LiteYukonPAObject> availableRoutes = getAvailableRoutes(energyCompany);
        modelMap.addAttribute("availableRoutes", availableRoutes);

        List<LiteSubstation> availableSubstations = getAvailableSubstations(energyCompany);
        modelMap.addAttribute("availableSubstations", availableSubstations);

        return "energyCompany/routesAndSubstations.jsp";
    }

    
    /**
     * This method handles the edit version of the route and substation route controller.
     */
    @RequestMapping
    public String edit(YukonUserContext userContext, ModelMap modelMap, 
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        
        return "energyCompany/routesAndSubstations.jsp";
    }

    /**
     * This method adds a route to the given energy company.
     */
    @RequestMapping(params="addRoute")
    public String addRoute(YukonUserContext userContext, FlashScope flashScope, ModelMap modelMap, 
                           int routeId, EnergyCompanyInfoFragment energyCompanyInfoFragment){

        // Validate Access
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        boolean routeAccess = checkRoutePermission(energyCompanyInfoFragment, userContext.getYukonUser());
        modelMap.addAttribute("routeAccess", routeAccess);
        
        energyCompanyService.addRouteToEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId(), routeId);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.routesAndSubstations.routeAdded"));

        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        return "redirect:view";
    }

    /**
     * This method adds a route to the given energy company.
     */
    @RequestMapping(params="removeRoute")
    public String removeRoute(YukonUserContext userContext, FlashScope flashScope, ModelMap modelMap, 
                              int removeRoute, EnergyCompanyInfoFragment energyCompanyInfoFragment){
        // Validate Access
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        boolean routeAccess = checkRoutePermission(energyCompanyInfoFragment, userContext.getYukonUser());
        modelMap.addAttribute("routeAccess", routeAccess);
        
        energyCompanyService.removeRouteFromEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId(), removeRoute);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.routesAndSubstations.routeRemoved"));

        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        return "redirect:view";
    }

    /**
     * This method adds a substation to the given energy company.
     */
    @RequestMapping(params="addSubstation")
    public String addSubstation(YukonUserContext userContext, FlashScope flashScope, ModelMap modelMap,
                                int substationId, EnergyCompanyInfoFragment energyCompanyInfoFragment){

        // Validate Access
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        Substation substation = substationDao.getById(substationId);
        LiteSubstation liteSubstation = new LiteSubstation(substation);
        
        energyCompany.addSubstation(liteSubstation);
        
        energyCompanyService.addSubstationToEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId(), substationId);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.routesAndSubstations.substationAdded"));
        
        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        return "redirect:view";
    }

    /**
     * This method adds a route to the given energy company.
     */
    @RequestMapping(params="removeSubstation")
    public String removeSubstation(YukonUserContext userContext, FlashScope flashScope, ModelMap modelMap, 
                              int removeSubstation, EnergyCompanyInfoFragment energyCompanyInfoFragment){

        // Validate Access
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        
        energyCompanyService.removeSubstationFromEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId(), removeSubstation);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.routesAndSubstations.substationRemoved"));

        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        return "redirect:view";
    }

    /**
     * This method redirects you to the substation creation page.
     */
    @RequestMapping("createSubstation")
    public String createSubstation(YukonUserContext userContext, ModelMap modelMap) {
        return "redirect:/spring/adminSetup/setup/substations/routeMapping/view";
    }
    
    private void setupModelMap(ModelMap modelMap, EnergyCompanyInfoFragment energyCompanyInfoFragment, YukonUserContext userContext) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);

        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        GeneralInfo generalInfo = generalInfoService.getGeneralInfo(energyCompany);
        modelMap.addAttribute("generalInfo", generalInfo);
        
        List<LiteYukonPAObject> inheritedRoutes = getInheritedRoutes(energyCompany);
        modelMap.addAttribute("inheritedRoutes", inheritedRoutes);
        List<LiteYukonPAObject> ecRoutes = getECRoutes(energyCompany);
        modelMap.addAttribute("ecRoutes", ecRoutes);

        List<LiteSubstation> inheritedSubstations = getInheritedSubstations(energyCompany);
        modelMap.addAttribute("inheritedSubstations", inheritedSubstations);
        List<LiteSubstation> ecSubstations = getECSubstations(energyCompany);
        modelMap.addAttribute("ecSubstations", ecSubstations);

    }

    // Route Helper Methods
    /**
     * This method returns a list of all the available routes that can be selected for the given
     * energy company.
     */
    private List<LiteYukonPAObject> getAvailableRoutes(LiteStarsEnergyCompany energyCompany) {
        List<LiteYukonPAObject> availableRoutes = Lists.newArrayList();
        
        availableRoutes.addAll(Lists.newArrayList(paoDao.getAllLiteRoutes()));
        availableRoutes.removeAll(Lists.newArrayList(energyCompany.getAllRoutes()));

        return availableRoutes;
    }
    
    /**
     * This method returns all the routes the given energy company inherits from its parent.
     */
    private List<LiteYukonPAObject> getInheritedRoutes(LiteStarsEnergyCompany energyCompany) {
        List<LiteYukonPAObject> inheritedRoutes = Lists.newArrayList();
        
        if (energyCompany.getParent() != null) {
            inheritedRoutes.addAll(Lists.newArrayList(energyCompany.getParent().getAllRoutes()));
        }
        
        return inheritedRoutes;
    }
    
    /**
     * This method returns all the routes that are directly associated with the given energy company.
     * These routes can be changed by an energy company unlike the inherited routes
     * that can only be used.
     */
    private List<LiteYukonPAObject> getECRoutes(LiteStarsEnergyCompany energyCompany) {
        List<LiteYukonPAObject> ecRoutes = Lists.newArrayList();
        
        ecRoutes.addAll(Lists.newArrayList(energyCompany.getAllRoutes()));
        
        List<LiteYukonPAObject> inheritedRoutes = getInheritedRoutes(energyCompany);
        ecRoutes.removeAll(inheritedRoutes);
        
        return ecRoutes;
    }

    /**
     * This method verifies that an operator has access to the route select page.  If it does not
     * it will throw a not authorized exception.
     */
    private boolean checkRoutePermission(EnergyCompanyInfoFragment energyCompanyInfoFragment,
                                         LiteYukonUser yukonUser) {
        if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, yukonUser)) return true;

        LiteStarsEnergyCompany energyCompany = 
            starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        
        if (energyCompany.getParent() == null ||
            rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_MEMBER_ROUTE_SELECT, yukonUser)) {
            return true;
        }
        
        return false;
    }
    
    // Substation Helper Methods
    /**
     * This method returns a list of all the available subsations that can be selected for the given
     * energy company.
     */
    private List<LiteSubstation> getAvailableSubstations(LiteStarsEnergyCompany energyCompany) {
        List<LiteSubstation> availableSubstations = Lists.newArrayList();
        
        
        List<Substation> allSubstations = substationDao.getAll();
        
        List<LiteSubstation> allLiteSubstations = 
            Lists.transform(allSubstations, new Function<Substation, LiteSubstation>() {
                @Override
                public LiteSubstation apply(Substation substation) {
                    LiteSubstation liteSubstation = new LiteSubstation();
                    liteSubstation.setSubstationID(substation.getId());
                    liteSubstation.setSubstationName(substation.getName());
                    return liteSubstation;
                }
                
            });
        
        availableSubstations.addAll(allLiteSubstations);
        availableSubstations.removeAll(energyCompany.getAllSubstations());

        return availableSubstations;
    }
    
    /**
     * This method returns all the substations the given energy company inherits from its parent.
     */
    private List<LiteSubstation> getInheritedSubstations(LiteStarsEnergyCompany energyCompany) {
        List<LiteSubstation> inheritedSubstations = Lists.newArrayList();
        
        if (energyCompany.getParent() != null) {
            inheritedSubstations.addAll(energyCompany.getParent().getAllSubstations());
        }
        
        return inheritedSubstations;
    }
    
    /**
     * This method returns all the substations that are directly associated with the given energy company.
     * These substations can be changed by an energy company unlike the inherited substations 
     * that can only be used.
     */
    private List<LiteSubstation> getECSubstations(LiteStarsEnergyCompany energyCompany) {
        List<LiteSubstation> ecSubstations = Lists.newArrayList();
        
        ecSubstations.addAll(energyCompany.getAllSubstations());
        
        List<LiteSubstation> inheritedSubstations = getInheritedSubstations(energyCompany);
        ecSubstations.removeAll(inheritedSubstations);
        
        return ecSubstations;
    }

    // DI Setters
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
    @Autowired
    public void setGeneralInfoService(GeneralInfoService generalInfoService) {
        this.generalInfoService = generalInfoService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setSubstationDao(SubstationDao substationDao) {
        this.substationDao = substationDao;
    }
}