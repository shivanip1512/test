package com.cannontech.services.systemDataPublisher.processor.impl;

import java.text.DecimalFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public abstract class ReadRateDataProcessor extends YukonDataProcessor {
    
    private static DecimalFormat percentageFormat = new DecimalFormat("0.0");
    @Autowired SystemDataPublisherDao publisherDao;

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        String readRate = getData();
        SystemData systemData = SystemDataProcessorHelper.buildSystemData(cloudDataConfiguration, readRate);
        return systemData;
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
