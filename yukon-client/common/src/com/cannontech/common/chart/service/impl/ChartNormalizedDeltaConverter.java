package com.cannontech.common.chart.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartDataConverter;

/**
 * ChartDataConverter which converts raw usage values into a list of usage
 * deltas.
 * Does not modify list of values passed in. 
 * Returns new list of ChartValue that is a copy of original list but the new ChartValues
 * have normalized value/formattedValue.
 */
public class ChartNormalizedDeltaConverter implements ChartDataConverter {

    private static final Logger log = YukonLogManager.getLogger(ChartNormalizedDeltaConverter.class);
    
    public List<ChartValue<Double>> convertValues(List<ChartValue<Double>> chartValues,
            ChartInterval interval) {
        
        NumberFormat pointValueFormat = new DecimalFormat();
        pointValueFormat.setGroupingUsed(false);

        List<ChartValue<Double>> chartValuesCopy = new ArrayList<ChartValue<Double>>(chartValues);
        List<ChartValue<Double>> convertedValues = new ArrayList<ChartValue<Double>>();
        

        Double previousValue = null;
        Long previousTime = null;
        for (ChartValue<Double> chartValue : chartValuesCopy) {

            double currVal = chartValue.getValue();
            long currTime = chartValue.getTime();

            if (previousValue != null && previousTime != null) {

                double deltaValue = currVal - previousValue;

                // Only use value if delta is positive - usage should always grow
                if (deltaValue >= 0) {
                    long millisecondDelta = currTime - previousTime;
                    // Convert time delta to days (86400000 milliseconds in a day)
                    double deltaDays = ((double)millisecondDelta) / 86400000;
                    double kwhPerDay = deltaValue / deltaDays;
                    log.debug("deltaValue: " + deltaValue + " MillisecondDelta: " + millisecondDelta + "  DeltaDays: " + deltaDays + "  kWhPerDay: " + kwhPerDay);
                    chartValue.setValue(kwhPerDay);
                    chartValue.setFormattedValue(pointValueFormat.format(kwhPerDay));
                    log.debug(chartValue.toString());
                    convertedValues.add(chartValue);
                }
            }

            previousValue = currVal;
            previousTime = currTime;
        }

        return convertedValues;
    }

}
