package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.device.data.collection.model.DataCollectionSummary;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public abstract class DataCompletenessProcessor extends YukonDataProcessor {

    @Autowired SystemDataPublisherDao publisherDao;

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        int dataCompleteness = getData();
        SystemData systemData = SystemDataProcessorHelper.buildSystemData(cloudDataConfiguration,
                Integer.toString(dataCompleteness));
        return systemData;
    }

    /**
     * Make DAO call to get data
     */
    private int getData() {
        String deviceGroupName = getDeviceGroupName();
        DataCollectionSummary summary = publisherDao.getDataCompleteness(deviceGroupName);
        return summary.getExpected().getDeviceCount();
    }

    public abstract String getDeviceGroupName();
}
