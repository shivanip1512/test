package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public abstract class PaoCountDataProcessor extends YukonDataProcessor {

    @Autowired PaoDao paoDao;

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        int paoCount = getData();
        SystemData systemData = SystemDataProcessorHelper.buildSystemData(cloudDataConfiguration, Integer.toString(paoCount));
        return systemData;
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
