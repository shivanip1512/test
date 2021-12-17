package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public abstract class PaoCountDataProcessor extends YukonMetricIntervalProducer {

    @Autowired PaoDao paoDao;

    @Override
    public YukonMetric produce() {
        int value = getData();
        YukonMetric metric = new YukonMetric();
        metric.setValue(value);
        metric.setPointInfo(getYukonMetricPointInfo());
        metric.setTimestamp(new DateTime());
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    /**
     * Make DAO call to get data
     */
    private int getData() {
        Set<PaoType> paoTypes = getSupportedPaoTypes();
        int paoCount = paoDao.getEnabledPaoCount(paoTypes);
        return paoCount;
    }

    public abstract Set<PaoType> getSupportedPaoTypes();
}
