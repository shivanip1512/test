package com.cannontech.services.systemDataPublisher.processor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;

@Service
public class ElectricMeterDataProcessor extends DeviceGroupDataProcessor {

    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.ELECTRIC_METER_COUNT;
    }

    @Override
    public long getPeriodInMinutes() {
        return 360;
    }

    @Override
    public String getDeviceGroupName() {
        return deviceGroupEditorDao.getFullPath(SystemGroupEnum.SERVICE_ACTIVE_RF_ELECTRIC_METERS);
    }
}