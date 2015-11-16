package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.common.userpage.dao.UserSubscriptionDao;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;


@Controller
@RequestMapping("/subscribedMonitorsWidget/*")
@CheckRoleProperty({YukonRoleProperty.DEVICE_DATA_MONITORING,
    YukonRoleProperty.OUTAGE_PROCESSING,
    YukonRoleProperty.TAMPER_FLAG_PROCESSING,
    YukonRoleProperty.STATUS_POINT_MONITORING,
    YukonRoleProperty.PORTER_RESPONSE_MONITORING,
    YukonRoleProperty.VALIDATION_ENGINE,
    })
public class SubscribedMonitorsWidget extends AllMonitorsWidget {

    public SubscribedMonitorsWidget() {
    }
    
    @Autowired
    public SubscribedMonitorsWidget(
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        super(roleAndPropertyDescriptionService);
    }

    @Autowired private UserSubscriptionDao userSubscriptionDao;

    protected void putMonitorsInModel(ModelMap model, YukonUserContext context) {
        List<UserSubscription> subscribed = 
            Lists.newArrayList(userSubscriptionDao.getSubscriptionsForUser(context.getYukonUser()));

        List<DeviceDataMonitor> deviceDataMonitors = new ArrayList<>();
        List<OutageMonitor> outageMonitors = new ArrayList<>();
        List<TamperFlagMonitor> tamperFlagMonitors = new ArrayList<>();
        List<StatusPointMonitor> statusPointMonitors = new ArrayList<>();
        List<PorterResponseMonitor> porterResponseMonitors = new ArrayList<>();
        List<ValidationMonitor> validationMonitors = new ArrayList<>();

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