package com.cannontech.web.updater.deviceDataMonitor.handler;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.updater.deviceDataMonitor.DeviceDataMonitorUpdaterTypeEnum;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MonitoringCountUpdaterHandler implements DeviceDataUpdaterHandler {

    private static final Logger log = YukonLogManager.getLogger(MonitoringCountUpdaterHandler.class);

    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private MonitorCacheService cacheService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    // monitorId / monitoring device count
    private final Cache<Integer, Integer> monitorIdToDeviceCount =
        CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    @Override
    public String handle(int monitorId, YukonUserContext userContext) {
        try {
            Integer cachedCount = monitorIdToDeviceCount.getIfPresent(monitorId);
            if (cachedCount == null) {
                DeviceDataMonitor monitor = cacheService.getDeviceMonitor(monitorId);
                DeviceGroup group = monitor.getGroup();
                cachedCount = deviceGroupService.getDeviceCount(Collections.singletonList(group));
                monitorIdToDeviceCount.put(monitorId, cachedCount);
            }
            return String.valueOf(cachedCount);
        } catch (NotFoundException e) {
            // no monitor by that id or no device group
            log.debug("no monitor found with id " + monitorId);
        }
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return messageSourceAccessor.getMessage("yukon.common.na");
    }

    @Override
    public DeviceDataMonitorUpdaterTypeEnum getUpdaterType() {
        return DeviceDataMonitorUpdaterTypeEnum.MONITORING_COUNT;
    }

    @Override
    public boolean isValueAvailableImmediately() {
        return true;
    }
}
