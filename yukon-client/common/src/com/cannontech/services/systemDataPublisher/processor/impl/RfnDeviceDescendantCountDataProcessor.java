package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.service.model.RfnDeviceDescendantCountData;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public abstract class RfnDeviceDescendantCountDataProcessor extends YukonDataProcessor {

    @Autowired RfnDeviceDao rfnDeviceDao;

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        RfnDeviceDescendantCountData data = rfnDeviceDao.findDeviceDescendantCountDataForPaoTypes(getSupportedPaoTypes());
        SystemData systemData = null;
        if (data != null) {
            systemData = SystemDataProcessorHelper.buildSystemData(cloudDataConfiguration, data);
        }
        return systemData;
    }

    abstract Set<PaoType> getSupportedPaoTypes();
}
