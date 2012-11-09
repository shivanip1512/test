package com.cannontech.amr.waterMeterLeak.service;

import java.util.List;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.amr.waterMeterLeak.model.WaterMeterLeak;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.user.YukonUserContext;

public interface WaterMeterLeakService {
    List<WaterMeterLeak> getWaterMeterLeaks(Set<SimpleDevice> devices, Instant fromDate,
                                            Instant toDate, boolean includeDisabledPaos,
                                            double threshold,
                                            YukonUserContext userContext,
                                            boolean useOldMethod);

    List<WaterMeterLeak> getWaterMeterLeakIntervalData(Set<SimpleDevice> devices,
                                                       Instant fromDate,
                                                       Instant toDate,
                                                       boolean includeDisabledPaos,
                                                       double threshold,
                                                       YukonUserContext userContext,
                                                       boolean useOldMethod);
}
