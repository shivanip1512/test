package com.cannontech.web.dev;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.deviceDataMonitor.service.impl.DeviceDataMonitorServiceImpl;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.message.request.SmartNotificationSimulatorRequest;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.simulators.message.response.SmartNotificationSimulatorResponse;
import com.cannontech.user.YukonUserContext;
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
    
    @RequestMapping("createEvents")
    public String createEvents(@RequestParam int waitTime, @RequestParam int eventsPerMessage,
            @RequestParam int numberOfMessages, FlashScope flash) {
        SimulatorResponseBase response = 
                getSimulatorResponse(new SmartNotificationSimulatorRequest(waitTime, eventsPerMessage, numberOfMessages));
        if (response.isSuccessful()) {
            flash.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Created test events."));
        } else {
            flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(response.getException().getMessage()));
        }
        return "redirect:smartNotificationsSimulator";
    }

  @RequestMapping(value="saveSubscription", method=RequestMethod.POST)
  @ResponseBody
  public Map<String, Object> saveSmartNotificationsSubscription(@ModelAttribute("subscription") SmartNotificationSubscription subscription,
                                                   @RequestParam int userGroupId, @RequestParam boolean generateTestEmailAddresses, 
                                                   YukonUserContext userContext, FlashScope flash) throws Exception {
    SimulatorResponseBase response = 
            getSimulatorResponse(new SmartNotificationSimulatorRequest(subscription, userGroupId, generateTestEmailAddresses, userContext));
      Map<String, Object> json = new HashMap<String, Object>();
      if (response.isSuccessful()) {
          json.put("success", true);
          json.put("message", "Subscription saved successfully.");
      } else {
          json.put("success", false);
          json.put("message", response.getException().getMessage());
      }
      return json;
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
