package com.cannontech.message.listener.service;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.cannontech.data.provider.DataProvider;
import com.cannontech.message.model.SystemData;
import com.cannontech.message.model.YukonMetric;
import com.cannontech.message.model.YukonMetricIOTDataType;
import com.cannontech.message.model.YukonMetricPointInfo;
import com.cannontech.util.DateTimeDeserializer;
import com.cannontech.util.SystemDataConverterHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Listen for Yukon metric on the topic, once received call method to update cache.
 */

@Service
public class YukonMetricListener {
    private static final Logger log = (Logger) LogManager.getLogger(YukonMetricListener.class);

    @Autowired private DataProvider dataProvider;
    private Gson gson;

    @PostConstruct
    public void init() {
        gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                .create();
    }

    @JmsListener(destination = "com.eaton.eas.yukon.metric", containerFactory = "topicListenerFactory")
    public void receiveMessage(Message message) {
        if (message instanceof TextMessage) {
            String json = null;
            try {
                json = ((TextMessage) message).getText();
                YukonMetric yukonMetric = gson.fromJson(json, YukonMetric.class);
                if (isIOTData(yukonMetric.getPointInfo())) {
                    SystemData data = SystemDataConverterHelper.convert(yukonMetric);
                    log.info("Yukon Metric data received " + yukonMetric);
                    dataProvider.updateSystemInformation(data);
                }
            } catch (JMSException e) {
                log.error("Error receiving system data " + e);
            }
        }
    }

    private boolean isIOTData(YukonMetricPointInfo pointInfo) {
        try {
            YukonMetricIOTDataType.valueOf(pointInfo.name());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}