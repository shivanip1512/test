package com.cannontech.web.common.widgets.service.impl;

import static org.joda.time.Instant.now;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao;
import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.data.collection.message.CollectionRequest;
import com.cannontech.common.device.data.collection.message.RecalculationRequest;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;
import com.google.common.collect.Lists;

@Service
public class DataCollectionWidgetServiceImpl implements DataCollectionWidgetService, MessageListener {

    private static final Duration DAYS_2 = Duration.standardDays(2);
    private static final Duration DAYS_7 = Duration.standardDays(7);
    private static final Duration DAYS_14 = Duration.standardDays(14);

    @Autowired private RecentPointValueDao rpvDao;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    private JmsTemplate jmsTemplate;
    private static final String collectionQueueName = "yukon.qr.obj.data.collection.CollectionRequest";
    private static final Logger log = YukonLogManager.getLogger(DataCollectionWidgetServiceImpl.class);
    private Map<DeviceGroup, DataCollectionSummary> enabledDeviceSummary = new ConcurrentHashMap<>();
    private Map<DeviceGroup, DataCollectionSummary> allDeviceSummary = new ConcurrentHashMap<>();
    private boolean calculating = false;

    @Override
    public DataCollectionSummary getDataCollectionSummary(DeviceGroup group, boolean includeDisabled) {
        DataCollectionSummary summary = null;
        if (includeDisabled) {
            summary = allDeviceSummary.get(group);
        } else {
            summary = enabledDeviceSummary.get(group);
        }
        if (summary == null) {
            //first time device group was added to widget or server restarted.
            Instant lastCollectionTime = persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.DATA_COLLECTION_TIME);
            summary = recalculate(group, includeDisabled, getRanges(), lastCollectionTime);
        }
        return summary;
    }

    @Override
    public void collectData() {
        jmsTemplate.convertAndSend(collectionQueueName, new CollectionRequest());
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof RecalculationRequest) {
                    // Received message from SM to start recalculation
                    recalculate();
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }

    /**
     * Recalculates summaries for all cached groups.
     */
    private void recalculate() {
        if (calculating) {
            log.debug("Recalculation already running.");
            return;
        }
        try {
            calculating = true;
            Instant lastCollectionTime = persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.DATA_COLLECTION_TIME);
            Map<RangeType, Range<Instant>> ranges = getRanges();
            enabledDeviceSummary.keySet().forEach(key -> {
                enabledDeviceSummary.put(key, recalculate(key, false, ranges, lastCollectionTime));
            });
            allDeviceSummary.keySet().forEach(key -> {
                allDeviceSummary.put(key, recalculate(key, true, ranges, lastCollectionTime));
            });
        } finally {
            calculating = false;
        }
    }

    /**
     * Creates and caches data collection summary.
     */
    private DataCollectionSummary recalculate(DeviceGroup group, boolean includeDisabled,
            Map<RangeType, Range<Instant>> ranges, Instant lastCollectionTime) {
        DataCollectionSummary summary = new DataCollectionSummary(lastCollectionTime);
        summary.setAvailable(rpvDao.getDeviceCount(group, includeDisabled, ranges.get(RangeType.AVAILABLE)));
        summary.setExpected(rpvDao.getDeviceCount(group, includeDisabled, ranges.get(RangeType.EXPECTED)));
        summary.setOutdated(rpvDao.getDeviceCount(group, includeDisabled, ranges.get(RangeType.OUTDATED)));
        summary.setUnavailable(rpvDao.getDeviceCount(group, includeDisabled, ranges.get(RangeType.UNAVAILABLE)));
        // devices in a group subtract devices found in RecentPointValue 
        int devicesWithoutData = deviceGroupService.getDeviceCount(Lists.newArrayList(group))
            - summary.getTotalDeviceCount();
        // add devices without data to unavailable device count
        summary.getUnavailable().setDeviceCount(summary.getUnavailable().getDeviceCount() + devicesWithoutData);
        summary.calculatePrecentages();
        log.debug(summary);
        if (includeDisabled) {
            allDeviceSummary.put(group, summary);
        } else {
            enabledDeviceSummary.put(group, summary);
        }
        return summary;
    }
    
    /**
     * Creates time ranges for each range type.
     */
    private Map<RangeType, Range<Instant>> getRanges() {
        Map<RangeType, Range<Instant>> ranges = new HashMap<>();
        Instant startOfTheDay = new Instant(new DateTime().withTimeAtStartOfDay());
        Range<Instant> currentRange = buildRange(RangeType.AVAILABLE, ranges, startOfTheDay.minus(DAYS_2), now());
        currentRange = buildRange(RangeType.EXPECTED, ranges, currentRange.getMin().minus(DAYS_7), currentRange.getMin());
        currentRange = buildRange(RangeType.OUTDATED, ranges, currentRange.getMin().minus(DAYS_14), currentRange.getMin());
        buildRange(RangeType.UNAVAILABLE, ranges, null, currentRange.getMin());
        return ranges;
    }
    
    /**
     * Constructs range, adds it list of ranges and logs the range information.
     */
    private Range<Instant> buildRange(RangeType type, Map<RangeType, Range<Instant>> ranges, Instant min, Instant max) {
        Range<Instant> range = new Range<>(min, true, max, false);
        ranges.put(type, range);
        log.debug(getLogString(range, type));
        return range;
    }
    
    /**
     * Builds a string message that describes the range. Used for logging.
     */
    private String getLogString(Range<Instant> range, RangeType type) {
        final DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
        String min = range.getMin() == null ? "" : range.getMin().toString(df.withZone(DateTimeZone.getDefault()));
        String max = range.getMax() == null ? "" : range.getMax().toString(df.withZone(DateTimeZone.getDefault()));
        String includesMin = " [exclusive] ";
        String includesMax = " [exclusive] ";
        if (range.isIncludesMinValue()) {
            includesMin = " [inclusive] ";
        }
        if (range.isIncludesMaxValue()) {
            includesMax = " [inclusive] ";
        }
        return type + " : " + includesMin + min + " - " + includesMax + max + " ";
    }
   
    @Override
    public List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group1, DeviceGroup group2,
            boolean includeDisabled, RangeType... ranges) {
        Map<RangeType, Range<Instant>> allRanges = getRanges();
        log.debug("Getting device collection results:");
        List<DeviceCollectionDetail> details = new ArrayList<>();
        Arrays.asList(ranges).forEach(type -> {
            Range<Instant> timeRange = allRanges.get(type);
            List<DeviceCollectionDetail> detail = rpvDao.getDeviceCollectionResult(group1, group2, includeDisabled, timeRange);
            log.debug("For date range " + getLogString(timeRange, type) + " got " + detail.size() + " results.");
            details.addAll(detail);
        });
        List<DeviceCollectionDetail> detail = rpvDao.getDeviceCollectionResult(group1, group2, includeDisabled);
        log.debug("No entries in RecentPointValue table for " + detail.size() + " devices.");
        details.addAll(detail);
        return details;
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}