package com.cannontech.yukon.system.metrics.listener;

import javax.annotation.PostConstruct;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class YukonMetricListener implements MessageListener {
    private static final Logger log = YukonLogManager.getLogger(YukonMetricListener.class);

    private ObjectMapper mapper;

    @PostConstruct
    public void init() {
        mapper = new ObjectMapper()
                .registerModule(new JodaModule());
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            String textMessage = null;
            try {
                textMessage = ((TextMessage) message).getText();
                YukonMetric yukonMetric = mapper.readValue(textMessage, YukonMetric.class);
                if (shouldGeneratePointData(yukonMetric.getPointInfo())) {
                    log.info("Received Yukon Metric data " + yukonMetric);
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
