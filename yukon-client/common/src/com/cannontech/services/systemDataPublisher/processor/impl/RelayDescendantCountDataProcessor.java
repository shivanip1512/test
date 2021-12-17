package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;

@Service
public class RelayDescendantCountDataProcessor extends RfnDeviceDescendantCountDataProcessor {

    @Override
    Set<PaoType> getSupportedPaoTypes() {
        return PaoType.getRfRelayTypes();
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.HIGHEST_RELAY_DESCEDANT_COUNT_DATA;
    }

    @Override
    public long getPeriodInMinutes() {
        return 360;
    }
}
