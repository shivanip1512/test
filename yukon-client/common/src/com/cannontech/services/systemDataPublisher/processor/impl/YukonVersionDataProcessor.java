package com.cannontech.services.systemDataPublisher.processor.impl;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.cannontech.common.version.VersionTools;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricStartupProducer;

@Service
public class YukonVersionDataProcessor extends YukonMetricStartupProducer {

    @Override
    public YukonMetric produce() {
        return new YukonMetric(getYukonMetricPointInfo(), VersionTools.getYukonDetails(), new DateTime());
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.YUKON_VERSION;
    }

}