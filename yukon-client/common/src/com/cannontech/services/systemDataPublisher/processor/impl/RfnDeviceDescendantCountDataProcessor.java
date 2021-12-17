package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public abstract class RfnDeviceDescendantCountDataProcessor extends YukonMetricIntervalProducer {

    @Autowired RfnDeviceDao rfnDeviceDao;

    @Override
    public YukonMetric produce() {
        YukonMetric metric = new YukonMetric();
        metric.setPointInfo(getYukonMetricPointInfo());
        metric.setTimestamp(new DateTime());
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    abstract Set<PaoType> getSupportedPaoTypes();
}
