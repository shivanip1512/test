package com.cannontech.web.widget;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.pao.PaoType;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.google.common.collect.Sets;

/**
 * Widget used to display the system actions available.
 */
@Controller
@RequestMapping("/systemActionsMenuWidget")
public class SystemActionsMenuWidget extends WidgetControllerBase {

    @Autowired private ServerDatabaseCache serverDatabaseCache;
    
    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("systemActionsMenuWidget/render.jsp");
        boolean hasRolePropertyToShowWaterLeak = false;
        Set<PaoType> hasWaterMeters = Sets.intersection(serverDatabaseCache.getAllPaoTypes(), PaoType.getWaterMeterTypes());
        if (!hasWaterMeters.isEmpty()) {
            hasRolePropertyToShowWaterLeak = true;
        }
        mav.addObject("showWaterLeak", hasRolePropertyToShowWaterLeak);
        return mav;
    }

}
