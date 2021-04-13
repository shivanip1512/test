package com.cannontech.web.dev;

import static com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest.RequestAction.CLEAR_ALL_EVENTS;
import static com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest.RequestAction.CLEAR_ALL_SUBSCRIPTIONS;
import static com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest.RequestAction.GET_SETTINGS;

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
import com.cannontech.simulators.dao.YukonSimulatorSettingsDao;
import com.cannontech.simulators.dao.YukonSimulatorSettingsKey;
import com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest;
import com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest.RequestAction;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.simulators.message.response.SmartNotificationSimulatorResponse;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.widgets.service.InfrastructureWarningsWidgetService;
import com.cannontech.yukon.IDatabaseCache;

@Controller
public class SmartNotificationsTestingController {
    private static final Logger log = YukonLogManager.getLogger(SmartNotificationsTestingController.class);
    @Autowired protected IDatabaseCache cache;
    @Autowired private InfrastructureWarningsWidgetService infrastructureWarningsWidgetService;
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceDataMonitorServiceImpl monitorService;
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    @Autowired private YukonSimulatorSettingsDao yukonSimulatorSettingsDao;
  
    @RequestMapping("smartNotificationsSimulator")
    public String smartNotificationsSimulator(ModelMap model) {
        SmartNotificationSimulatorResponse response = 
                (SmartNotificationSimulatorResponse) getSimulatorResponse(new SmartNotificationSimulatorRequest(GET_SETTINGS));
        model.addAttribute("smartNotificationSimulatorSettings", response.getSettings());
        model.addAttribute("eventTypes", SmartNotificationEventType.values());
        model.addAttribute("deviceDataMonitors", monitorCacheService.getEnabledDeviceDataMonitors());
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
    
    @PostMapping("createEvents")
    public String createEvents(@ModelAttribute SmartNotificationSimulatorSettings settings, FlashScope flash) {
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_ALL_EVENT_TYPES, settings.isAllTypes());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENT_TYPE, settings.getType());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENT_PARAMETER, settings.getParameter());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_TYPE, settings.getEventsPerType());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_EVENTS_PER_MESSAGE, settings.getEventsPerMessage());
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_WAIT_TIME_SEC, settings.getWaitTimeSec());
        SimulatorResponseBase response = getSimulatorResponse(new SmartNotificationSimulatorRequest(RequestAction.CREATE_EVENTS));
        if (response.isSuccessful()) {
            flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Created test events."));
        } else {
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(response.getException().getMessage()));
        }
        return "redirect:smartNotificationsSimulator";
    }
    
    @RequestMapping(value="startDailyDigest")
    public String startDailyDigest(@RequestParam Integer hour, FlashScope flash) {
        yukonSimulatorSettingsDao.setValue(YukonSimulatorSettingsKey.SMART_NOTIFICATION_SIM_DAILY_DIGEST_HOUR, hour);
        SimulatorResponseBase response = getSimulatorResponse(new SmartNotificationSimulatorRequest(RequestAction.START_DAILY_DIGEST));
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
