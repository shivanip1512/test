package com.cannontech.yukon.system.metrics.producer.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.publisher.YukonMetricPublisher;

/**
 * Base class for the producers which will produce and publish the data on specified time interval.
 */
public abstract class YukonMetricIntervalProducer implements YukonMetricProducer {
    @Autowired private YukonMetricPublisher publisher;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new YukonMetricThreadFactory());

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                YukonMetric metric = produce();
                if (metric != null) {
                    publisher.publish(metric);
                }
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