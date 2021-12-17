package com.cannontech.yukon.system.metrics.producer.service;

import com.cannontech.yukon.system.metrics.message.YukonMetric;

public interface YukonMetricProducer {

    /**
     * Generate Yukon metric data.
     * 
     * @return TODO
     */
    YukonMetric produce();

    /**
     * specify whether the producer need to produce the data in the current system or not.
     */
    boolean shouldProduce();
}
