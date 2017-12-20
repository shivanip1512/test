package com.cannontech.web.admin.substations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.substations.model.SubstationRouteMapping;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@RequestMapping("/substations/*")
@CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
@Controller
public class SubstationController {

    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private SubstationDao substationDao;
    @Autowired private SubstationToRouteMappingDao strmDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    public final static String BASE_KEY = "yukon.web.modules.adminSetup.substationToRouteMapping.";

    @RequestMapping("routeMapping/view")
    public String view(ModelMap model, HttpServletRequest request, HttpServletResponse response,
            YukonUserContext userContext) throws Exception {
        Integer id = ServletRequestUtils.getIntParameter(request, "substationId");
        boolean hasVendorId = false;
        int vendorId = globalSettingDao.getInteger(GlobalSettingType.MSP_PRIMARY_CB_VENDORID);
        if (vendorId > 0) {
            hasVendorId = true;
        }
        model.addAttribute("hasVendorId", hasVendorId);

        List<Substation> list = substationDao.getAll();
        Collections.sort(list);
        model.addAttribute("list", list);

        List<Route> routeList = Lists.newArrayList();
        List<Route> avList = null;
        if (id == null) {
            id = (list != null && list.size() > 0) ? list.get(0).getId() : null;
        }
        routeList = (id != null) ? strmDao.getRoutesBySubstationId(id) : null;
        avList = (id != null) ? strmDao.getAvailableRoutesBySubstationId(id) : strmDao.getAll();

        model.addAttribute("routeList", routeList);
        model.addAttribute("avlist", avList);
        SubstationRouteMapping substationRouteMapping = new SubstationRouteMapping();
        substationRouteMapping.setSubstationId(id);
        substationRouteMapping.setRouteList(routeList);
        substationRouteMapping.setAvList(avList);
        model.addAttribute("substationRouteMapping", substationRouteMapping);
        return "substations/mappings.jsp";
    }

    @RequestMapping(value = "/routeMapping/save", method = RequestMethod.POST)
    public ModelAndView saveRoutes(HttpServletRequest request, HttpServletResponse response, YukonUserContext userContext,
            FlashScope flashScope, ModelMap modelMap, SubstationRouteMapping substationRouteMapping) {
        if(substationRouteMapping.getSelectedRoutes() != null) {
            strmDao.update(substationRouteMapping.getSubstationId(), substationRouteMapping.getSelectedRoutes());
        } else {
            strmDao.update(substationRouteMapping.getSubstationId(), Collections.emptyList());
        }
        flashScope.setConfirm(new YukonMessageSourceResolvable(BASE_KEY + "routesUpdated"));
        response.setStatus(HttpStatus.NO_CONTENT.value());
        modelMap.addAttribute("substationId", substationRouteMapping.getSubstationId());
        return new ModelAndView("redirect:view", modelMap);
    }

    @RequestMapping(value = "routeMapping/save", params = "addSubstation", method = RequestMethod.POST)
    public String addSubstation(HttpServletRequest request, HttpServletResponse response, YukonUserContext userContext,
            FlashScope flashScope, ModelMap modelMap, SubstationRouteMapping substationRouteMapping) {

        String name = substationRouteMapping.getSubstationName();
        if (!(DeviceGroupUtil.isValidName(name))) {
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.error.deviceGroupName.containsIllegalChars"));
            return "redirect:view";
        }
        Substation substation = new Substation();
        substation.setName(name);
        substationDao.add(substation);
        flashScope.setConfirm(new YukonMessageSourceResolvable(BASE_KEY + "createdSubstation", name));
        return "redirect:view";
    }

    @RequestMapping(value = "routeMapping/save", params = "removeSubstation", method = RequestMethod.POST)
    public String removeSubstation(HttpServletRequest request, HttpServletResponse response,
            YukonUserContext userContext, FlashScope flashScope, ModelMap modelMap,
            SubstationRouteMapping substationRouteMapping) {
        Integer removeSubstation = substationRouteMapping.getSubstationId();
        if (removeSubstation == null) {
            return "redirect:view";
        }

        Substation substation = new Substation();
        substation.setId(removeSubstation);

        Collection<EnergyCompany> allEnergyCompanies = ecDao.getAllEnergyCompanies();
        for (EnergyCompany energyCompany : allEnergyCompanies) {
            energyCompanyService.removeSubstationFromEnergyCompany(energyCompany.getId(), removeSubstation);
        }

        strmDao.removeAllBySubstationId(removeSubstation);
        substationDao.remove(substation);
        
        flashScope.setConfirm(new YukonMessageSourceResolvable(BASE_KEY + "substationDeleted"));
        return "redirect:view";
    }

    /**
     * This method removes a route from the given energy company.
     */
    @RequestMapping(value = "/routeMapping/save", params = "removeRoute", method = RequestMethod.POST)
    public String removeRoute(HttpServletRequest request, HttpServletResponse response, YukonUserContext userContext,
            FlashScope flashScope, ModelMap modelMap, Integer removeRoute, SubstationRouteMapping substationRouteMapping,RedirectAttributes redirectAttributes) {
        strmDao.remove(substationRouteMapping.getSubstationId(), removeRoute);
        flashScope.setConfirm(new YukonMessageSourceResolvable(BASE_KEY + "routeDeleted"));
        redirectAttributes.addAttribute("substationId", substationRouteMapping.getSubstationId());
        return "redirect:view";
    }

}