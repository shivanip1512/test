package com.cannontech.web.dev;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.deviceDataMonitor.service.impl.DeviceDataMonitorServiceImpl;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.stars.scheduledDataImport.AssetImportResultType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.infrastructure.model.SmartNotificationSimulatorSettings;
import com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.simulators.message.response.SmartNotificationSimulatorResponse;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.widgets.service.InfrastructureWarningsWidgetService;
import com.cannontech.yukon.IDatabaseCache;
import static com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest.RequestAction.*;

@Controller
public class SmartNotificationsTestingController {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationsTestingController.class);
    @Autowired protected IDatabaseCache cache;
    @Autowired private InfrastructureWarningsWidgetService infrastructureWarningsWidgetService;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceDataMonitorServiceImpl monitorService;
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
  
    @RequestMapping("smartNotificationsSimulator")
    public String smartNotificationsSimulator(ModelMap model) {
        SmartNotificationSimulatorResponse response = 
                (SmartNotificationSimulatorResponse) getSimulatorResponse(new SmartNotificationSimulatorRequest(GET_SETTINGS));
        model.addAttribute("smartNotificationSimulatorSettings", response.getSettings());
        model.addAttribute("eventTypes", SmartNotificationEventType.values());
        model.addAttribute("deviceDataMonitors", monitorCacheService.getDeviceDataMonitors());
        model.addAttribute("assetImportTypes", AssetImportResultType.values());
        model.addAttribute("ddmType", SmartNotificationEventType.DEVICE_DATA_MONITOR);
        model.addAttribute("assetImportType", SmartNotificationEventType.ASSET_IMPORT);
        return "smartNotificationsSimulator.jsp";
    }
    
    @RequestMapping("clearAllSubscriptions")
    public String clearAllSubscriptions(FlashScope flash) {
        SimulatorResponseBase response = getSimulatorResponse(new SmartNotificationSimulatorRequest(CLEAR_ALL_SUBSCRIPTIONS));
        if (response.isSuccessful()) {
            flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("All Subscriptions have been cleared."));
        } else {
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(response.getException().getMessage()));
        }
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping("clearAllEvents")
    public String clearAllEvents(FlashScope flash) {
        SimulatorResponseBase response = getSimulatorResponse(new SmartNotificationSimulatorRequest(CLEAR_ALL_EVENTS));
        if (response.isSuccessful()) {
            flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("All Events have been cleared."));
        } else {
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(response.getException().getMessage()));
        }
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping("createRealEvents")
    public String createRealEvents(FlashScope flash) {
        monitorCacheService.getDeviceDataMonitors().forEach(m -> {
            monitorService.recaclulate(m);
        });
        infrastructureWarningsWidgetService.initiateRecalculation();
        flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Requested recalculation."));
        return "redirect:smartNotificationsSimulator";
    }
    
    @PostMapping("createEvents")
    public String createEvents(@ModelAttribute SmartNotificationSimulatorSettings smartNotificationSimulatorSettings, FlashScope flash) {
        SimulatorResponseBase response = 
                getSimulatorResponse(new SmartNotificationSimulatorRequest(smartNotificationSimulatorSettings.getWaitTimeSec(), 
                                                                           smartNotificationSimulatorSettings.getEventsPerMessage(), 
                                                                           smartNotificationSimulatorSettings.getEventsPerMessage()));
        if (response.isSuccessful()) {
            flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Created test events."));
        } else {
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(response.getException().getMessage()));
        }
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping(value="startDailyDigest")
    public String startDailyDigest(@RequestParam Integer hour, FlashScope flash) {
        SimulatorResponseBase response = getSimulatorResponse(new SmartNotificationSimulatorRequest(hour));
        if (response.isSuccessful()) {
            flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Initiated a daily digest for " + hour + ":00"));
        } else {
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(response.getException().getMessage()));
        }
        return "redirect:smartNotificationsSimulator";
    }
    
    private SimulatorResponseBase getSimulatorResponse(SmartNotificationSimulatorRequest request) {
        try {
            SimulatorResponseBase response = simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
            return response;
        } catch (Exception e) {
            log.error(e);
            SimulatorResponseBase response = new SimulatorResponseBase(false);
            response.setException(new Exception("Unable to send message to Simulator Service: " + e.getMessage() + "."));
            return response;
        }
    }
}
