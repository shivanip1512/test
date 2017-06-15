package com.cannontech.web.amr.monitor;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMonitorService;
import com.cannontech.amr.statusPointMonitoring.service.StatusPointMonitorService;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.validation.service.ValidationMonitorService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
public class AllMonitorsController {

    @Autowired private DeviceDataMonitorService deviceDataMonitorService;
    @Autowired private OutageMonitorService outageMonitorService;
    @Autowired private TamperFlagMonitorService tamperFlagMonitorService;
    @Autowired private StatusPointMonitorService statusPointMonitorService;
    @Autowired private PorterResponseMonitorService porterResponseMonitorService;
    @Autowired private ValidationMonitorService validationMonitorService;

    @CheckRoleProperty(YukonRoleProperty.DEVICE_DATA_MONITORING)
    @RequestMapping(value = "/device-data-monitor/{monitorId}/toggle", method = RequestMethod.POST)
    public void toggleDeviceData(HttpServletResponse resp, @PathVariable int monitorId) {
        deviceDataMonitorService.toggleEnabled(monitorId);
        resp.setStatus(HttpStatus.NO_CONTENT.value());

    }

    @CheckRoleProperty(YukonRoleProperty.OUTAGE_PROCESSING)
    @RequestMapping(value = "/outage-monitor/{outageMonitorId}/toggle", method = RequestMethod.POST)
    public void toggleOutage(HttpServletResponse resp, @PathVariable int outageMonitorId) {
        outageMonitorService.toggleEnabled(outageMonitorId);
        resp.setStatus(HttpStatus.NO_CONTENT.value());

    }
    
    @CheckRoleProperty(YukonRoleProperty.STATUS_POINT_MONITORING)
    @RequestMapping(value = "/status-point-monitor/{statusPointMonitorId}/toggle", method = RequestMethod.POST)
    public void toggleStatusPoint(HttpServletResponse resp, @PathVariable int statusPointMonitorId) {
        statusPointMonitorService.toggleEnabled(statusPointMonitorId);
        resp.setStatus(HttpStatus.NO_CONTENT.value());

    }
    
    @CheckRoleProperty(YukonRoleProperty.TAMPER_FLAG_PROCESSING)
    @RequestMapping(value = "/tamper-flag-monitor/{tamperFlagMonitorId}/toggle", method = RequestMethod.POST)
    public void toggleTamperFlag(HttpServletResponse resp, @PathVariable int tamperFlagMonitorId) {
        tamperFlagMonitorService.toggleEnabled(tamperFlagMonitorId);
        resp.setStatus(HttpStatus.NO_CONTENT.value());

    }
    @CheckRoleProperty(YukonRoleProperty.PORTER_RESPONSE_MONITORING)
    @RequestMapping(value = "/porter-response-monitor/{monitorId}/toggle", method = RequestMethod.POST)
    public void togglePorterResponse(HttpServletResponse resp, @PathVariable int monitorId) {
        porterResponseMonitorService.toggleEnabled(monitorId);
        resp.setStatus(HttpStatus.NO_CONTENT.value());

    }
    @CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
    @RequestMapping(value = "/validation-monitor/{validationMonitorId}/toggle", method = RequestMethod.POST)
    public void toggleValidation(HttpServletResponse resp, @PathVariable int validationMonitorId) {
        validationMonitorService.toggleEnabled(validationMonitorId);
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
}
