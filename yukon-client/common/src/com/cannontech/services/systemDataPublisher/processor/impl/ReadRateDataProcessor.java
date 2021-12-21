package com.cannontech.services.systemDataPublisher.processor.impl;

import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public abstract class ReadRateDataProcessor extends YukonMetricIntervalProducer {

    private static DecimalFormat percentageFormat = new DecimalFormat("0.0");
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
    private String getData() {
        String groupName = getDeviceGroupName();
        DataCollectionSummary summary = publisherDao.getReadRate(groupName);
        return percentageFormat.format((summary.getAvailable().getPercentage()));
    }

    public abstract String getDeviceGroupName();
}
