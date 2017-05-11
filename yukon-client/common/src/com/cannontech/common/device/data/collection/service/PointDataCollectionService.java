package com.cannontech.common.device.data.collection.service;

import static org.joda.time.Instant.now;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao;
import com.cannontech.common.device.data.collection.message.CollectionRequest;
import com.cannontech.common.device.data.collection.message.RecalculationRequest;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class PointDataCollectionService implements MessageListener {

    private static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_COLLECTION = Duration.standardMinutes(15);
    private static final String recalculationQueueName = "yukon.qr.obj.data.collection.RecalculationRequest";
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    @Autowired private RecentPointValueDao recentPointValueDao;
    private JmsTemplate jmsTemplate;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final Logger log = YukonLogManager.getLogger(PointDataCollectionService.class);
    private Instant lastCollectionTime;
    private boolean collectingData = false;

    @PostConstruct
    public void init() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                collect();
            } catch (Exception e) {
                log.error("Failed to start collection", e);
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof CollectionRequest) {
                    if (now().isAfter(lastCollectionTime.plus(MINUTES_TO_WAIT_BEFORE_NEXT_COLLECTION))) {
                        collect();
                    }
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }

    /**
     * If data collection is not running inserts the recent point data in RecentPointValue.
     */
    private void collect() {
        if (collectingData) {
            log.debug("Data collection already running.");
            return;
        }
        try {
            collectingData = true;
            this.lastCollectionTime = new Instant();
            List<LiteYukonPAObject> devices = databaseCache.getAllYukonPAObjects();
            log.debug("Starting data collection for " + devices.size() + " devices.");

            List<LiteYukonPAObject> meters = new ArrayList<>();
            List<LiteYukonPAObject> waterMeters = new ArrayList<>();
            List<LiteYukonPAObject> lcrs = new ArrayList<>();

            devices.forEach(device -> {
                if (device.getPaoType().isWaterMeter()) {
                    waterMeters.add(device);
                } else if (device.getPaoType().isMeter()) {
                    meters.add(device);
                } else if (device.getPaoType() == PaoType.LCR6200_RFN || device.getPaoType() == PaoType.LCR6600_RFN
                    || device.getPaoType() == PaoType.LCR3102) {
                    lcrs.add(device);
                }
            });
            Long lastChangeId =
                persistedSystemValueDao.getLongValue(PersistedSystemValueKey.DATA_COLLECTION_LAST_CHANGE_ID);
            Long maxChangeId = rphDao.getMaxChangeId();
            ReadableRange<Long> changeIdRange = Range.inclusive(lastChangeId, maxChangeId);
            Map<PaoIdentifier, PointValueQualityHolder> recentValues = new HashMap<>();
            addRecentValues(recentValues, waterMeters, BuiltInAttribute.USAGE_WATER, changeIdRange);
            addRecentValues(recentValues, meters, BuiltInAttribute.USAGE, changeIdRange);
            addRecentValues(recentValues, lcrs, BuiltInAttribute.RELAY_1_RUN_TIME_DATA_LOG, changeIdRange);

            log.debug("Got " + recentValues.size() + " rows of data from RPH");
            if (!devices.isEmpty()) {
               // recentPointValueDao.collectData(recentValues);

                // send message to web server to start the data collection widget values recalculation
                jmsTemplate.convertAndSend(recalculationQueueName, new RecalculationRequest(lastCollectionTime));
                persistedSystemValueDao.setValue(PersistedSystemValueKey.DATA_COLLECTION_LAST_CHANGE_ID,
                    changeIdRange.getMax());
            }
        } finally {
            collectingData = false;
        }
    }

    /**
     * Adds values from RPH to the list
     */
    private void addRecentValues(Map<PaoIdentifier, PointValueQualityHolder> recentValues,
            List<LiteYukonPAObject> devices, Attribute attribute, ReadableRange<Long> changeIdRange) {
        if (!devices.isEmpty()) {
            recentValues.putAll(rphDao.getSingleAttributeData(devices, attribute, false, null, changeIdRange));
        }
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}
