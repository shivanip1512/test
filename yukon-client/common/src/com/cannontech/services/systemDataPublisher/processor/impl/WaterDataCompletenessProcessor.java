package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;

@Service
public class WaterDataCompletenessProcessor extends DataCompletenessProcessor {

    @Override
    public String getDeviceGroupName() {
        return "/Service/Active RFW Meters";
    }

    @Override
    public boolean supportsField(FieldType field) {
        return field == FieldType.DATA_COMPLETENESS_WATER;
    }
}
