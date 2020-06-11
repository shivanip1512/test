package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;
import com.google.common.collect.ImmutableSet;

@Service
public class ElectricDataCompletenessProcessor extends DataCompletenessProcessor {

    @Override
    public String getDeviceGroupName() {
        return "/System/Meters/All Meters/All RFN Meters/All RF Electric Meters";
    }

    @Override
    public ImmutableSet<PaoType> getPaotype() {
        return PaoType.getRfElectricTypes();
    }

    @Override
    public boolean supportsField(FieldType field) {
        return field == FieldType.DATA_COMPLETENESS_ELECTRIC;
    }

}
