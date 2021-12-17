package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;

@Service
public class LcrDescendantCountDataProcessor extends RfnDeviceDescendantCountDataProcessor {

    @Override
    Set<PaoType> getSupportedPaoTypes() {
        return PaoType.getRfLcrTypes();
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.HIGHEST_LCR_DESCEDANT_COUNT_DATA;
    }

    @Override
    public long getPeriodInMinutes() {
        return 360;
    }
}
