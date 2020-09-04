package com.cannontech.amr.monitors.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.amr.deviceDataMonitor.service.DeviceDataMonitorCalculationService;
import com.cannontech.amr.monitors.DeviceDataMonitorProcessorFactory;
import com.cannontech.amr.monitors.MonitorCacheService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class DeviceDataMonitorProcessorFactoryImpl extends MonitorProcessorFactoryBase<DeviceDataMonitor>
        implements DeviceDataMonitorProcessorFactory {
    @Autowired private MonitorCacheService monitorCacheService;
    @Autowired private DeviceDataMonitorCalculationService deviceDataMonitorCalculationService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private AttributeService attributeService;
    private static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorProcessorFactoryImpl.class);

    private final Cache<Pair<SimpleDevice, Integer>, Boolean> devicesToMonitors = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS).build();
    
    //monitor id/ point id
    private final Cache<Pair<Integer, Integer>, Date> processedPoint = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS).build();

    private Map<Integer, Processor> processors = new ConcurrentHashMap<>();
    private int threadCount = 10;
    
    @PostConstruct
    public void initialize() {
        IntStream.range(0, threadCount).forEach(i -> {
            Processor processor = new Processor(i);
            processor.start();
            processors.put(i, processor);
        });
    }

    private class Processor extends Thread {        
        private final BlockingQueue<Data> queue =  new LinkedBlockingDeque<>();
        Processor(int count) {
            this.setName("DeviceDataMonitorProcessor Thread #" + count);
        }

        void add(Data data) {
            try {
                log("Adding to queue", data);
                queue.put(data);
            } catch (Exception e) {
                log.error("Processor {}", this.getName(), e);
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Data data = queue.take();
                    if (data == null) {
                        continue;
                    }
                    log("Removing from queue", data);
                    updateViolationsGroupBasedOnNewPointData(data.monitor, data.richPointData, data.attribute, data.trackingLogger);
                } catch (Exception e) {
                    log.error("Processor {}", this.getName(), e);
                }
            }
        }

        private void log(String text, Data data) {
            log.debug(text + " {} monitor:{} device:{} point:{} created on:{}",
                    this.getName(), data.monitor.getName(),
                    data.richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoId(),
                    data.richPointData.getPointValue(), data.written.toString(dateFormat));
        }
    }

    @Override
    protected List<DeviceDataMonitor> getAllMonitors() {
        return monitorCacheService.getEnabledDeviceDataMonitors();
    }

    @Override
    protected RichPointDataListener createPointListener(DeviceDataMonitor monitor) {
        var trackingLogger = new PointDataTrackingLogger(monitor.getName(), log);
        return richPointData -> handlePointDataReceived(monitor, richPointData, trackingLogger);
    }

    @Override
    public void handlePointDataReceived(DeviceDataMonitor monitor, RichPointData richPointData, PointDataTrackingLogger trackingLogger) {
        if (richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoType() == PaoType.SYSTEM
                || richPointData.getPointValue().getPointQuality().isInvalid() || !monitor.isEnabled()
                || monitor.getAttributes().isEmpty()) {
            if (richPointData.getPointValue().getPointQuality().isInvalid()) {
                log.debug("monitor {} discarded point data {} because point quality is invalid", monitor,
                        richPointData.getPointValue());
                trackingLogger.rejectId(richPointData);
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
            int paoId = richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoId();
            BuiltInAttribute attribute = getAttribute(monitor, richPointData, paoId);
            if (attribute == null) {
                // this point can't be processed by this monitor
                return;
            }
            int queue = Math.abs(richPointData.getPointValue().getId()) % processors.size();
            Processor processor = processors.get(queue);
            processor.add(new Data(monitor, richPointData, attribute, trackingLogger));
        }
    }

    /**
     * Returns null if the point can't be processed by this monitor
     */
    private BuiltInAttribute getAttribute(DeviceDataMonitor monitor, RichPointData richPointData, int paoId) {
        BuiltInAttribute attribute = Iterables.getFirst(
                attributeService.findAttributesForPoint(richPointData.getPaoPointIdentifier().getPaoTypePointIdentifier(),
                        Sets.newHashSet(monitor.getAttributes())),
                null);
        if (attribute == null) {
            log.debug("{} recalculation of violation for device {} is skipped. The processor for point {} is not found.",
                    monitor, paoId, richPointData.getPointValue().getId());
        }
        return attribute;
    }

    class Data {
        private DeviceDataMonitor monitor;
        private RichPointData richPointData;
        private BuiltInAttribute attribute;
        private DateTime written;
        private PointDataTrackingLogger trackingLogger;

        public Data(DeviceDataMonitor monitor, RichPointData richPointData, BuiltInAttribute attribute, PointDataTrackingLogger trackingLogger) {
            this.monitor = monitor;
            this.richPointData = richPointData;
            this.attribute = attribute;
            this.written = DateTime.now();
            this.trackingLogger = trackingLogger;
        }
    }

    private void updateViolationsGroupBasedOnNewPointData(DeviceDataMonitor monitor, RichPointData richPointData,
            BuiltInAttribute attribute, PointDataTrackingLogger trackingLogger) {
        if (isValidPointDataForCalculation(monitor, richPointData)) {
            deviceDataMonitorCalculationService.updateViolationsGroupBasedOnNewPointData(monitor, richPointData, attribute);
            trackingLogger.acceptId(richPointData);
        } else {
            trackingLogger.rejectId(richPointData);
        }
    }

    /**
     * Returns true is the data is recent and adds the recent point date to cache.
     */
    boolean isValidPointDataForCalculation(DeviceDataMonitor monitor, RichPointData richPointData) {
        Pair<Integer, Integer> pair = Pair.of(monitor.getId(), richPointData.getPointValue().getId());
        Date cachedDate = processedPoint.getIfPresent(pair);
        Date currentDate = richPointData.getPointValue().getPointDataTimeStamp();
        if (cachedDate == null || currentDate.after(cachedDate)) {
            processedPoint.put(pair, richPointData.getPointValue().getPointDataTimeStamp());
            log.debug("Processing point data monitor:{} device:{} point:{}",
                    monitor.getName(), richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoId(),
                    richPointData.getPointValue());
            return true;
        }
        log.debug("Discarding point data monitor:{} device:{} discarding point:{} cached point date:{}",
                monitor.getName(), richPointData.getPaoPointIdentifier().getPaoIdentifier().getPaoId(),
                richPointData.getPointValue(), cachedDate);
        return false;
    }
}
