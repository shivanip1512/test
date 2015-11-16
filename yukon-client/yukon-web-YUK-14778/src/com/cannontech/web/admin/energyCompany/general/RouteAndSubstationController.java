package com.cannontech.web.admin.energyCompany.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.model.Substation;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteSubstation;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.DefaultRouteService;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.admin.energyCompany.general.model.EnergyCompanyInfoFragment;
import com.cannontech.web.admin.energyCompany.general.model.GeneralInfo;
import com.cannontech.web.admin.energyCompany.general.service.GeneralInfoService;
import com.cannontech.web.admin.energyCompany.service.EnergyCompanyInfoFragmentHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


@RequestMapping("/energyCompany/routesAndSubstations/*")
@Controller
public class RouteAndSubstationController { 
    private final Logger log = YukonLogManager.getLogger(RouteAndSubstationController.class);

    public enum Reason implements DisplayableEnum {
        CAN_DELETE(true),
        THIS_DEFAULT(false),
        CHILD_DEFAULT(false),
        INHERITED(false),
        ;
        
        private final boolean deletable;

        private Reason(boolean deletable) {
            this.deletable = deletable;
        }
        
        public boolean isDeletable() {
            return deletable;
        }
        
        @Override
        public String getFormatKey() {
            return "yukon.web.modules.adminSetup.routesAndSubstations.routeReasons." + name();
        }
    }

    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private DefaultRouteService defaultRouteService;
    @Autowired private GeneralInfoService generalInfoService;
    @Autowired private PaoDao paoDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private SubstationDao substationDao;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;

    /**
     * This method handles the view version of the route and substation controller.
     */
    @RequestMapping("view")
    public String view(YukonUserContext userContext, ModelMap modelMap, int ecId, 
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        // Validate Access
        energyCompanyService.verifyViewPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        boolean routeAccess = checkRoutePermission(energyCompanyInfoFragment, userContext.getYukonUser());
        modelMap.addAttribute("routeAccess", routeAccess);
        
        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.VIEW);
        
        LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId());
        EnergyCompany energyCompany = ecDao.getEnergyCompany(lsec.getEnergyCompanyId());
        
        List<LiteYukonPAObject> availableRoutes = getAvailableRoutes(energyCompany);
        modelMap.addAttribute("availableRoutes", availableRoutes);

        List<LiteSubstation> availableSubstations = getAvailableSubstations(lsec);
        modelMap.addAttribute("availableSubstations", availableSubstations);

        return "energyCompany/routesAndSubstations.jsp";
    }

    
    /**
     * This method handles the edit version of the route and substation route controller.
     */
    @RequestMapping("edit")
    public String edit(YukonUserContext userContext, ModelMap modelMap, 
                       EnergyCompanyInfoFragment energyCompanyInfoFragment) {
        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        modelMap.addAttribute("mode", PageEditMode.EDIT);
        
        return "energyCompany/routesAndSubstations.jsp";
    }

    /**
     * This method adds a route to the given energy company.
     */
    @RequestMapping(value="edit", params="addRoute")
    public String addRoute(YukonUserContext userContext, FlashScope flashScope, ModelMap modelMap, 
                           int routeId, EnergyCompanyInfoFragment energyCompanyInfoFragment){

        // Validate Access
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());
        
        energyCompanyService.addRouteToEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId(), routeId);
        flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.routesAndSubstations.routeAdded"));

        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        return "redirect:view";
    }

    /**
     * This method removes a route from the given energy company.
     */
    @RequestMapping(value="edit", params="removeRoute")
    public String removeRoute(YukonUserContext userContext, FlashScope flashScope, ModelMap modelMap, 
                              int removeRoute, EnergyCompanyInfoFragment energyCompanyInfoFragment){
        // Validate Access
        energyCompanyService.verifyEditPageAccess(userContext.getYukonUser(), energyCompanyInfoFragment.getEnergyCompanyId());

        int numberRoutesRemoved = energyCompanyService.removeRouteFromEnergyCompany(energyCompanyInfoFragment.getEnergyCompanyId(), removeRoute);
        if (numberRoutesRemoved != 1) {
        	log.error("Attempting to remove route id: " + removeRoute + " failed. Number of routes removed: " + numberRoutesRemoved);
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.routesAndSubstations.routeRemovedError"));
        } else {
        	flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.adminSetup.routesAndSubstations.routeRemoved"));
        }

        setupModelMap(modelMap, energyCompanyInfoFragment, userContext);
        return "redirect:view";
    }

    /**
     * This method adds a substation to the given energy company.
     */
    @RequestMapping(value="edit", params="addSubstation")
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
    @RequestMapping(value="edit", params="removeSubstation")
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
        return "redirect:/admin/substations/routeMapping/view";
    }
    
    private void setupModelMap(ModelMap modelMap, EnergyCompanyInfoFragment energyCompanyInfoFragment,
                               YukonUserContext userContext) {
        EnergyCompanyInfoFragmentHelper.setupModelMapBasics(energyCompanyInfoFragment, modelMap);
        int ecId = energyCompanyInfoFragment.getEnergyCompanyId();
        LiteStarsEnergyCompany lsec = starsDatabaseCache.getEnergyCompany(ecId);
        EnergyCompany energyCompany = ecDao.getEnergyCompany(ecId);
        
        GeneralInfo generalInfo = generalInfoService.getGeneralInfo(energyCompany);
        modelMap.addAttribute("generalInfo", generalInfo);
        
        Iterable<LiteYukonPAObject> allRoutes = ecDao.getAllRoutes(energyCompany);
        Map<LiteYukonPAObject, Reason> routeToReason = Maps.newLinkedHashMap();
        for (LiteYukonPAObject liteYukonPAObject : allRoutes) {
            routeToReason.put(liteYukonPAObject, Reason.CAN_DELETE);
        }
        
        if (energyCompany.getParent() != null) {
            Iterable<LiteYukonPAObject> ineritedRoutes = ecDao.getAllRoutes(energyCompany.getParent());
            for (LiteYukonPAObject liteYukonPAObject : ineritedRoutes) {
                routeToReason.put(liteYukonPAObject, Reason.INHERITED);
            }
        }
        
        LiteYukonPAObject defaultRoute = defaultRouteService.getDefaultRoute(energyCompany);
        if (defaultRoute != null) {
            routeToReason.put(defaultRoute, Reason.THIS_DEFAULT);
        }
        
        Iterable<LiteYukonPAObject> allChildDefaultRoutes = getAllChildDefaultRoutes(energyCompany);
        for (LiteYukonPAObject childDefault : allChildDefaultRoutes) {
            routeToReason.put(childDefault, Reason.CHILD_DEFAULT);
        }

        modelMap.addAttribute("isSingleEnergyCompany", energyCompanySettingDao.getBoolean(EnergyCompanySettingType.SINGLE_ENERGY_COMPANY, energyCompany.getEnergyCompanyId()));
        modelMap.addAttribute("ecRoutes", routeToReason);

        List<LiteSubstation> inheritedSubstations = getInheritedSubstations(lsec);
        modelMap.addAttribute("inheritedSubstations", inheritedSubstations);
        List<LiteSubstation> ecSubstations = getECSubstations(lsec);
        modelMap.addAttribute("ecSubstations", ecSubstations);
    }

    // Route Helper Methods
    /**
     * This method returns a list of all the available routes that can be selected for the given
     * energy company.
     */
    private List<LiteYukonPAObject> getAvailableRoutes(EnergyCompany energyCompany) {
        List<LiteYukonPAObject> availableRoutes = new ArrayList<>();
        availableRoutes.addAll(Lists.newArrayList(paoDao.getAllLiteRoutes()));
        availableRoutes.removeAll(ecDao.getAllRoutes(energyCompany));
        return availableRoutes;
    }
    
    private Set<LiteYukonPAObject> getAllChildDefaultRoutes(EnergyCompany energyCompany) {
        Iterable<EnergyCompany> children = energyCompany.getChildren();
        if (Iterables.isEmpty(children)) {
            return ImmutableSet.of();
        }
        ImmutableSet.Builder<LiteYukonPAObject> builder = ImmutableSet.builder();
        for (EnergyCompany child : children) {
            LiteYukonPAObject defaultRoute = defaultRouteService.getDefaultRoute(energyCompany);
            if (defaultRoute != null) {
                builder.add(defaultRoute);
            }
            builder.addAll(getAllChildDefaultRoutes(child));
        }
        return builder.build();
    }
    
    /**
     * This method verifies that an operator has access to the route select page.  If it does not
     * it will throw a not authorized exception.
     */
    private boolean checkRoutePermission(EnergyCompanyInfoFragment energyCompanyInfoFragment,
                                         LiteYukonUser yukonUser) {
        if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.ADMIN_SUPER_USER, yukonUser)) {
            return true;
        }

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
}