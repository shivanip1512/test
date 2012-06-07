package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@CheckRoleProperty(YukonRoleProperty.OUTAGE_PROCESSING)
public class OutageMonitorsWidget extends WidgetControllerBase {

	private OutageMonitorDao outageMonitorDao;
	private OutageMonitorService outageMonitorService;
	
	@Override
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("outageMonitorsWidget/render.jsp");
		
		List<OutageMonitor> monitors = outageMonitorDao.getAll();
		Collections.sort(monitors);
		mav.addObject("monitors", monitors);
		
		return mav;
	}
	
	public ModelAndView toggleEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int outageMonitorId = WidgetParameterHelper.getRequiredIntParameter(request, "outageMonitorId");
        
        String outageMonitorsWidgetError = null;
        
        try {
        	outageMonitorService.toggleEnabled(outageMonitorId);
        } catch (OutageMonitorNotFoundException e) {
        	outageMonitorsWidgetError = "yukon.web.widgets.outageMonitorsWidget.exception.notFound";
        }
        
        ModelAndView mav = render(request, response);
        mav.addObject("outageMonitorsWidgetError", outageMonitorsWidgetError);
        
        return mav;
	}
	
	@Autowired
	public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
		this.outageMonitorDao = outageMonitorDao;
	}
	
	@Autowired
	public void setOutageMonitorService(OutageMonitorService outageMonitorService) {
		this.outageMonitorService = outageMonitorService;
	}
}
