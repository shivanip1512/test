package com.cannontech.yukon.system.metrics.listener;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class YukonMetricListener implements MessageListener {
    private static final Logger log = YukonLogManager.getLogger(YukonMetricListener.class);
    @Autowired private AttributeService attributeService;
    @Autowired private SimplePointAccessDao pointAccessDao;

    @PostConstruct
    public void init() {
        log.info("init callllllllled");
    }
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            String textMessage = null;
            try {
                textMessage = ((TextMessage) message).getText();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                        .create();
                YukonMetric yukonMetric = gson.fromJson(textMessage, YukonMetric.class);
                if (shouldGeneratePointData(yukonMetric.getAttributeName())) {
                    LitePoint litePoint = attributeService.getPointForAttribute(PaoUtils.SYSTEM_PAOIDENTIFIER,
                            BuiltInAttribute.valueOf(yukonMetric.getAttributeName()));
                    pointAccessDao.setPointValue(litePoint, yukonMetric.getTimestamp().toInstant(),
                            Double.valueOf(yukonMetric.getFieldValue().toString()));
                }
            } catch (Exception e) {
                log.error("Error occurred while generating point data.", e);
            }

        }

    }

    private boolean shouldGeneratePointData(String attributeName) {
        try {
            YukonMetricPointDataType.valueOf(attributeName);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

}
