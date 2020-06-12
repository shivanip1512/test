package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.google.common.collect.ImmutableSet;

@Service
public abstract class DataCompletenessProcessor extends YukonDataProcessor {

    @Autowired SystemDataPublisherDao publisherDao;

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        double dataCompleteness = getData();
        SystemData systemData = SystemDataProcessorHelper.buildSystemData(cloudDataConfiguration,
                Double.toString(dataCompleteness));
        return systemData;
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
     *  This method returns the set of Pao types which belongs to the device group
     */
    protected abstract ImmutableSet<PaoType> getPaotype();
}
