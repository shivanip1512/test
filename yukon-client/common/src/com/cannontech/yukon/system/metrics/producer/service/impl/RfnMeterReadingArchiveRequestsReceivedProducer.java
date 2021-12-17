package com.cannontech.yukon.system.metrics.producer.service.impl;

import java.util.Random;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricThresholdProducer;
import com.cannontech.yukon.system.metrics.publisher.YukonMetricPublisher;

public class RfnMeterReadingArchiveRequestsReceivedProducer extends YukonMetricThresholdProducer {
    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadingArchiveRequestsReceivedProducer.class);
    private static final int thresholdLevel = 95;

    @Autowired private YukonMetricPublisher publisher;

    /**
     * Consider this producer is publishing the data on threshold limit. It continuously watch the data, once the threshold
     * exceeds 95%, it push the data.
     */
    @Override
    public YukonMetric produce() {
        YukonMetric metric = new YukonMetric();
        metric.setPointInfo(getYukonMetricPointInfo());
        metric.setValue(10);
        metric.setTimestamp(new DateTime());
        debug(metric, log);
        publisher.publish(metric);
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    @Override
    public boolean watch() {
        // Logic to continuously monitor a data. Can use a thread to monitor and notify the once reached the threshold value
        if (shouldProduce()) {
            Random random = new Random();
            int percentage = random.nextInt(100);
            if (percentage > thresholdLevel) {
                log.info("Threashold Reached to {}, so publishing data to the topic.", percentage);
                return true;
            }
        }
        return false;
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUEST_RECEIVED;
    }
}