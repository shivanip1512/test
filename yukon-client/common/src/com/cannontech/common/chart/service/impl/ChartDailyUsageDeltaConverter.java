package com.cannontech.common.chart.service.impl;

import java.util.List;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;

public class ChartDailyUsageDeltaConverter extends ChartNormalizedDeltaConverter {

    @Override
    public List<ChartValue<Double>> convertValues(List<ChartValue<Double>> chartValues,
                                                  ChartInterval interval) {
        
        return super.convertValues(chartValues, interval);
    }

}
