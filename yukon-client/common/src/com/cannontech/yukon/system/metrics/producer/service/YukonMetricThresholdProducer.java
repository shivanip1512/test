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
 * Base class for the producers which will produce and publish the data based on threshold value. This will compute and then
 * compare the data with threshold value in every 30 seconds. If the computed value is exceeds the threshold limit, Will generate
 * and publish Yukon Metrics data.
 */
public abstract class YukonMetricThresholdProducer implements YukonMetricProducer {
    @Autowired private YukonMetricPublisher publisher;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new YukonMetricThreadFactory());

    @PostConstruct
    public void initScheduler() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                YukonMetric metric = checkPreviousValue();
                if (metric != null) {
                    publisher.publish(metric);
                }
                boolean shouldProduce = watch();
                if (shouldProduce) {
                    publisher.publish(produce());
                }
            }
        }, 15, 30, TimeUnit.SECONDS);

        if (shouldGenerateIntervalData()) {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    publisher.publish(produce());
                }
            }, 1, getPeriodInMinutes(), TimeUnit.MINUTES);
        }
    }

    /**
     * Compute the data and compare the data with the threshold value & verify the escape valve condition in a regular interval.
     * Once exceeded the threshold level & escape valve conditions are met, notify the publisher to generate and publish the
     * metric data.
     */
    public abstract boolean watch();

    /**
     * Identify whether this producer will generate interval data along with threshold data.
     */
    public abstract boolean shouldGenerateIntervalData();

    /**
     * Define data generation interval if this producer is producing interval data.
     */
    public abstract long getPeriodInMinutes();

    /**
     * Return previous metric details if it goes above threshold level or if goes below threshold value. This is to identify the
     * values just before reaching the threshold limit and just after dropping from the threshold value.
     */
    protected abstract YukonMetric checkPreviousValue();

    /**
     * Debug message for threshold data.
     */
    public void debug(YukonMetric metric, Logger log, Object thresholdValue) {
        if (log.isDebugEnabled()) {
            log.debug("Publishing Yukon Metric threshold data {} to the topic as value exceeds threshold value {}.", metric,
                    thresholdValue);
        }
    }

    /**
     * Debug message for interval data.
     */
    public void debug(YukonMetric metric, Logger log) {
        if (log.isDebugEnabled()) {
            log.debug("Publishing Yukon Metric Interval data {} to the topic", metric);
        }
    }

}
