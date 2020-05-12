package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private int getData() {
        String deviceGroupName = getDeviceGroupName();
        int dataCompleteness = publisherDao.getDataCompleteness(deviceGroupName);
        return dataCompleteness;
    }

    public abstract String getDeviceGroupName();
}
