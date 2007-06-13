package com.cannontech.common.chart.service;

import java.util.List;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;

/**
 * Interface used to convert raw chart data into a list of chartable data
 */
public interface ChartDataConverter {

    public List<ChartValue<Double>> convertValues(List<ChartValue<Double>> chartValues,
            ChartInterval interval);
}
