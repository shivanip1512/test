package com.cannontech.yukon.system.metrics.producer.service;

import java.util.concurrent.ThreadFactory;

public class YukonMetricThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        return new Thread(r, "YukonMetrics");
    }
}
