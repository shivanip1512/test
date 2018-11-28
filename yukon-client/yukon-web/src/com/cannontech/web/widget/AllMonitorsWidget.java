package com.cannontech.web.widget;

import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.base.Joiner;


@Controller
@RequestMapping("/allMonitorsWidget/*")
@CheckRoleProperty({YukonRoleProperty.DEVICE_DATA_MONITORING,
                    YukonRoleProperty.OUTAGE_PROCESSING,
                    YukonRoleProperty.TAMPER_FLAG_PROCESSING,
                    YukonRoleProperty.STATUS_POINT_MONITORING,
                    YukonRoleProperty.PORTER_RESPONSE_MONITORING,
                    YukonRoleProperty.VALIDATION_ENGINE,
                    })

public class AllMonitorsWidget extends AdvancedWidgetControllerBase {

    @Autowired protected MonitorCacheService monitorCacheService;
    @Autowired private DeviceGroupService deviceGroupService;

    public AllMonitorsWidget() {
    }
    
    @Autowired
    public AllMonitorsWidget(RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        String checkRole = Joiner.on(" ").join(YukonRoleProperty.DEVICE_DATA_MONITORING.name(),
                                               YukonRoleProperty.OUTAGE_PROCESSING.name(),
                                               YukonRoleProperty.TAMPER_FLAG_PROCESSING.name(),
                                               YukonRoleProperty.STATUS_POINT_MONITORING.name(),
                                               YukonRoleProperty.PORTER_RESPONSE_MONITORING.name(),
                                               YukonRoleProperty.VALIDATION_ENGINE.name());
        
        setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(checkRole));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request) {
        putMonitorsInModel(model, request);
        return "allMonitorsWidget/render.jsp";
    }

    protected void putMonitorsInModel(ModelMap model, HttpServletRequest request) {
        sortMonitorsAndAddToModel(monitorCacheService.getDeviceDataMonitors(), monitorCacheService.getOutageMonitors(),
            monitorCacheService.getTamperFlagMonitors(), monitorCacheService.getStatusPointMonitors(),
            monitorCacheService.getPorterResponseMonitors(), monitorCacheService.getValidationMonitors(), model);

        if (CollectionUtils.isNotEmpty(monitorCacheService.getDeviceDataMonitors())) {
            setSmartNotificationsEvent(SmartNotificationEventType.DEVICE_DATA_MONITOR);
        } else {
            setSmartNotificationsEvent(null);
        }
    }

    protected void sortMonitorsAndAddToModel(List<DeviceDataMonitor> deviceDataMonitors,
            List<OutageMonitor> outageMonitors, List<TamperFlagMonitor> tamperFlagMonitors,
            List<StatusPointMonitor> statusPointMonitors, List<PorterResponseMonitor> porterResponseMonitors,
            List<ValidationMonitor> validationMonitors, ModelMap model) {
        
        deviceDataMonitors.sort(Comparator.comparing(DeviceDataMonitor::getName));
        model.addAttribute("deviceDataMonitors", deviceDataMonitors);
        
        outageMonitors.sort(Comparator.comparing(OutageMonitor::getName));
        model.addAttribute("outageMonitors", outageMonitors);
        
        tamperFlagMonitors.sort(Comparator.comparing(TamperFlagMonitor::getName));
        model.addAttribute("tamperFlagGroupBase", deviceGroupService.getFullPath(SystemGroupEnum.TAMPER_FLAG));
        model.addAttribute("tamperFlagMonitors", tamperFlagMonitors);
        
        statusPointMonitors.sort(Comparator.comparing(StatusPointMonitor::getName));
        model.addAttribute("statusPointMonitors", statusPointMonitors);
        
        porterResponseMonitors.sort(Comparator.comparing(PorterResponseMonitor::getName));
        model.addAttribute("porterResponseMonitors", porterResponseMonitors);
        
        validationMonitors.sort(Comparator.comparing(ValidationMonitor::getName));
        model.addAttribute("validationMonitors", validationMonitors);  
    }
}
