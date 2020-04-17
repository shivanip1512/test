package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public class OtherDataProcessor implements SystemDataProcessor {

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        return SystemDataProcessorHelper.processOtherData(cloudDataConfiguration);
    }

    @Override
    public boolean supportsField(FieldType field) {
        return false;
    }

}
