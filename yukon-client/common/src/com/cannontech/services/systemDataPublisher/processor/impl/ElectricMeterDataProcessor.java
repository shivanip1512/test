package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;

@Service
public class ElectricMeterDataProcessor extends PaoCountDataProcessor {

    @Override
    public Set<PaoType> getSupportedPaoTypes() {
        return PaoType.getRfMeterTypes();
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.ELECTRIC_METER_COUNT;
    }

    @Override
    public long getPeriodInMinutes() {
        return 360;
    }
}
