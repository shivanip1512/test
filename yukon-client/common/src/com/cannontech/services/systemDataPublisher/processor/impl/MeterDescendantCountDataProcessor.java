package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;

@Service
public class MeterDescendantCountDataProcessor extends RfnDeviceDescendantCountDataProcessor {

    @Override
    public boolean supportsField(FieldType field) {
        return field == FieldType.HIGHEST_METER_DESCEDANT_COUNT_DATA;
    }

    @Override
    Set<PaoType> getSupportedPaoTypes() {
        return PaoType.getRfMeterTypes();
    }
}
