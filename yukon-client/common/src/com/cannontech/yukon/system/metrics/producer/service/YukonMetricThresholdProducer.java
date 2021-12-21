package com.cannontech.yukon.system.metrics.producer.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;

import com.cannontech.yukon.system.metrics.message.YukonMetric;

/**
 * Base class for the producers which will produce and publish the data based on threshold value. This will compute and then
 * compare the data with threshold value in every 30 seconds. If the computed value is exceeds the threshold limit, Will generate
 * and publish Yukon Metrics data.
 */
public abstract class YukonMetricThresholdProducer implements YukonMetricProducer {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new YukonMetricThreadFactory());

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                watchAndNotify();
            }
        }, 15, 30, TimeUnit.SECONDS);
    }

    /**
     * Compute the data and compare the data with the threshold value in a regular interval. Once exceeded the threshold level
     * notify the publisher to generate and publish the metric data.
     */
    public abstract void watchAndNotify();

    /**
     * Debug message for threshold data
     */
    public void debug(YukonMetric metric, Logger log) {
        if (log.isDebugEnabled()) {
            log.debug("Publishing Yukon Metric threshold data {} to the topic", metric);
        }
    }

}
