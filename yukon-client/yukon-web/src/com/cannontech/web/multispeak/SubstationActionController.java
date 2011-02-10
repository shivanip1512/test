package com.cannontech.web.multispeak;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.model.Substation;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.ADMIN_MULTISPEAK_SETUP)
public class SubstationActionController extends  MultiActionController {
    
    private SubstationDao substationDao;
    private SubstationToRouteMappingDao strmDao;
    private RolePropertyDao rolePropertyDao;

    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView mav = new ModelAndView("redirect:mappings");

        String name = ServletRequestUtils.getStringParameter(request, "name");
        if (StringUtils.isBlank(name)) {
            return mav;
        }

        Substation substation = new Substation();
        substation.setName(name);
        substation.setRouteId(0);
        substationDao.add(substation);
        
        return mav;
    }

    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) {
        return view(request, response);
    }

    public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("redirect:mappings");

        Integer id = ServletRequestUtils.getIntParameter(request, "selection_list");
        if (id == null) {
            return mav;
        }

        Substation substation = new Substation();
        substation.setId(id);

        strmDao.removeAllBySubstationId(id);
        substationDao.remove(substation);

        return mav;
    }
    
    public ModelAndView removeAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("redirect:mappings");

        List<Substation> currentSubstations = substationDao.getAll();
        for (Substation currentSubstation : currentSubstations) {

            strmDao.removeAllBySubstationId(currentSubstation.getId());
            substationDao.remove(currentSubstation);
        }

        return mav;
    }
    
    

    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        
        ModelAndView mav = new ModelAndView();
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        boolean hasVendorId = false;
        int vendorId = rolePropertyDao.getPropertyIntegerValue(YukonRoleProperty.MSP_PRIMARY_CB_VENDORID, userContext.getYukonUser());
        if (vendorId > 0) {
            hasVendorId = true;
        }
        mav.addObject("hasVendorId", hasVendorId);
        
        List<Substation> list = substationDao.getAll();
        Collections.sort(list, new SubstationComparator());
        
        mav.setViewName("setup/routemapping/substationView.jsp");
        mav.addObject("list", list);
        return mav;
    }
    
    private class SubstationComparator implements Comparator<Substation> {
        
        public int compare(Substation o1, Substation o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    };

    @Autowired
    public void setSubstationDao(final SubstationDao substationDao) {
        this.substationDao = substationDao;
    }

    @Autowired
    public void setSubstationToRouteMappingDao(final SubstationToRouteMappingDao strmDao) {
        this.strmDao = strmDao;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
}
