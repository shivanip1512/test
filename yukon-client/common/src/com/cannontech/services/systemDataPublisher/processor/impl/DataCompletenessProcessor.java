package com.cannontech.services.systemDataPublisher.processor.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;
import com.google.common.collect.ImmutableSet;

@Service
public abstract class DataCompletenessProcessor extends YukonMetricIntervalProducer {

    @Autowired SystemDataPublisherDao publisherDao;

    @Override
    public YukonMetric produce() {
        return new YukonMetric(getYukonMetricPointInfo(), getData(), new DateTime());
    }

    @Override
    public boolean shouldProduce() {
        return true;
    }

    /**
     * Make DAO call to get data
     */
    private double getData() {
        String deviceGroupName = getDeviceGroupName();
        return publisherDao.getDataCompleteness(deviceGroupName, getPaotype());
    }

    public abstract String getDeviceGroupName();

    /**
     * This method returns the set of Pao types which belongs to the device group
     */
    protected abstract ImmutableSet<PaoType> getPaotype();
}
