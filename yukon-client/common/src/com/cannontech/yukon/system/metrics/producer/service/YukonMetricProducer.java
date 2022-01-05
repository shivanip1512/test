package com.cannontech.yukon.system.metrics.producer.service;

import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;

public interface YukonMetricProducer {

    /**
     * Generate Yukon metric data.
     */
    YukonMetric produce();

    /**
     * specify whether the producer need to produce the data in the current system or not.
     */
    boolean shouldProduce();

    /**
     * Return Yukon Metric Point Info.
     */
    public abstract YukonMetricPointInfo getYukonMetricPointInfo();
}
