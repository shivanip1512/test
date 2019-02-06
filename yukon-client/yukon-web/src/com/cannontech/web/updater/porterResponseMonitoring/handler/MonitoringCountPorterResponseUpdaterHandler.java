package com.cannontech.web.updater.porterResponseMonitoring.handler;

import java.util.Collections;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.porterResponseMonitoring.PorterResponseMonitorUpdaterTypeEnum;

public class MonitoringCountPorterResponseUpdaterHandler implements PorterResponseUpdaterHandler {

    private @Autowired PorterResponseMonitorDao porterResponseMonitorDao;
    private @Autowired DeviceGroupService deviceGroupService;

    @Override
    public String handle(int porterResponseMonitorId, YukonUserContext userContext) {

        String countStr = "N/A";
        
        try {
            
            PorterResponseMonitor porterResponseMonitor = porterResponseMonitorDao.getMonitorById(porterResponseMonitorId);
            String groupName = porterResponseMonitor.getGroupName();
            
            DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
            int deviceCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
            countStr = String.valueOf(deviceCount);
            
        } catch (NotFoundException e) {
            Log.error("Porter Response Monitor not found with id " + porterResponseMonitorId, e);
        }
        
        return countStr;
    }

    @Override
    public PorterResponseMonitorUpdaterTypeEnum getUpdaterType() {
        return PorterResponseMonitorUpdaterTypeEnum.MONITORING_COUNT;
    }

}