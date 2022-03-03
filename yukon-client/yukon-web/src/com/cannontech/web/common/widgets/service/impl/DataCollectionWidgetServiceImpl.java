package com.cannontech.web.common.widgets.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.SortBy;
import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.data.collection.message.CollectionRequest;
import com.cannontech.common.device.data.collection.message.RecalculationRequest;
import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.common.device.data.collection.service.DataCollectionHelper;
import com.cannontech.common.device.data.collection.service.PointDataCollectionService;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;

@Service
public class DataCollectionWidgetServiceImpl implements DataCollectionWidgetService, MessageListener {

    @Autowired private RecentPointValueDao rpvDao;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    @Autowired private GlobalSettingDao globalSettingDao;

    private YukonJmsTemplate jmsTemplate;
    private static final Logger log = YukonLogManager.getLogger(DataCollectionWidgetServiceImpl.class);
    private Map<DeviceGroup, DataCollectionSummary> enabledDeviceSummary = new ConcurrentHashMap<>();
    private Map<DeviceGroup, DataCollectionSummary> allDeviceSummary = new ConcurrentHashMap<>();
    private boolean calculating = false;

    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.DATA_COLLECTION);
    }

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
            Duration days = getAvailabilityWindow();
            summary = recalculate(group, includeDisabled, DataCollectionHelper.getRanges(days), lastCollectionTime);
        }
        return summary;
    }

    @Override
    public void collectData() {
        jmsTemplate.convertAndSend(new CollectionRequest());
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
            Duration days = getAvailabilityWindow();
            Map<RangeType, Range<Instant>> ranges = DataCollectionHelper.getRanges(days);
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
        summary.setAvailable(rpvDao.getDeviceCount(group, includeDisabled, null, RangeType.AVAILABLE, ranges.get(RangeType.AVAILABLE)));
        summary.setExpected(rpvDao.getDeviceCount(group, includeDisabled, null, RangeType.EXPECTED, ranges.get(RangeType.EXPECTED)));
        summary.setOutdated(rpvDao.getDeviceCount(group, includeDisabled, null, RangeType.OUTDATED, ranges.get(RangeType.OUTDATED)));
        summary.setUnavailable(rpvDao.getDeviceCount(group, includeDisabled, null, RangeType.UNAVAILABLE, ranges.get(RangeType.UNAVAILABLE)));
        summary.calculatePrecentages();
        log.debug(summary);
        if (includeDisabled) {
            allDeviceSummary.put(group, summary);
        } else {
            enabledDeviceSummary.put(group, summary);
        }
        return summary;
    }
    

   
    @Override
    public SearchResults<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, List<DeviceGroup> groups,
            boolean includeDisabled, Integer[] selectedGatewayIds, List<RangeType> ranges, PagingParameters paging, SortBy sortBy,
            Direction direction) {
        Duration days = getAvailabilityWindow();
        Map<RangeType, Range<Instant>> allRanges = DataCollectionHelper.getRanges(days);
        log.debug("Getting device collection results:");
        allRanges.keySet().removeIf(type -> !ranges.contains(type));
        allRanges.forEach((k, v) -> log.debug(DataCollectionHelper.getLogString(k, v)));
        return rpvDao.getDeviceCollectionResult(group, groups, includeDisabled, selectedGatewayIds, allRanges, paging, sortBy, direction);
    }
    
    private Duration getAvailabilityWindow() {
        return Duration.standardDays(globalSettingDao.getInteger(GlobalSettingType.DATA_AVAILABILITY_WINDOW_IN_DAYS));
    }

}