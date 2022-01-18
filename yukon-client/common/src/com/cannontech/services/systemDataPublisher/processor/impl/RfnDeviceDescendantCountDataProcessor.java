package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public abstract class RfnDeviceDescendantCountDataProcessor extends YukonMetricIntervalProducer {
    private static final Logger log = YukonLogManager.getLogger(RfnDeviceDescendantCountDataProcessor.class);

    @Autowired private RfnDeviceDao rfnDeviceDao;

    @Override
    public YukonMetric produce() {
        YukonMetric metric = new YukonMetric(getYukonMetricPointInfo(),
                rfnDeviceDao.findDeviceDescendantCountDataForPaoTypes(getSupportedPaoTypes()), new DateTime());
        debug(metric, log);
        return metric;
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    abstract Set<PaoType> getSupportedPaoTypes();
}
