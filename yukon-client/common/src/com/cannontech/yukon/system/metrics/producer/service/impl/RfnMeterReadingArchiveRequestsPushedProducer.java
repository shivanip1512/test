package com.cannontech.yukon.system.metrics.producer.service.impl;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

public class RfnMeterReadingArchiveRequestsPushedProducer extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(RfnMeterReadingArchiveRequestsPushedProducer.class);

    /**
     * Consider this producer is publishing the data in specified interval. It generate and publish the data in every 5 minutes.
     */
    @Override
    public YukonMetric produce() {
        YukonMetric metric = null;
        if (shouldProduce()) {
            metric = new YukonMetric();
            metric.setPointInfo(getYukonMetricPointInfo());
            metric.setValue(10);
            metric.setTimestamp(new DateTime());
            debug(metric, log);
        }
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    @Override
    public long getPeriodInMinutes() {
        return 5;
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.RFN_METER_READING_ARCHIVE_REQUESTS_PUSHED;
    }
}