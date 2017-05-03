package com.cannontech.common.device.data.collection.service;

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
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao;
import com.cannontech.common.device.data.collection.message.CollectionRequest;
import com.cannontech.common.device.data.collection.message.RecalculationRequest;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.yukon.IDatabaseCache;

public class PointDataCollectionService implements MessageListener {

    private static final int MINUTES_TO_WAIT_TO_START_COLLECTION = 5;
    private static final String recalculationQueueName = "yukon.qr.obj.data.collection.RecalculationRequest";
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private RawPointHistoryDao rphDao;
    @Autowired private RecentPointValueDao recentPointValueDao;
    private JmsTemplate jmsTemplate;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private static final Logger log = YukonLogManager.getLogger(PointDataCollectionService.class);
    private Instant collectionTime;
    private boolean collectingData = false;

    @PostConstruct
    public void init() {
        
        log.info("Waiting " + MINUTES_TO_WAIT_TO_START_COLLECTION + " minutes to start data collection.");

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                collect();
            } catch (Exception e) {
                log.error("Failed to start collection", e);
            }
        }, MINUTES_TO_WAIT_TO_START_COLLECTION, 1, TimeUnit.HOURS);
    }
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof CollectionRequest) {
                    if (new DateTime().isAfter(new DateTime(collectionTime).plusMinutes(15))) {
                        collect();
                    }
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }

    /**
     * If data collection is not running
     * 1. Deletes the data from RecentPointValue table
     * 2. Copies the latest data from rph to RecentPointValue
     * 3. Notifies DataCollectionWidgetServiceImpl that the data is ready for recalculation
     */
    private void collect() {
        if (collectingData) {
            return;
        }
        log.debug("Starting data collection");
        collectingData = true;
        this.collectionTime = new Instant();
        List<LiteYukonPAObject> devices = databaseCache.getAllYukonPAObjects();
        Map<PaoIdentifier, PointValueQualityHolder> recentValues =
            rphDao.getSingleAttributeData(devices, BuiltInAttribute.USAGE, false, null);
        log.debug("Got data from RPH");
        recentPointValueDao.collectData(recentValues);

        // send message to web server to start the data collection widget value recalculation
        jmsTemplate.convertAndSend(recalculationQueueName, new RecalculationRequest(collectionTime));
        collectingData = false;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }
}
