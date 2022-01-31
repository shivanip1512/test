package com.cannontech.services.systemDataPublisher.processor.impl;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.version.VersionTools;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public class YukonVersionDataProcessor extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(YukonVersionDataProcessor.class);

    @Override
    public YukonMetric produce() {
        YukonMetric metric = new YukonMetric(getYukonMetricPointInfo(), VersionTools.getYukonDetails(), new DateTime());
        debug(metric, log);
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.YUKON_VERSION;
    }

    @Override
    public long getPeriodInMinutes() {
        return 1440;
    }

}