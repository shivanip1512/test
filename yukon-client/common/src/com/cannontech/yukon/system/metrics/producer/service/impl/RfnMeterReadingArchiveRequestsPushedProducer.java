package com.cannontech.yukon.system.metrics.producer.service.impl;

import org.joda.time.DateTime;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricProducer;

public class RfnMeterReadingArchiveRequestsPushedProducer extends YukonMetricProducer {

    @Override
    public YukonMetric produce() {
        YukonMetric metric = null;
        if (shouldProduce()) {
            metric = new YukonMetric();
            metric.setFieldValue(20);
            metric.setTimestamp(new DateTime());
            metric.setAttributeName(BuiltInAttribute.RFN_METER_READING_ARCHIVERE_REQUEST_PUSHED.toString());
        }
        return metric;
    }

    @Override
    public long getPeriodInMinutes() {
        return 5;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }
}
