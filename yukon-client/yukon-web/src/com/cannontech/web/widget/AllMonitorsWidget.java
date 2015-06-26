package com.cannontech.web.widget;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.deviceDataMonitor.dao.DeviceDataMonitorDao;
import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMonitorService;
import com.cannontech.amr.statusPointMonitoring.dao.StatusPointMonitorDao;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.statusPointMonitoring.service.StatusPointMonitorService;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.events.loggers.OutageEventLogService;
import com.cannontech.common.validation.dao.ValidationMonitorDao;
import com.cannontech.common.validation.dao.ValidationMonitorNotFoundException;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.common.validation.service.ValidationMonitorService;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;


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

    @Autowired protected DeviceDataMonitorDao deviceDataMonitorDao;
    @Autowired private DeviceDataMonitorService deviceDataMonitorService;
    @Autowired protected OutageMonitorDao outageMonitorDao;
    @Autowired private OutageMonitorService outageMonitorService;
    @Autowired protected TamperFlagMonitorDao tamperFlagMonitorDao;
    @Autowired private TamperFlagMonitorService tamperFlagMonitorService;
    @Autowired protected StatusPointMonitorDao statusPointMonitorDao;
    @Autowired private StatusPointMonitorService statusPointMonitorService;
    @Autowired protected PorterResponseMonitorDao porterResponseMonitorDao;
    @Autowired private PorterResponseMonitorService porterResponseMonitorService;
    @Autowired protected ValidationMonitorDao validationMonitorDao;
    @Autowired private ValidationMonitorService validationMonitorService;
    @Autowired private OutageEventLogService outageEventLogService;
    
    public AllMonitorsWidget() {
    }
    
    @Autowired
    public AllMonitorsWidget(RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        
        this.setIdentityPath("common/deviceIdentity.jsp");
        this.setRoleAndPropertiesChecker(roleAndPropertyDescriptionService.compile(
                "DEVICE_DATA_MONITORING OUTAGE_PROCESSING TAMPER_FLAG_PROCESSING "
                + "STATUS_POINT_MONITORING PORTER_RESPONSE_MONITORING VALIDATION_ENGINE"));
    }
    
    @RequestMapping("render")
    public String render(ModelMap model, YukonUserContext context) {
        putMonitorsInModel(model, context);
        return "allMonitorsWidget/render.jsp";
    }

    protected void putMonitorsInModel(ModelMap model, YukonUserContext context) {
        List<DeviceDataMonitor> deviceDataMonitors = deviceDataMonitorDao.getAllMonitors();
        Collections.sort(deviceDataMonitors);
        model.addAttribute("deviceDataMonitors", deviceDataMonitors);
        
        List<OutageMonitor> outageMonitors = outageMonitorDao.getAll();
        Collections.sort(outageMonitors);
        model.addAttribute("outageMonitors", outageMonitors);
        
        List<TamperFlagMonitor> tamperFlagMonitors = tamperFlagMonitorDao.getAll();
        Collections.sort(tamperFlagMonitors);
        model.addAttribute("tamperFlagMonitors", tamperFlagMonitors);
        
        List<StatusPointMonitor> statusPointMonitors = statusPointMonitorDao.getAllStatusPointMonitors();
        Collections.sort(statusPointMonitors);
        model.addAttribute("statusPointMonitors", statusPointMonitors);
        
        List<PorterResponseMonitor> porterResponseMonitors = porterResponseMonitorDao.getAllMonitors();
        Collections.sort(porterResponseMonitors);
        model.addAttribute("porterResponseMonitors", porterResponseMonitors);
        
        List<ValidationMonitor> validationMonitors = validationMonitorDao.getAll();
        Collections.sort(validationMonitors);
        model.addAttribute("validationMonitors", validationMonitors);
        
    }

    @RequestMapping("toggleEnabledDeviceData")
    public String toggleEnabledDeviceData(int monitorId, ModelMap model, YukonUserContext context) {
        
        String widgetError = null;

        try {
            deviceDataMonitorService.toggleEnabled(monitorId);
        } catch (NotFoundException e) {
            widgetError = e.getMessage();
        }

        model.addAttribute("widgetError", widgetError);

        return render(model, context);
    }
    
    @RequestMapping("toggleEnabledOutage")
    public String toggleEnabledOutage(int outageMonitorId, ModelMap model, YukonUserContext context) {
        
        String outageMonitorsWidgetError = null;
        
        try {
            outageMonitorService.toggleEnabled(outageMonitorId);
        } catch (OutageMonitorNotFoundException e) {
            outageMonitorsWidgetError = "yukon.web.widgets.outageMonitorsWidget.exception.notFound";
        }
        
        model.addAttribute("outageMonitorsWidgetError", outageMonitorsWidgetError);
        
        return render(model, context);
    }
    
    @RequestMapping("toggleEnabledStatusPoint")
    public String toggleEnabledStatusPoint(int statusPointMonitorId, ModelMap model, YukonUserContext context) throws Exception {
        
        String statusPointMonitorsWidgetError = null;
        
        try {
            statusPointMonitorService.toggleEnabled(statusPointMonitorId);
        } catch (NotFoundException e) {
            statusPointMonitorsWidgetError = "yukon.web.widgets.statusPointMonitorsWidget.exception.notFound";
        }
        
        model.addAttribute("statusPointMonitorsWidgetError", statusPointMonitorsWidgetError);
        
        return render(model, context);
    }
    
    @RequestMapping("toggleEnabledTamperFlag")
    public String toggleEnabledTamperFlag(int tamperFlagMonitorId, ModelMap model, YukonUserContext context) {
        
        String tamperFlagMonitorsWidgetError = null;
        
        try {
            tamperFlagMonitorService.toggleEnabled(tamperFlagMonitorId);
        } catch (TamperFlagMonitorNotFoundException e) {
            tamperFlagMonitorsWidgetError = e.getMessage();
        }
        
        model.addAttribute("tamperFlagMonitorsWidgetError", tamperFlagMonitorsWidgetError);
        
        return render(model, context);
    }
    
    @RequestMapping("toggleEnabledPorterResponse")
    public String toggleEnabledPorterResponse(int monitorId, ModelMap model, YukonUserContext context) {

        MonitorEvaluatorStatus status = porterResponseMonitorDao.getMonitorById(monitorId).getEvaluatorStatus();
        
        String widgetError = null;

        try {
            status = porterResponseMonitorService.toggleEnabled(monitorId);
        } catch (NotFoundException e) {
            widgetError = e.getMessage();
        }

        model.addAttribute("widgetError", widgetError);

        outageEventLogService.porterResponseMonitorEnableDisable(monitorId, status.name(), context.getYukonUser());

        return render(model, context);
    }

    @RequestMapping("toggleEnabledValidation")
    public String toggleEnabledValidation(int validationMonitorId, ModelMap model, YukonUserContext context) {
        
        String validationMonitorsWidgetError = null;
        
        try {
            validationMonitorService.toggleEnabled(validationMonitorId);
        } catch (ValidationMonitorNotFoundException e) {
            validationMonitorsWidgetError = e.getMessage();
        }
        
        model.addAttribute("validationMonitorsWidgetError", validationMonitorsWidgetError);
        
        return render(model, context);
    }
}
