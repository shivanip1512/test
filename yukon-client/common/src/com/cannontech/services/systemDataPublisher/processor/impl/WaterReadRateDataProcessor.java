package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;

@Service
public class WaterReadRateDataProcessor extends ReadRateDataProcessor {

    @Override
    public String getDeviceGroupName() {
        return "/System/Meters/All Meters/All RFN Meters/All RFW Meters";
    }

    @Override
    public boolean supportsField(FieldType field) {
        return field == FieldType.WATER_READ_RATE;
    }
}
