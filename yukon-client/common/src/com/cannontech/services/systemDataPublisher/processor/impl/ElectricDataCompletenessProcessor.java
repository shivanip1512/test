package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;
import com.google.common.collect.ImmutableSet;

@Service
public class ElectricDataCompletenessProcessor extends DataCompletenessProcessor {
    @Autowired DeviceGroupEditorDao deviceGroupEditorDao;

    @Override
    public String getDeviceGroupName() {
        return deviceGroupEditorDao.getFullPath(SystemGroupEnum.SERVICE_ACTIVE_RF_ELECTRIC_METERS);
    }

    @Override
    public ImmutableSet<PaoType> getPaotype() {
        return PaoType.getRfElectricTypes();
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.DATA_COMPLETENESS_ELECTRIC;
    }

    @Override
    public long getPeriodInMinutes() {
        return 360;
    }

}
