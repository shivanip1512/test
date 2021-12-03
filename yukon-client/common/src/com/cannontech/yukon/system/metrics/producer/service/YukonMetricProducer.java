package com.cannontech.yukon.system.metrics.producer.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.publisher.YukonMetricPublisher;

public abstract class YukonMetricProducer {
    private static final Logger log = YukonLogManager.getLogger(YukonMetricProducer.class);
    @Autowired private YukonMetricPublisher publisher;
    private ScheduledExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    YukonMetric metric = produce();
                    if (metric != null) {
                        log.debug("Publishing Yukon metric: {}", JsonUtils.toJson(metric));
                        publisher.publish(metric);
                    }
                } catch (Exception e) {
                    log.error("Error occurred while publishing Yukon metric", e);
                }
            }
        }, 1, getPeriodInMinutes(), TimeUnit.MINUTES);
    }

    /**
     * Generate the data which needs to be pushed.
     */
    abstract public YukonMetric produce();

    /**
     * Specify the interval in which data will be pushed.
     */
    abstract public long getPeriodInMinutes();

    /**
     * specify whether the producer need to produce the data in the current system or not.
     */
    abstract public boolean shouldProduce();
}
