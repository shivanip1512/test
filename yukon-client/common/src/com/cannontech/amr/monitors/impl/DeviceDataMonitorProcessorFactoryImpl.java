package com.cannontech.amr.monitors.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.monitors.DeviceDataMonitorProcessorFactory;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class DeviceDataMonitorProcessorFactoryImpl extends MonitorProcessorFactoryBase<DeviceDataMonitor> implements DeviceDataMonitorProcessorFactory {

    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceDataMonitorCalculationService deviceDataMonitorCalculationService;
    @Autowired private DeviceGroupService deviceGroupService;
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorProcessorFactoryImpl.class);

    private final Cache<Pair<SimpleDevice, Integer>, Boolean> devicesToMonitors =
        CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
    
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

        if (richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoType() == PaoType.SYSTEM
            || richPointData.getPointValue().getPointQuality().isInvalid() || !monitor.isEnabled()
            || monitor.getAttributes().isEmpty()) {
            if (richPointData.getPointValue().getPointQuality().isInvalid()) {
                log.debug("monitor {} discarded point data {} because point quality is invalid", monitor,
                    richPointData.getPointValue());
                rejectTrackingId(richPointData);
            }
            return;
        }

        SimpleDevice device;
        try {
            device = new SimpleDevice(richPointData.getPaoPointIdentifier().getPaoIdentifier());
        } catch (Exception e) {
            // device type is invalid for this monitor
            return;
        }

        Pair<SimpleDevice, Integer> pair = Pair.of(device, monitor.getId());
        Boolean isValidDeviceForMonitor = devicesToMonitors.getIfPresent(pair);
        // Not cached, find the answer and cache it
        if (isValidDeviceForMonitor == null) {
            isValidDeviceForMonitor = deviceGroupService.isDeviceInGroup(monitor.getGroup(), device);
            devicesToMonitors.put(pair, isValidDeviceForMonitor);
        }

        if (Boolean.TRUE.equals(isValidDeviceForMonitor)) {
            deviceDataMonitorCalculationService.updateViolationsGroupBasedOnNewPointData(monitor, richPointData);
            acceptTrackingId(richPointData);
        }
    }
    
    @Override
    protected Logger getTrackingLogger() {
        return log;
    }
}
