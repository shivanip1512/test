package com.cannontech.web.updater.deviceDataMonitor.handler;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorService;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceDataMonitor.DeviceDataMonitorUpdaterTypeEnum;

public class CalculationStatusUpdaterHandler implements DeviceDataUpdaterHandler {
    
    private static final Logger log = YukonLogManager.getLogger(CalculationStatusUpdaterHandler.class);

    @Autowired private DeviceDataMonitorService deviceDataMonitorService;
    @Autowired private MonitorCacheService cacheService;

    @Override
    public String handle(int monitorId, YukonUserContext userContext) {
        try {
            DeviceDataMonitor monitor = cacheService.getDeviceMonitor(monitorId);
            
            // if this monitor is disabled then just return "N/A"
            if (monitor.isEnabled()) {
                try {
                    return deviceDataMonitorService.getViolations(monitorId);
                } catch (ExecutionException e) {
                    log.error(e);
                }
            }
        } catch (NotFoundException e) {
            // no monitor by that id or no device group
            log.debug("no monitor found with id " + monitorId);
        }
        return "NA";
    }

    @Override
    public DeviceDataMonitorUpdaterTypeEnum getUpdaterType() {
        return DeviceDataMonitorUpdaterTypeEnum.CALCULATION_STATUS;
    }

    @Override
    public boolean isValueAvailableImmediately() {
        return false;
    }
}
