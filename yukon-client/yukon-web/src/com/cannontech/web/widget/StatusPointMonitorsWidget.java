package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointProcessing.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointProcessing.model.StatusPointMonitor;
import com.cannontech.amr.statusPointProcessing.service.StatusPointMonitorService;
import com.cannontech.common.events.loggers.StatusPointMonitorEventLogService;
import com.cannontech.core.dao.StatusPointMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@CheckRoleProperty(YukonRoleProperty.STATUS_POINT_PROCESSING)
public class StatusPointMonitorsWidget extends WidgetControllerBase {

	private StatusPointMonitorDao statusPointMonitorDao;
	private StatusPointMonitorService statusPointMonitorService;
	private StatusPointMonitorEventLogService statusPointMonitorEventLogService;
	
	@Override
	public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView mav = new ModelAndView("statusPointMonitorsWidget/render.jsp");
		
		List<StatusPointMonitor> monitors = statusPointMonitorDao.getAllStatusPointMonitors();
		Collections.sort(monitors);
		mav.addObject("monitors", monitors);
		
		return mav;
	}
	
	public ModelAndView toggleEnabled(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
		int statusPointMonitorId = WidgetParameterHelper.getRequiredIntParameter(request, "statusPointMonitorId");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
		String statusPointMonitorsWidgetError = null;
		StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
		MonitorEvaluatorStatus status = statusPointMonitor.getEvaluatorStatus();
        
        try {
            status = statusPointMonitorService.toggleEnabled(statusPointMonitorId);
        } catch (StatusPointMonitorNotFoundException e) {
        	statusPointMonitorsWidgetError = e.getMessage();
        }
        
        ModelAndView mav = render(request, response);
        mav.addObject("statusPointMonitorsWidgetError", statusPointMonitorsWidgetError);
        
        statusPointMonitorEventLogService.statusPointMonitorEnableDisable(statusPointMonitorId, 
                                                                          status.name(), 
                                                                          userContext.getYukonUser());
        
        return mav;
	}
	
	@Autowired
	public void setStatusPointMonitorDao(StatusPointMonitorDao statusPointMonitorDao) {
		this.statusPointMonitorDao = statusPointMonitorDao;
	}
	
	@Autowired
	public void setStatusPointMonitorService(StatusPointMonitorService statusPointMonitorService) {
		this.statusPointMonitorService = statusPointMonitorService;
	}
	
	@Autowired
	public void setStatusPointMonitorEventLogService(StatusPointMonitorEventLogService statusPointMonitorEventLogService) {
        this.statusPointMonitorEventLogService = statusPointMonitorEventLogService;
    }
}
