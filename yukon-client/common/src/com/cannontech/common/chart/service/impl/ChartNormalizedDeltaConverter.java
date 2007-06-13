package com.cannontech.common.chart.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartDataConverter;

/**
 * ChartDataConverter which converts raw usage values into a list of usage
 * deltas
 */
public class ChartNormalizedDeltaConverter implements ChartDataConverter {

    public List<ChartValue<Double>> convertValues(List<ChartValue<Double>> chartValues,
            ChartInterval interval) {

        List<ChartValue<Double>> convertedValues = new ArrayList<ChartValue<Double>>();

        Double previousValue = null;
        Long previousTime = null;
        for (ChartValue<Double> chartValue : chartValues) {

            double currVal = chartValue.getValue();
            long currTime = chartValue.getId();

            if (previousValue != null && previousTime != null) {

                double deltaValue = currVal - previousValue;

                // Only use value if delta is positive - usage should always grow
                if (deltaValue >= 0) {
                    long millisecondDelta = currTime - previousTime;
                    // Convert time delta to days (86400000 milliseconds in a day)
                    double deltaDays = ((double)millisecondDelta) / 86400000;

                    double kwhPerDay = deltaValue / deltaDays;
                    chartValue.setValue(kwhPerDay);
                    convertedValues.add(chartValue);
                }
            }

            previousValue = currVal;
            previousTime = currTime;
        }

        return convertedValues;
    }

}
