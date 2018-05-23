package com.cannontech.amr.monitors.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.monitors.DeviceDataMonitorProcessorFactory;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class DeviceDataMonitorProcessorFactoryImpl extends MonitorProcessorFactoryBase<DeviceDataMonitor> implements DeviceDataMonitorProcessorFactory {

    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceDataMonitorCalculationService deviceDataMonitorCalculationService;
    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    private Set<DbChangeType> changeType = EnumSet.of(DbChangeType.UPDATE, DbChangeType.DELETE);
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorProcessorFactoryImpl.class);

    //<monitor id, Map<device in the monitor group, attributes processed by monitor>> 
    private final Cache<Integer, Multimap<SimpleDevice, Attribute>> deviceGroupToDevices =
        CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
    
    //<monitor id, device ids> Devices not processed by monitor
    private final Cache<Integer, List<Integer>> devicesToIgnore =
            CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();
        
    @Override
    protected List<DeviceDataMonitor> getAllMonitors() {
        return monitorCacheService.getEnabledDeviceDataMonitors();
    }

    @PostConstruct
    public void initialize() {
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.DEVICE_DATA_MONITOR, changeType, e -> {
            //if monitor was changed, removing the cached values for that monitor
            deviceGroupToDevices.invalidate(e.getPrimaryKey());
        });
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
            || richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoType().getPaoCategory() != PaoCategory.DEVICE
            || richPointData.getPointValue().getPointQuality().isInvalid() || !monitor.isEnabled()
            || monitor.getAttributes().isEmpty()) {
            if (richPointData.getPointValue().getPointQuality().isInvalid()) {
                log.debug("monitor {} discarded point data {} because point quality is invalid", monitor,
                    richPointData.getPointValue());
            }
            return;
        }

        SimpleDevice device = new SimpleDevice(richPointData.getPaoPointIdentifier().getPaoIdentifier());
        
        if (devicesToIgnore.getIfPresent(monitor.getId()) != null
            && devicesToIgnore.getIfPresent(monitor.getId()).contains(device.getDeviceId())) {
            //we already checked this device in the last 30 seconds.
            return;
        }

        boolean isMonitorCached = deviceGroupToDevices.getIfPresent(monitor.getId()) != null;
        boolean isDeviceCached = isMonitorCached && deviceGroupToDevices.getIfPresent(monitor.getId()).containsKey(device);
        
        log.debug("{} isCached {}", device, isDeviceCached);

        if (!isDeviceCached) {
            Multimap<SimpleDevice, Attribute> deviceInGroup = attributeService.getDevicesInGroupThatSupportAttribute(
                monitor.getGroup(), monitor.getAttributes(), Lists.newArrayList(device.getDeviceId()));
            if (!deviceInGroup.isEmpty()) {
                if (!isMonitorCached) {
                    deviceGroupToDevices.put(monitor.getId(), HashMultimap.create());
                }
                Multimap<SimpleDevice, Attribute> cachedDevices = deviceGroupToDevices.getIfPresent(monitor.getId());
                cachedDevices.putAll(deviceInGroup);
            } else {
                if(devicesToIgnore.getIfPresent(monitor.getId()) == null) {
                    devicesToIgnore.put(monitor.getId(), new ArrayList<>());
                }
                //devices will be ignored for 30 seconds
                devicesToIgnore.getIfPresent(monitor.getId()).add(device.getDeviceId());
            }
        }

        if (log.isDebugEnabled()) {
            deviceGroupToDevices.asMap().keySet().forEach(monitorId -> {
                Multimap<SimpleDevice, Attribute> devices = deviceGroupToDevices.getIfPresent(monitorId);
                devices.keySet().forEach(d -> {
                    log.debug("Cached values for monitorId {} device {} attributes {}", monitorId, d,
                        devices.get(d).toArray());
                });
            });
        }

        Multimap<SimpleDevice, Attribute> deviceToRecalculate = HashMultimap.create();
        Collection<Attribute> attributes = deviceGroupToDevices.getIfPresent(monitor.getId()).get(device);
        if(!attributes.isEmpty()) {
            deviceToRecalculate.putAll(device, attributes);
        }
        
        deviceDataMonitorCalculationService.recalculateViolation(monitor, deviceToRecalculate);
    }
}
