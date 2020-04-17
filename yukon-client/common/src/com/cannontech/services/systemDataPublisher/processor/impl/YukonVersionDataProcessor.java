package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.stereotype.Service;

import com.cannontech.common.version.VersionTools;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public class YukonVersionDataProcessor extends YukonDataProcessor {

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        String yukonVersion = VersionTools.getYukonDetails();
        SystemData systemData = SystemDataProcessorHelper.buildSystemData(cloudDataConfiguration, yukonVersion);
        return systemData;
    }

    @Override
    public boolean supportsField(FieldType field) {
        return field == FieldType.YUKON_VERSION;
    }
}
