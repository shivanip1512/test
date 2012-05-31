package com.cannontech.amr.waterMeterLeak.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.amr.waterMeterLeak.model.WaterMeterLeak;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.user.YukonUserContext;

public interface WaterMeterLeakService {
    List<WaterMeterLeak> getWaterMeterLeaks(Set<SimpleDevice> devices, Date fromDate,
                                            Date toDate, boolean includeDisabledPaos,
                                            double threshold,
                                            YukonUserContext userContext);

    List<WaterMeterLeak> getWaterMeterLeakIntervalData(Set<SimpleDevice> devices,
                                                       Date fromDate,
                                                       Date toDate,
                                                       boolean includeDisabledPaos,
                                                       double threshold,
                                                       YukonUserContext userContext);
}
