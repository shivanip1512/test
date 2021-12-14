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
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.SimplePointAccessDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class YukonMetricListener implements MessageListener {
    private static final Logger log = YukonLogManager.getLogger(YukonMetricListener.class);

    @Autowired private PointDao pointDao;
    @Autowired private SimplePointAccessDao pointAccessDao;

    private Gson gson;

    @PostConstruct
    public void init() {
        gson = new GsonBuilder()
                .registerTypeAdapter(DateTime.class, new DateTimeDeserializer())
                .create();
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            String textMessage = null;
            try {
                textMessage = ((TextMessage) message).getText();
                YukonMetric yukonMetric = gson.fromJson(textMessage, YukonMetric.class);
                log.info("Received Yukon Metric data: {}", JsonUtils.toJson(yukonMetric));
                if (shouldGeneratePointData(yukonMetric.getPointInfo())) {
                    YukonMetricPointDataType pointDataype = YukonMetricPointDataType.valueOf(yukonMetric.getPointInfo().name());
                    PointIdentifier pointIdentifier = new PointIdentifier(pointDataype.getType(), pointDataype.getOffset());
                    PaoPointIdentifier paoPointIdentifier = new PaoPointIdentifier(PaoUtils.SYSTEM_PAOIDENTIFIER,
                            pointIdentifier);
                    // Commented these lines as DB changes are not yet committed to master.
                    // LitePoint litePoint = pointDao.getLitePoint(paoPointIdentifier);
                    // pointAccessDao.setPointValue(litePoint, yukonMetric.getTimestamp().toInstant(),
                    // Double.valueOf(yukonMetric.getValue().toString()));
                }
            } catch (Exception e) {
                log.error("Error occurred while generating point data.", e);
            }
        }
    }

    private boolean shouldGeneratePointData(YukonMetricPointInfo pointInfo) {
        try {
            YukonMetricPointDataType.valueOf(pointInfo.name());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

}
