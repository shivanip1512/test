package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMonitorService;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/porterResponseMonitorsWidget/*")
@CheckRoleProperty(YukonRoleProperty.PORTER_RESPONSE_MONITORING)
public class PorterResponseMonitorsWidget extends AdvancedWidgetControllerBase {

    @Autowired private PorterResponseMonitorDao porterResponseMonitorDao;
    @Autowired private PorterResponseMonitorService porterResponseMonitorService;
    @Autowired private OutageEventLogService outageEventLogService;

	@Autowired
    public PorterResponseMonitorsWidget(RoleAndPropertyDescriptionService 
            roleAndPropertyDescriptionService) {
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService
                                         .compile("PORTER_RESPONSE_MONITORING"));
    }
	
	@RequestMapping("render")
	public String render(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		setupRenderModel(model, request);
		return "porterResponseMonitorsWidget/render.jsp";
	}

	@RequestMapping("toggleEnabled")
	public String toggleEnabled(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		int monitorId = WidgetParameterHelper.getRequiredIntParameter(request, "monitorId");
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);

		PorterResponseMonitor monitor = porterResponseMonitorDao.getMonitorById(monitorId);
		MonitorEvaluatorStatus status = monitor.getEvaluatorStatus();
		String widgetError = null;

		try {
			status = porterResponseMonitorService.toggleEnabled(monitorId);
		} catch (NotFoundException e) {
			widgetError = e.getMessage();
		}

		setupRenderModel(model, request);
		model.addAttribute("widgetError", widgetError);

		outageEventLogService.porterResponseMonitorEnableDisable(monitorId, status.name(), userContext.getYukonUser());

		return "porterResponseMonitorsWidget/render.jsp";
	}

	private void setupRenderModel(ModelMap model, HttpServletRequest request) throws Exception {
		List<PorterResponseMonitor> monitors = porterResponseMonitorDao.getAllMonitors();
		model.addAttribute("monitors", monitors);
	}
}
