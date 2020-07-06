package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.services.systemDataPublisher.service.model.SystemDataFieldType.FieldType;
import com.google.common.collect.ImmutableSet;

@Service
public class WaterDataCompletenessProcessor extends DataCompletenessProcessor {
    @Autowired DeviceGroupEditorDao deviceGroupEditorDao;

    @Override
    public String getDeviceGroupName() {
        return deviceGroupEditorDao.getFullPath(SystemGroupEnum.SERVICE_ACTIVE_RFW_METERS);
    }

    @Override
    public boolean supportsField(FieldType field) {
        return field == FieldType.DATA_COMPLETENESS_WATER;
    }

    @Override
    public ImmutableSet<PaoType> getPaotype() {
        return PaoType.getWaterMeterTypes();
    }
}
