package com.cannontech.web.widget;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitor;
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.common.userpage.dao.UserMonitorDao;
import com.cannontech.common.userpage.dao.UserPageDao;
import com.cannontech.common.userpage.model.UserMonitor;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserPage.Category;
import com.cannontech.common.userpage.model.UserPage.Module;
import com.cannontech.common.validation.model.ValidationMonitor;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class SubscribedMonitorsWidget extends AllMonitorsWidget {
    
    @Autowired private UserMonitorDao userMonitorDao;
    
    
    @Override
    protected void putMonitorsInModel(ModelMap model, YukonUserContext context) {
        List<UserMonitor> subscribed = Lists.newArrayList(userMonitorDao.getMonitorsForUser(context.getYukonUser()));
        
        List<DeviceDataMonitor> deviceDataMonitors = Lists.newArrayList();
        List<OutageMonitor> outageMonitors = Lists.newArrayList();
        List<TamperFlagMonitor> tamperFlagMonitors = Lists.newArrayList();
        List<StatusPointMonitor> statusPointMonitors = Lists.newArrayList();
        List<PorterResponseMonitor> porterResponseMonitors = Lists.newArrayList();
        List<ValidationMonitor> validationMonitors = Lists.newArrayList();

        for (UserMonitor monitor : subscribed) {
            switch (monitor.getType()) {
            case DEVICE_DATA:
                deviceDataMonitors.add(deviceDataMonitorDao.getMonitorById(monitor.getMonitorId()));
                break;
            case OUTAGE:
                outageMonitors.add(outageMonitorDao.getById(monitor.getMonitorId()));
                break;
            case TAMPER_FLAG:
                tamperFlagMonitors.add(tamperFlagMonitorDao.getById(monitor.getMonitorId()));
                break;
            case STATUS_POINT:
                statusPointMonitors.add(statusPointMonitorDao.getStatusPointMonitorById(monitor.getMonitorId()));
                break;
            case PORTER_RESPONSE:
                porterResponseMonitors.add(porterResponseMonitorDao.getMonitorById(monitor.getMonitorId()));
                break;
            case VALIDATION:
                validationMonitors.add(validationMonitorDao.getById(monitor.getMonitorId()));
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