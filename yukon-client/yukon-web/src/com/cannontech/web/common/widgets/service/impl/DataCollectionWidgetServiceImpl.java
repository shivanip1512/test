package com.cannontech.web.common.widgets.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.dao.RecentPointValueDao;
import com.cannontech.common.device.data.collection.dao.model.DeviceCollectionDetail;
import com.cannontech.common.device.data.collection.message.CollectionRequest;
import com.cannontech.common.device.data.collection.message.RecalculationRequest;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.web.common.widgets.model.DataCollectionSummary;
import com.cannontech.web.common.widgets.service.DataCollectionWidgetService;

@Service
public class DataCollectionWidgetServiceImpl implements DataCollectionWidgetService, MessageListener {

    private JmsTemplate jmsTemplate;
    private static final String collectionQueueName = "yukon.qr.obj.data.collection.CollectionRequest";
    private static final Logger log = YukonLogManager.getLogger(DataCollectionWidgetServiceImpl.class);
    private Map<DeviceGroup, DataCollectionSummary> enabledDeviceSummary = new ConcurrentHashMap<>();
    private Map<DeviceGroup, DataCollectionSummary> allDeviceSummary = new ConcurrentHashMap<>();
    private Instant collectionTime;
    @Autowired private RecentPointValueDao recentPointValueDao;

    @Override
    public DataCollectionSummary getDataCollectionSummary(DeviceGroup group, boolean includeDisabled) {
        DataCollectionSummary summary = null;
        if (includeDisabled) {
            summary = allDeviceSummary.get(group);
        } else {
            summary = enabledDeviceSummary.get(group);
        }
        return summary == null ? recalculate(group, includeDisabled) : summary;
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
                    RecalculationRequest request = (RecalculationRequest) objMessage.getObject();
                    collectionTime = request.getCollectionTime();
                    recalculate();
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }

    private void recalculate() {
        enabledDeviceSummary.keySet().forEach(key -> {
            enabledDeviceSummary.put(key, recalculate(key, false));
        });
        allDeviceSummary.keySet().forEach(key -> {
            allDeviceSummary.put(key, recalculate(key, true));
        });
    }

    private DataCollectionSummary recalculate(DeviceGroup group, boolean includeDisabled) {
        DataCollectionSummary summary = new DataCollectionSummary();
        summary.setCollectionTime(this.collectionTime);
        if (includeDisabled) {
            allDeviceSummary.put(group, summary);
        } else {
            enabledDeviceSummary.put(group, summary);
        }
        return summary;
    }
    
    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
    }

    @Override
    public List<DeviceCollectionDetail> getDeviceCollectionResult(DeviceGroup group, boolean includeDisabled, ResultType... types) {
        //construct a date range based on the types
        return recentPointValueDao.getDeviceCollectionResult(group, includeDisabled, null);
    }
}