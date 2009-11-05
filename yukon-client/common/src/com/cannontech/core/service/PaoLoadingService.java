package com.cannontech.core.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.device.model.DeviceCollectionReportDevice;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;


public interface PaoLoadingService {
    public List<DisplayablePao> getDisplayableDevices(Iterable<? extends YukonPao> paos);
    public List<DeviceCollectionReportDevice> getDeviceCollectionReportDevices(Iterable<? extends YukonPao> paos);
    public Map<PaoIdentifier, DisplayablePao> getDisplayableDeviceLookup(Iterable<? extends YukonPao> paos);
    public DisplayablePao getDisplayablePao(YukonPao pao);
}
