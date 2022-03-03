package com.cannontech.services.systemDataPublisher.processor.impl;

import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.yukon.system.metrics.message.YukonMetric;
import com.cannontech.yukon.system.metrics.producer.service.YukonMetricIntervalProducer;

@Service
public abstract class ReadRateDataProcessor extends YukonMetricIntervalProducer {

    private static final Logger log = YukonLogManager.getLogger(ReadRateDataProcessor.class);
    private static DecimalFormat percentageFormat = new DecimalFormat("0.0");
    @Autowired private SystemDataPublisherDao publisherDao;

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
    private String getData() {
        String groupName = getDeviceGroupName();
        DataCollectionSummary summary = publisherDao.getReadRate(groupName);
        return percentageFormat.format((summary.getAvailable().getPercentage()));
    }

    public abstract String getDeviceGroupName();
}
