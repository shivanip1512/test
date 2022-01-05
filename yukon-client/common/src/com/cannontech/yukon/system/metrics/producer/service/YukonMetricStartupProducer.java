package com.cannontech.yukon.system.metrics.producer.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.publisher.YukonMetricPublisher;

public abstract class YukonMetricStartupProducer implements YukonMetricProducer {
    @Autowired private YukonMetricPublisher publisher;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new YukonMetricThreadFactory());

    @PostConstruct
    public void init() {
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                YukonMetric metric = produce();
                if (metric != null) {
                    publisher.publish(metric);
                }
            }
        }, 1, TimeUnit.MINUTES);
    }
}