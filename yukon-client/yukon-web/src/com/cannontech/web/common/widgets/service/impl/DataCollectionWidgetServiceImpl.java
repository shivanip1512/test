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
import com.cannontech.common.util.Range;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;

@Service
public class DataCollectionWidgetServiceImpl implements DataCollectionWidgetService, MessageListener {

    private static final Duration DAYS_2 = Duration.standardDays(2);
    private static final Duration DAYS_7 = Duration.standardDays(7);
    private static final Duration DAYS_14 = Duration.standardDays(14);
    
    private JmsTemplate jmsTemplate;
    private static final String collectionQueueName = "yukon.qr.obj.data.collection.CollectionRequest";
    private static final Logger log = YukonLogManager.getLogger(DataCollectionWidgetServiceImpl.class);
    private Map<DeviceGroup, DataCollectionSummary> enabledDeviceSummary = new ConcurrentHashMap<>();
    private Map<DeviceGroup, DataCollectionSummary> allDeviceSummary = new ConcurrentHashMap<>();
    private Instant lastCollectionTime;
    private boolean calculating = false;
    @Autowired private RecentPointValueDao rpvDao;

    @Override
    public DataCollectionSummary getDataCollectionSummary(DeviceGroup group, boolean includeDisabled) {
        DataCollectionSummary summary = null;
        if (includeDisabled) {
            summary = allDeviceSummary.get(group);
        } else {
            summary = enabledDeviceSummary.get(group);
        }

        return summary == null ? recalculate(group, includeDisabled, getRanges()) : summary;
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
                    if (calculating) {
                        log.debug("Recalculation already running.");
                        return;
                    }
                    calculating = true;
                    RecalculationRequest request = (RecalculationRequest) objMessage.getObject();
                    lastCollectionTime = request.getCollectionTime();
                    recalculate();
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
            calculating = false;
        }
    }

    private void recalculate() {
        Map<RangeType, Range<Instant>> ranges = getRanges();
        enabledDeviceSummary.keySet().forEach(key -> {
            enabledDeviceSummary.put(key, recalculate(key, false, ranges));
        });
        allDeviceSummary.keySet().forEach(key -> {
            allDeviceSummary.put(key, recalculate(key, true, ranges));
        });
    }

    private DataCollectionSummary recalculate(DeviceGroup group, boolean includeDisabled, Map<RangeType, Range<Instant>> ranges) {
        DataCollectionSummary summary = new DataCollectionSummary(lastCollectionTime);
        summary.setAvailable(rpvDao.getDeviceCount(group, includeDisabled, ranges.get(RangeType.AVAILABLE)));
        summary.setExpected(rpvDao.getDeviceCount(group, includeDisabled, ranges.get(RangeType.EXPECTED)));
        summary.setOutdated(rpvDao.getDeviceCount(group, includeDisabled, ranges.get(RangeType.OUTDATED)));
        summary.setUnavailable(rpvDao.getDeviceCount(group, includeDisabled, ranges.get(RangeType.UNAVAILABLE)));
        summary.calculatePrecentages();
        log.debug(summary);
        if (includeDisabled) {
            allDeviceSummary.put(group, summary);
        } else {
            enabledDeviceSummary.put(group, summary);
        }
        return summary;
    }
    
    private Map<RangeType, Range<Instant>> getRanges() {
        Map<RangeType, Range<Instant>> ranges = new HashMap<>();
        Instant startOfTheDay = new Instant(new DateTime().withTimeAtStartOfDay());
        Range<Instant> currentRange = buildRange(RangeType.AVAILABLE, ranges, startOfTheDay.minus(DAYS_2), now());
        currentRange = buildRange(RangeType.EXPECTED, ranges, currentRange.getMin().minus(DAYS_7), currentRange.getMin());
        currentRange = buildRange(RangeType.OUTDATED, ranges, currentRange.getMin().minus(DAYS_14), currentRange.getMin());
        buildRange(RangeType.UNAVAILABLE, ranges, null, currentRange.getMax());
        return ranges;
    }
    
    private Range<Instant> buildRange(RangeType type, Map<RangeType, Range<Instant>> ranges, Instant min, Instant max) {
        Range<Instant> range = new Range<>(min, true, max, false);
        ranges.put(type, range);
        log.debug(getLogString(range, type));
        return range;
    }
    
    private String getLogString(Range<Instant> range, RangeType type) {
        final DateTimeFormatter df = DateTimeFormat.forPattern("MMM dd YYYY HH:mm:ss");
        String min = range.getMin() == null ? "" : range.getMin().toString(df.withZone(DateTimeZone.getDefault()));
        String max = range.getMax() == null ? "" : range.getMax().toString(df.withZone(DateTimeZone.getDefault()));
        return type + " : " + min + "-" + max + " ";
    }
   
    @Override
    public List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group1, DeviceGroup group2,
            boolean includeDisabled, RangeType... ranges) {
        Map<RangeType, Range<Instant>> allRanges = getRanges();
        log.debug("Getting device collection results:");
        List<DeviceCollectionDetail> details = new ArrayList<>();
        Arrays.asList(ranges).forEach(type -> {
            Range<Instant> timeRange = allRanges.get(type);
            List<DeviceCollectionDetail> detail =
                rpvDao.getDeviceCollectionResult(group1, group2, includeDisabled, timeRange);
            log.debug("For date range " + getLogString(timeRange, type) + " got " + detail.size() + " results.");
            details.addAll(detail);
        });
        return details;
    }

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}