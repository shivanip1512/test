package com.cannontech.web.admin.substations;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

@RequestMapping("/substations/*")
@CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
@Controller
public class SubstationController { 
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private SubstationDao substationDao;
    @Autowired private SubstationToRouteMappingDao strmDao;
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private GlobalSettingDao globalSettingDao;
    private TypeReference<List<Integer>> integerListType = new TypeReference<List<Integer>>() {/*Jackson requires*/};

    /**
     * @param request unused
     * @param response unused
     */
    @RequestMapping("routeMapping/view")
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("substations/mappings.jsp");
        
        return mav;
    }

    /**
     * @param response unused
     */
    @RequestMapping("routeMapping/viewRoute")
    public ModelAndView viewRoute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getIntParameter(request, "substationid");

        List<Route> routeList = Lists.newArrayList();
        if (id != null) {
            routeList = strmDao.getRoutesBySubstationId(id);
        }
        final List<Route> avList = (id != null) ? 
                strmDao.getAvailableRoutesBySubstationId(id) : strmDao.getAll();

        mav.setViewName("substations/routeView.jsp");
        mav.addObject("list", routeList);
        mav.addObject("avlist", avList);
        return mav;
    }

    /**
     * @param request unused
     * @param response unused
     */
    @RequestMapping("routeMapping/viewSubstation")
    public ModelAndView viewSubstation(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView();
        
        boolean hasVendorId = false;
        int vendorId = globalSettingDao.getInteger(GlobalSettingType.MSP_PRIMARY_CB_VENDORID);
        if (vendorId > 0) {
            hasVendorId = true;
        }
        mav.addObject("hasVendorId", hasVendorId);
        
        List<Substation> list = substationDao.getAll();
        Collections.sort(list, new SubstationComparator());
        
        mav.setViewName("substations/substationView.jsp");
        mav.addObject("list", list);
        return mav;
    }
    
    @RequestMapping("routeMapping/addRoute")
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer substationId = ServletRequestUtils.getIntParameter(request, "substationid");
        final Integer routeId = ServletRequestUtils.getIntParameter(request, "routeid");
        final Integer ordering = ServletRequestUtils.getIntParameter(request, "ordering");

        if (substationId == null || routeId == null || ordering == null) {
            return viewRoute(request, response); 
        }

        strmDao.add(substationId, routeId, ordering);
        return viewRoute(request, response);
    }

    @RequestMapping("routeMapping/removeRoute")
    public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer substationId = ServletRequestUtils.getIntParameter(request, "substationid");
        final Integer routeId = ServletRequestUtils.getIntParameter(request, "routeid");

        if (substationId == null || routeId == null) {
            return viewRoute(request, response);
        }

        strmDao.remove(substationId, routeId);
        return viewRoute(request, response);
    }

    @RequestMapping("routeMapping/update")
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer substationId = ServletRequestUtils.getIntParameter(request, "substationid");
        final String jsonString = ServletRequestUtils.getStringParameter(request, "array");
        if (substationId == null) {
            return viewRoute(request, response);
        }            
        List<Integer> routeIdList = JsonUtils.fromJson(jsonString, integerListType);

        strmDao.update(substationId, routeIdList);
        return viewRoute(request,response);
    }

    /**
     * @param response unused
     */
    @RequestMapping(value="routeMapping/edit", params="add")
    public ModelAndView addSubstation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("redirect:view");

        String name = ServletRequestUtils.getStringParameter(request, "name");
        if (StringUtils.isBlank(name)) {
            return mav;
        }

        Substation substation = new Substation();
        substation.setName(name);
        substationDao.add(substation);
        
        return mav;
    }

    @RequestMapping("routeMapping/updateSubstation")
    public ModelAndView updateSubstation(HttpServletRequest request, HttpServletResponse response) {
        return viewSubstation(request, response);
    }

    /**
     * @param response unused
     */
    @RequestMapping(value="routeMapping/edit", params="remove")
    public ModelAndView removeSubstation(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("redirect:view");

        Integer id = ServletRequestUtils.getIntParameter(request, "selection_list");
        if (id == null) {
            return mav;
        }

        Substation substation = new Substation();
        substation.setId(id);

        Collection<EnergyCompany> allEnergyCompanies = yukonEnergyCompanyService.getAllEnergyCompanies();
        for (EnergyCompany energyCompany : allEnergyCompanies) {
            energyCompanyService.removeSubstationFromEnergyCompany(energyCompany.getId(), id);
        }
        
        strmDao.removeAllBySubstationId(id);
        substationDao.remove(substation);

        return mav;
    }

    /**
     * @param request unused
     * @param response unused
     */
    @RequestMapping("routeMapping/removeAll")
    public ModelAndView removeAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("redirect:view");

        List<Substation> currentSubstations = substationDao.getAll();
        for (Substation currentSubstation : currentSubstations) {

            strmDao.removeAllBySubstationId(currentSubstation.getId());
            substationDao.remove(currentSubstation);
        }

        return mav;
    }
    
    private class SubstationComparator implements Comparator<Substation> {
        @Override
        public int compare(Substation o1, Substation o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };
}
