package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public abstract class PaoCountDataProcessor extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(PaoCountDataProcessor.class);

    @Autowired private PaoDao paoDao;

    @Override
    public YukonMetric produce() {
        YukonMetric metric = new YukonMetric(getYukonMetricPointInfo(), getData(), new DateTime());
        debug(metric, log);
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
