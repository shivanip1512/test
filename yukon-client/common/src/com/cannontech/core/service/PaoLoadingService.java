package com.cannontech.core.service;

import java.util.List;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;


public interface PaoLoadingService {
    public List<DisplayablePao> getDisplayableDevices(Iterable<? extends YukonPao> paos);
    public List<DeviceCollectionReportDevice> getDeviceCollectionReportDevices(Iterable<? extends YukonPao> paos);
}
