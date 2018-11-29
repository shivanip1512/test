package com.cannontech.web.amr.monitor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.amr.porterResponseMonitor.service.PorterResponseMonitorService;
import com.cannontech.amr.statusPointMonitoring.service.StatusPointMonitorService;
import com.cannontech.amr.tamperFlagProcessing.service.TamperFlagMonitorService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.validation.service.ValidationMonitorService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
public class AllMonitorsController {

    @Autowired private DeviceDataMonitorService deviceDataMonitorService;
    @Autowired private OutageMonitorService outageMonitorService;
    @Autowired private TamperFlagMonitorService tamperFlagMonitorService;
    @Autowired private StatusPointMonitorService statusPointMonitorService;
    @Autowired private PorterResponseMonitorService porterResponseMonitorService;
    @Autowired private ValidationMonitorService validationMonitorService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private static final String baseKey = "yukon.web.modules.amr";

    @CheckRoleProperty(YukonRoleProperty.DEVICE_DATA_MONITORING)
    @RequestMapping(value = "/device-data-monitor/{monitorId}/toggle", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> toggleDeviceData(@PathVariable int monitorId, YukonUserContext yukonUserContext) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        Map<String, Object> json = new HashMap<>();
        try {
            boolean isEnabled = deviceDataMonitorService.toggleEnabled(monitorId);
            json.put("isEnabled", isEnabled);
        } catch (NotFoundException e) {
            String errorMsg = messageSourceAccessor.getMessage(
                new YukonMessageSourceResolvable(baseKey + ".deviceDataMonitor.monitorNotFound"));
            json.put("errorMsg", errorMsg);
        }
        return json;
    }

    @CheckRoleProperty(YukonRoleProperty.OUTAGE_PROCESSING)
    @RequestMapping(value = "/outage-monitor/{outageMonitorId}/toggle", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> toggleOutage(@PathVariable int outageMonitorId, YukonUserContext yukonUserContext) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        Map<String, Object> json = new HashMap<>();
        try {
            json.put("isEnabled", monitorCurrentStatus(outageMonitorService.toggleEnabled(outageMonitorId)));
        } catch (NotFoundException e) {
            String errorMsg = messageSourceAccessor.getMessage(
                new YukonMessageSourceResolvable(baseKey + ".outageMonitorConfig.notFound"));
            json.put("errorMsg", errorMsg);
        }
        return json;
    }

    @CheckRoleProperty(YukonRoleProperty.STATUS_POINT_MONITORING)
    @RequestMapping(value = "/status-point-monitor/{statusPointMonitorId}/toggle", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> toggleStatusPoint(@PathVariable int statusPointMonitorId, YukonUserContext yukonUserContext) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        Map<String, Object> json = new HashMap<>();
        try {
            json.put("isEnabled", monitorCurrentStatus(statusPointMonitorService.toggleEnabled(statusPointMonitorId)));
        } catch (NotFoundException e) {
            String errorMsg = messageSourceAccessor.getMessage(
                new YukonMessageSourceResolvable(baseKey + ".statusPointMonitor.monitorNotFound"));
            json.put("errorMsg", errorMsg);
        }
        return json;
    }

    @CheckRoleProperty(YukonRoleProperty.TAMPER_FLAG_PROCESSING)
    @RequestMapping(value = "/tamper-flag-monitor/{tamperFlagMonitorId}/toggle", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> toggleTamperFlag(@PathVariable int tamperFlagMonitorId, YukonUserContext yukonUserContext) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        Map<String, Object> json = new HashMap<>();
        try {
            json.put("isEnabled", monitorCurrentStatus(tamperFlagMonitorService.toggleEnabled(tamperFlagMonitorId)));
        } catch (NotFoundException e) {
            String errorMsg = messageSourceAccessor.getMessage(
                new YukonMessageSourceResolvable(baseKey + ".tamperFlagEditor.monitorNotFound"));
            json.put("errorMsg", errorMsg);
        }
        return json;
    }

    @CheckRoleProperty(YukonRoleProperty.PORTER_RESPONSE_MONITORING)
    @RequestMapping(value = "/porter-response-monitor/{monitorId}/toggle", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> togglePorterResponse(@PathVariable int monitorId, YukonUserContext yukonUserContext) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        Map<String, Object> json = new HashMap<>();
        try {
            json.put("isEnabled", monitorCurrentStatus(porterResponseMonitorService.toggleEnabled(monitorId)));
        } catch (NotFoundException e) {
            String errorMsg = messageSourceAccessor.getMessage(
                new YukonMessageSourceResolvable(baseKey + ".porterResponseMonitor.monitorNotFound"));
            json.put("errorMsg", errorMsg);
        }
        return json;
    }
    @CheckRoleProperty(YukonRoleProperty.VALIDATION_ENGINE)
    @RequestMapping(value = "/validation-monitor/{validationMonitorId}/toggle", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> toggleValidation(@PathVariable int validationMonitorId, YukonUserContext yukonUserContext) {
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(yukonUserContext);
        Map<String, Object> json = new HashMap<>();
        try {
            json.put("isEnabled", monitorCurrentStatus(validationMonitorService.toggleEnabled(validationMonitorId)));
        } catch (NotFoundException e) {
            String errorMsg = messageSourceAccessor.getMessage(
                new YukonMessageSourceResolvable(baseKey + ".validationEditor.monitorNotFound"));
            json.put("errorMsg", errorMsg);
        }
        return json;
    }
    
    public boolean monitorCurrentStatus(MonitorEvaluatorStatus newEvaluatorStatus) {
        if (newEvaluatorStatus.equals(MonitorEvaluatorStatus.DISABLED)) {
            return false;
        }
        return true;
    }
}
