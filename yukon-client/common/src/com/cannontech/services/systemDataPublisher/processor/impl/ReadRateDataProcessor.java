package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public abstract class ReadRateDataProcessor extends YukonDataProcessor {

    @Autowired SystemDataPublisherDao publisherDao;

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        int readRate = getData();
        SystemData systemData = SystemDataProcessorHelper.buildSystemData(cloudDataConfiguration, Integer.toString(readRate));
        return systemData;
    }

    /**
     * Make DAO call to get data
     */
    private int getData() {
        String groupName = getDeviceGroupName();
        int readRate = publisherDao.getReadRate(groupName);
        return readRate;
    }

    public abstract String getDeviceGroupName();
}
