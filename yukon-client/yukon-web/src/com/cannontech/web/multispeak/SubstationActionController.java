package com.cannontech.web.multispeak;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.multispeak.dao.SubstationDao;
import com.cannontech.multispeak.dao.SubstationToRouteMappingDao;
import com.cannontech.multispeak.db.Substation;
import com.cannontech.multispeak.db.impl.SubstationImpl;

public class SubstationActionController extends  MultiActionController {
    private SubstationDao substationDao;
    private SubstationToRouteMappingDao strmDao;

    public void setSubstationDao(final SubstationDao substationDao) {
        this.substationDao = substationDao;
    }

    public void setSubstationToRouteMappingDao(final SubstationToRouteMappingDao strmDao) {
        this.strmDao = strmDao;
    }

    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        

        final String name = ServletRequestUtils.getStringParameter(request, "name");
        if (name == null || name.equals("")) {
            mav.setViewName("redirect:mappings");
            return mav;
        }

        final Substation substation = new SubstationImpl();
        substation.setName(name);
        substation.setRouteId(0);

        substationDao.add(substation);
        
        mav.setViewName("redirect:mappings");
        return mav;
    }

    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) {
        return view(request, response);
    }

    public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();

        final Integer id = ServletRequestUtils.getIntParameter(request, "selection_list");
        if (id == null) {
            mav.setViewName("redirect:mappings");
            return mav;
        }

        final Substation substation = new SubstationImpl();
        substation.setId(id);

        strmDao.removeAllBySubstationId(id);
        substationDao.remove(substation);

        mav.setViewName("redirect:mappings");
        return mav;
    }

    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) {
        final ModelAndView mav = new ModelAndView();
        final List<Substation> list = substationDao.getAll();
        mav.setViewName("substationView.jsp");
        mav.addObject("list", list);
        return mav;
    }

}
