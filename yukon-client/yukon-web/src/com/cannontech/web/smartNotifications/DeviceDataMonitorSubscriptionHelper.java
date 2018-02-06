package com.cannontech.web.smartNotifications;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;

public class DeviceDataMonitorSubscriptionHelper {
    
    @Autowired private MonitorCacheService monitorCacheService;
    
    public List<SmartNotificationSubscription> retrieveSubscriptionsForMonitor(ModelMap model, HttpServletRequest request, 
                                                              List<SmartNotificationSubscription> subscriptions) {
        int monitorId = ServletRequestUtils.getIntParameter(request, "monitorId", 0);
        if (monitorId != 0) {
            model.addAttribute("monitorId", monitorId);
            subscriptions = subscriptions.stream()
                    .filter(sub -> monitorId == Integer
                        .parseInt((String) sub.getParameters().get("monitorId")))
                    .collect(Collectors.toList()); 
        }
        return subscriptions;
    }
    
    public void retrieveMonitor(ModelMap model, SmartNotificationSubscription subscription) {
        int monitorId = Integer.parseInt((String)subscription.getParameters().get("monitorId"));
        retrieveMonitorById(model, monitorId);
    }
    
    public String retrieveMonitorById(ModelMap model, int monitorId) {
        String monitorName = monitorCacheService.getDeviceMonitor(monitorId).getName();
        model.addAttribute("monitorName", monitorName);
        return monitorName;
    }
}
