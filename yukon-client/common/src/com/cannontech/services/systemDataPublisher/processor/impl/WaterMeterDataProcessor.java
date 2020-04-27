package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;

@Service
public class WaterMeterDataProcessor extends PaoCountDataProcessor {

    @Override
    public Set<PaoType> getSupportedPaoTypes() {
        return PaoType.getWaterMeterTypes();
    }

    @Override
    public boolean supportsField(FieldType field) {
        return field == FieldType.WATER_METER_COUNT;
    }
}
