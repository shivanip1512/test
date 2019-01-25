package com.cannontech.web.common.widgets.service.impl;

import static org.joda.time.Instant.now;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
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
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.SortBy;
import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.data.collection.message.CollectionRequest;
import com.cannontech.common.device.data.collection.message.RecalculationRequest;
import com.cannontech.common.device.data.collection.service.PointDataCollectionService;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;

@Service
public class DataCollectionWidgetServiceImpl implements DataCollectionWidgetService, MessageListener {

    private static final Duration DAYS_7 = Duration.standardDays(7);
    private static final Duration DAYS_14 = Duration.standardDays(14);

    @Autowired private RecentPointValueDao rpvDao;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    private JmsTemplate jmsTemplate;
    private static final String collectionQueueName = "yukon.qr.obj.data.collection.CollectionRequest";
    private static final Logger log = YukonLogManager.getLogger(DataCollectionWidgetServiceImpl.class);
    private Map<DeviceGroup, DataCollectionSummary> enabledDeviceSummary = new ConcurrentHashMap<>();
    private Map<DeviceGroup, DataCollectionSummary> allDeviceSummary = new ConcurrentHashMap<>();
    private boolean calculating = false;

    @Override
    public Instant getRunTime(boolean nextRunTime) {
        Instant runTime = persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.DATA_COLLECTION_TIME);
        if(nextRunTime){
            return runTime.plus(PointDataCollectionService.MINUTES_TO_WAIT_BEFORE_NEXT_COLLECTION);
        } 
        return runTime;
    }
    
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
            persistedSystemValueDao.setValue(PersistedSystemValueKey.DATA_COLLECTION_RECALC_TIME, new Instant());
            log.debug("Recalculation started.");
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
        summary.setAvailable(rpvDao.getDeviceCount(group, includeDisabled, RangeType.AVAILABLE, ranges.get(RangeType.AVAILABLE)));
        summary.setExpected(rpvDao.getDeviceCount(group, includeDisabled, RangeType.EXPECTED, ranges.get(RangeType.EXPECTED)));
        summary.setOutdated(rpvDao.getDeviceCount(group, includeDisabled, RangeType.OUTDATED, ranges.get(RangeType.OUTDATED)));
        summary.setUnavailable(rpvDao.getDeviceCount(group, includeDisabled, RangeType.UNAVAILABLE, ranges.get(RangeType.UNAVAILABLE)));
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
        Duration days = Duration.standardDays(globalSettingDao.getInteger(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS));
        Map<RangeType, Range<Instant>> ranges = new TreeMap<>();
        Instant startOfTheDay = new Instant(new DateTime().withTimeAtStartOfDay());
        Range<Instant> currentRange = buildRange(RangeType.AVAILABLE, ranges, startOfTheDay.minus(days), now());
        currentRange = buildRange(RangeType.EXPECTED, ranges, startOfTheDay.minus(DAYS_7), currentRange.getMin());
        currentRange = buildRange(RangeType.OUTDATED, ranges, startOfTheDay.minus(DAYS_14), currentRange.getMin());
        buildRange(RangeType.UNAVAILABLE, ranges, null, currentRange.getMin());
        return ranges;
    }
    
    /**
     * Constructs range, adds it list of ranges and logs the range information.
     */
    private Range<Instant> buildRange(RangeType type, Map<RangeType, Range<Instant>> ranges, Instant min, Instant max) {
        Range<Instant> range = new Range<>(min, false, max, true);
        ranges.put(type, range);
        log.debug(getLogString(type, range));
        return range;
    }
    
    /**
     * Builds a string message that describes the range. Used for logging.
     */
    private String getLogString(RangeType type, Range<Instant> range) {
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
    public SearchResults<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, List<DeviceGroup> groups,
            boolean includeDisabled, List<RangeType> ranges, PagingParameters paging, SortBy sortBy,
            Direction direction) {
        Map<RangeType, Range<Instant>> allRanges = getRanges();
        log.debug("Getting device collection results:");
        allRanges.keySet().removeIf(type -> !ranges.contains(type));
        allRanges.forEach((k, v) -> log.debug(getLogString(k, v)));
        return rpvDao.getDeviceCollectionResult(group, groups, includeDisabled, allRanges, paging, sortBy, direction);
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}