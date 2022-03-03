package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.yukon.system.metrics.message.YukonMetricPointInfo;

@Service
public class LCRDataProcessor extends PaoCountDataProcessor {

    @Override
    public Set<PaoType> getSupportedPaoTypes() {
        return PaoType.getRfLcrTypes();
    }

    @Override
    public YukonMetricPointInfo getYukonMetricPointInfo() {
        return YukonMetricPointInfo.RFN_LCR;
    }

    @Override
    public long getPeriodInMinutes() {
        return 360;
    }
}
