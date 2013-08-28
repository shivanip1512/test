package com.cannontech.web.widget;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class SubscribedMonitorsWidget extends AllMonitorsWidget {
    
    @Autowired private UserSubscriptionDao userSubscriptionDao;
    
    
    @Override
    protected void putMonitorsInModel(ModelMap model, YukonUserContext context) {
        List<UserSubscription> subscribed = Lists.newArrayList(userSubscriptionDao.getSubscriptionsForUser(context.getYukonUser()));

        List<DeviceDataMonitor> deviceDataMonitors = Lists.newArrayList();
        List<OutageMonitor> outageMonitors = Lists.newArrayList();
        List<TamperFlagMonitor> tamperFlagMonitors = Lists.newArrayList();
        List<StatusPointMonitor> statusPointMonitors = Lists.newArrayList();
        List<PorterResponseMonitor> porterResponseMonitors = Lists.newArrayList();
        List<ValidationMonitor> validationMonitors = Lists.newArrayList();

        for (UserSubscription monitor : subscribed) {
            switch (monitor.getType()) {
            case DEVICE_DATA_MONITOR:
                deviceDataMonitors.add(deviceDataMonitorDao.getMonitorById(monitor.getRefId()));
                break;
            case OUTAGE_MONITOR:
                outageMonitors.add(outageMonitorDao.getById(monitor.getRefId()));
                break;
            case TAMPER_FLAG_MONITOR:
                tamperFlagMonitors.add(tamperFlagMonitorDao.getById(monitor.getRefId()));
                break;
            case STATUS_POINT_MONITOR:
                statusPointMonitors.add(statusPointMonitorDao.getStatusPointMonitorById(monitor.getRefId()));
                break;
            case PORTER_RESPONSE_MONITOR:
                porterResponseMonitors.add(porterResponseMonitorDao.getMonitorById(monitor.getRefId()));
                break;
            case VALIDATION_MONITOR:
                validationMonitors.add(validationMonitorDao.getById(monitor.getRefId()));
                break;
            default:
                //Non-monitor subscription
                break;
            }
        }

        model.addAttribute("isSubscribedWidget", true);
        model.addAttribute("deviceDataMonitors", deviceDataMonitors);
        model.addAttribute("outageMonitors", outageMonitors);
        model.addAttribute("tamperFlagMonitors", tamperFlagMonitors);
        model.addAttribute("statusPointMonitors", statusPointMonitors);
        model.addAttribute("porterResponseMonitors", porterResponseMonitors);
        model.addAttribute("validationMonitors", validationMonitors);
    }
}