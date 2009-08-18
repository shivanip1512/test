package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;

@CheckRoleProperty(YukonRoleProperty.OUTAGE_PROCESSING)
public class OutageMonitorsWidget extends WidgetControllerBase {

	private OutageMonitorDao outageMonitorDao;
	private OutageMonitorService outageMonitorService;
	
	@Override
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("outageMonitorsWidget/render.jsp");
		
		List<OutageMonitor> monitors = outageMonitorDao.getAll();
		mav.addObject("monitors", monitors);
		
		return mav;
	}
	
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        int outageMonitorId = ServletRequestUtils.getRequiredIntParameter(request, "outageMonitorsWidget_deleteOutageMonitorId");
        
        String outageMonitorsWidgetError = null;
        
        try {
        	outageMonitorService.deleteOutageMonitor(outageMonitorId);
        } catch (OutageMonitorNotFoundException e) {
        	outageMonitorsWidgetError = e.getMessage();
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
