package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.StandardFilterByGenerator;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.amr.meter.service.MspMeterSearchService;
import com.cannontech.web.widget.support.WidgetControllerBase;

/**
 * Widget used to display basic device information
 */
@Controller
@RequestMapping("/meterSearchWidget/*")
public class MeterSearchWidget extends WidgetControllerBase {
    
    private MspMeterSearchService mspMeterSearchService;
    
    @Autowired
    public MeterSearchWidget(RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        String checkRole = YukonRole.METERING.name();
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }

    @RequestMapping("render")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
        ModelAndView mav = new ModelAndView("meterSearchWidget/render.jsp");
        
        // all filters
        List<FilterBy> filterByList = new ArrayList<FilterBy>();
        filterByList.addAll(StandardFilterByGenerator.getStandardFilterByList());
        filterByList.addAll(mspMeterSearchService.getMspFilterByList());
        
        mav.addObject("filterByList", filterByList);

        return mav;
    }
    
    
    @Autowired
    public void setMspMeterSearchService(MspMeterSearchService mspMeterSearchService) {
		this.mspMeterSearchService = mspMeterSearchService;
	}
}