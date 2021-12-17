package com.cannontech.yukon.system.metrics.producer.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;

import com.cannontech.yukon.system.metrics.message.YukonMetric;

/**
 * Base class for the producers which will produce and publish the data on specified time interval.
 */
public abstract class YukonMetricIntervalProducer implements YukonMetricProducer {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new YukonMetricThreadFactory());

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                produceAndPublish();
            }
        }, 1, getPeriodInMinutes(), TimeUnit.MINUTES);
    }

    /**
     * Specify the interval in which data will be pushed.
     */
    public abstract long getPeriodInMinutes();

    /**
     * Debug message for interval data
     */
    public void debug(YukonMetric metric, Logger log) {
        if (log.isDebugEnabled()) {
            log.debug("Publishing Yukon Metric Interval data {} to the topic", metric);
        }
    }
}