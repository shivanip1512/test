package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.service.StatusPointMonitorService;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@CheckRoleProperty(YukonRoleProperty.STATUS_POINT_MONITORING)
public class StatusPointMonitorsWidget extends AdvancedWidgetControllerBase {

	private StatusPointMonitorDao statusPointMonitorDao;
	private StatusPointMonitorService statusPointMonitorService;
	private OutageEventLogService outageEventLogService;
	
	@RequestMapping
	public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setupRenderModel(model, request);
		return "statusPointMonitorsWidget/render.jsp";
	}
	
	@RequestMapping
	public String toggleEnabled(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
		int statusPointMonitorId = WidgetParameterHelper.getRequiredIntParameter(request, "statusPointMonitorId");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
		StatusPointMonitor statusPointMonitor = statusPointMonitorDao.getStatusPointMonitorById(statusPointMonitorId);
		MonitorEvaluatorStatus status = statusPointMonitor.getEvaluatorStatus();
		String statusPointMonitorsWidgetError = null;
		
        try {
            status = statusPointMonitorService.toggleEnabled(statusPointMonitorId);
        } catch (NotFoundException e) {
        	statusPointMonitorsWidgetError = "yukon.web.widgets.statusPointMonitors.exception.notFound";
        }
        
        setupRenderModel(model, request);
        model.addAttribute("statusPointMonitorsWidgetError", statusPointMonitorsWidgetError);
        
        outageEventLogService.statusPointMonitorEnableDisable(statusPointMonitorId, 
                                                              status.name(), 
                                                              userContext.getYukonUser());
        
        return "statusPointMonitorsWidget/render.jsp";
	}
	
	private void setupRenderModel(ModelMap model, HttpServletRequest request) throws Exception {
	    List<StatusPointMonitor> monitors = statusPointMonitorDao.getAllStatusPointMonitors();
        Collections.sort(monitors);
        model.addAttribute("monitors", monitors);
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
	public void setOutageEventLogService(OutageEventLogService outageEventLogService) {
        this.outageEventLogService = outageEventLogService;
    }
}
