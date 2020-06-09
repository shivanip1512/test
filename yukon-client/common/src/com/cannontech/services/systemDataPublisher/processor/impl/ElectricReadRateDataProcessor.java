package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.stereotype.Service;

import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;

@Service
public class ElectricReadRateDataProcessor extends ReadRateDataProcessor {

    @Override
    public String getDeviceGroupName() {
        return "/Service/Active RF Electric Meters";
    }

    @Override
    public boolean supportsField(FieldType field) {
        return field == FieldType.ELECTRIC_READ_RATE;
    }
}
