package com.cannontech.common.chart.service.impl;

import java.util.List;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartDataConverter;

/**
 * Default chart data converter - leaves data as is
 */
public class ChartDefaultConverter implements ChartDataConverter {

    public List<ChartValue<Double>> convertValues(List<ChartValue<Double>> chartValues,
            ChartInterval interval) {
        return chartValues;
    }

}
