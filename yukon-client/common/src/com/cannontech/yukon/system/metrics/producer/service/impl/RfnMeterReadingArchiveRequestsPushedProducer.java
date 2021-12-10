package com.cannontech.yukon.system.metrics.producer.service.impl;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;
import com.cannontech.yukon.system.metrics.publisher.YukonMetricPublisher;

public class RfnMeterReadingArchiveRequestsPushedProducer extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadingArchiveRequestsPushedProducer.class);
    @Autowired private YukonMetricPublisher publisher;

    /**
     * Consider this producer is publishing the data in specified interval. It generate and publish the data in every 5 minutes.
     */
    @Override
    public void produceAndPublish() {
        if (shouldProduce()) {
            YukonMetric metric = new YukonMetric();
            metric.setPointInfo(YukonMetricPointInfo.RFN_METER_READING_ARCHIVERE_REQUEST_PUSHED);
            metric.setValue(10);
            metric.setTimestamp(new DateTime());
            debug(metric, log);
            publisher.publish(metric);
        }
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    @Override
    public long getPeriodInMinutes() {
        return 5;
    }
}