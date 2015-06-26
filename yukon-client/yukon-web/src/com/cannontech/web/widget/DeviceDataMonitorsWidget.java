package com.cannontech.web.widget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.monitors.DeviceDataMonitorCacheService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;

@Controller
@RequestMapping("/deviceDataMonitorsWidget/*")
@CheckRoleProperty(YukonRoleProperty.DEVICE_DATA_MONITORING)
public class DeviceDataMonitorsWidget extends AdvancedWidgetControllerBase {

    @Autowired private DeviceDataMonitorCacheService deviceDataMonitorCacheService;
	@Autowired private DeviceDataMonitorService deviceDataMonitorService;

	@Autowired
    public DeviceDataMonitorsWidget(RoleAndPropertyDescriptionService
            roleAndPropertyDescriptionService) {
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService
                                         .compile("DEVICE_DATA_MONITORING"));
    }
	
	@RequestMapping("render")
	public String render(ModelMap model, HttpServletRequest request) throws Exception {
		setupRenderModel(model);
		return "deviceDataMonitorWidget/render.jsp";
	}

	@RequestMapping("toggleEnabled")
	public String toggleEnabled(int monitorId, ModelMap model, YukonUserContext userContext) throws Exception {
		String widgetError = null;

		try {
			deviceDataMonitorService.toggleEnabled(monitorId);
		} catch (NotFoundException e) {
			widgetError = e.getMessage();
		}

		setupRenderModel(model);
		model.addAttribute("widgetError", widgetError);

		return "deviceDataMonitorWidget/render.jsp";
	}

	private void setupRenderModel(ModelMap model) throws Exception {
		List<DeviceDataMonitor> monitors = deviceDataMonitorCacheService.getAllMonitors();
		model.addAttribute("monitors", monitors);
	}
}
