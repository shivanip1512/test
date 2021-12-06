package com.cannontech.yukon.system.metrics.producer.service;

public interface YukonMetricProducer {

    /**
     * Generate and publish the data.
     */
    void produceAndPublish();

    /**
     * specify whether the producer need to produce the data in the current system or not.
     */
    boolean shouldProduce();
}
