package com.cannontech.yukon.system.metrics.producer.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.publisher.YukonMetricPublisher;

public abstract class YukonMetricStartupProducer implements YukonMetricProducer {
    @Autowired private YukonMetricPublisher publisher;

    @PostConstruct
    public void init() {
        YukonMetric metric = produce();
        if (metric != null) {
            publisher.publish(metric);
        }
    }
}