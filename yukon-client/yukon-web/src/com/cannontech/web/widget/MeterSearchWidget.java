package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.FilterByGenerator;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.widget.support.WidgetControllerBase;

/**
 * Widget used to display basic device information
 */
@CheckRole(YukonRole.METERING)
public class MeterSearchWidget extends WidgetControllerBase {

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ModelAndView mav = new ModelAndView("meterSearchWidget/render.jsp");
        
        List<FilterBy> filterByList = FilterByGenerator.getFilterByList();
        mav.addObject("filterByList", filterByList);

        return mav;
    }

}