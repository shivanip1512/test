package com.cannontech.web.multispeak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.multispeak.dao.SubstationToRouteMappingDao;
import com.cannontech.multispeak.db.Route;
import com.cannontech.multispeak.db.Route;

public class RouteActionController extends  MultiActionController {
    private SubstationToRouteMappingDao strmDao;

    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ModelAndView mav = new ModelAndView();
        final Integer id = ServletRequestUtils.getIntParameter(request, "substationid");

        final List routeList = (id != null) ?
                strmDao.getRoutesBySubstationId(id) : Collections.emptyList();
        final List avList = (id != null) ? 
                strmDao.getAvailableRoutesBySubstationId(id) : strmDao.getAll();

        mav.setViewName("routeView.jsp");
        mav.addObject("list", routeList);
        mav.addObject("avlist", avList);
        return mav;
    }

    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer substationId = ServletRequestUtils.getIntParameter(request, "substationid");
        final Integer routeId = ServletRequestUtils.getIntParameter(request, "routeid");
        final Integer ordering = ServletRequestUtils.getIntParameter(request, "ordering");

        if (substationId == null || routeId == null || ordering == null) {
            return view(request, response); 
        }

        strmDao.add(substationId, routeId, ordering);
        return view(request, response);
    }

    public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer substationId = ServletRequestUtils.getIntParameter(request, "substationid");
        final Integer routeId = ServletRequestUtils.getIntParameter(request, "routeid");

        if (substationId == null || routeId == null) {
            return view(request, response);
        }

        strmDao.remove(substationId, routeId);
        return view(request, response);
    }

    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final Integer substationId = ServletRequestUtils.getIntParameter(request, "substationid");
        final String jsonString = ServletRequestUtils.getStringParameter(request, "array");
        if (substationId == null) {
            return view(request, response);
        }            

        final JSONArray array = JSONArray.fromString(jsonString);
        final List<Route> routeList = new ArrayList<Route>(array.length());
        for (int x = 0; x < array.length(); x++) {
            routeList.add(new Route(Integer.parseInt(array.getString(x)), null, (x + 1)));
        }

        strmDao.update(substationId, routeList);
        return view(request,response);
    }

    public void setSubstationToRouteMappingDao(final SubstationToRouteMappingDao strmDao) {
        this.strmDao = strmDao;
    }

}
