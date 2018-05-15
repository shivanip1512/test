package com.cannontech.amr.monitors.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.monitors.DeviceDataMonitorProcessorFactory;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DeviceDataMonitorProcessorFactoryImpl extends MonitorProcessorFactoryBase<DeviceDataMonitor> implements DeviceDataMonitorProcessorFactory {

    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceDataMonitorCalculationService deviceDataMonitorCalculationService;
    @Autowired private DeviceGroupService deviceGroupService;

    private final Cache<ImmutablePair<Integer, DeviceGroup>, Boolean> deviceInGroupCache =
        CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorProcessorFactoryImpl.class);

    @Override
    protected List<DeviceDataMonitor> getAllMonitors() {
        return monitorCacheService.getEnabledDeviceDataMonitors();
    }

    @Override
    protected RichPointDataListener createPointListener(DeviceDataMonitor monitor) {
        return new RichPointDataListener() {
            @Override
            public void pointDataReceived(RichPointData richPointData) {
                handlePointDataReceived(monitor, richPointData);
            }
        };
    }

    @Override
    public void handlePointDataReceived(DeviceDataMonitor monitor, RichPointData richPointData) {
        
        PaoIdentifier paoIdentifier = richPointData.getPaoPointIdentifier().getPaoIdentifier();
        SimpleDevice device = new SimpleDevice(paoIdentifier);

        DeviceGroup groupToMonitor = deviceGroupService.findGroupName(monitor.getGroupName());
        /* Creating key using paoId and DeviceGroup as combination of both must be unique */
        ImmutablePair<Integer, DeviceGroup> cacheKey = new ImmutablePair<>(device.getDeviceId(), groupToMonitor);
        Boolean isDeviceInGroup = deviceInGroupCache.getIfPresent(cacheKey);

        if (isDeviceInGroup == null) {
            isDeviceInGroup = deviceGroupService.isDeviceInGroup(groupToMonitor, device);
            deviceInGroupCache.put(cacheKey, isDeviceInGroup);
        }

        if (!isDeviceInGroup) {
         //   log.debug("Device {} not in monitoring group {} ", device, groupToMonitor);
            return;
        }
        deviceDataMonitorCalculationService.recalculateViolation(monitor, richPointData);
    } 
}