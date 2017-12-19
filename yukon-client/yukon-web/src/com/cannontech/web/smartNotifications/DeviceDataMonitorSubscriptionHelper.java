package com.cannontech.web.smartNotifications;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;

public class DeviceDataMonitorSubscriptionHelper {
    
    @Autowired private MonitorCacheService monitorCacheService;
    
    public SmartNotificationSubscription retrieveSubscription(ModelMap model, HttpServletRequest request, 
                                                              List<SmartNotificationSubscription> subscriptions, SmartNotificationSubscription subscription) {
        int monitorId = ServletRequestUtils.getIntParameter(request, "monitorId", 0);
        if (monitorId != 0) {
            subscription.addParameters("monitorId",  monitorId);
            model.addAttribute("monitorName", monitorCacheService.getDeviceMonitor(monitorId).getName());
            Optional<SmartNotificationSubscription> ddmSub = subscriptions.stream()
                .filter(sub -> monitorId == Integer
                    .parseInt((String) sub.getParameters().get("monitorId")))
                .findFirst();
            if (ddmSub.isPresent()) {
                return ddmSub.get();
            }
        }
        return subscription;
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
