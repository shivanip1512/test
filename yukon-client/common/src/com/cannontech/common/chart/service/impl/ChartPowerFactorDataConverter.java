package com.cannontech.common.chart.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartDataConverter;

/**
 * 
 * Does not modify list of values passed in. 
 * 
 * Returns new list of values modified to display correctly with mixed leading and lagging values.
 *
 */
public class ChartPowerFactorDataConverter implements ChartDataConverter {
    
	public List<ChartValue<Double>> convertValues(List<ChartValue<Double>> chartValues,
            ChartInterval interval) {
        
		List<ChartValue<Double>> chartValuesCopy = new ArrayList<ChartValue<Double>>(chartValues);

        for (ChartValue<Double> chartValue : chartValuesCopy) {
        	Double origVal;
        	double val = origVal = chartValue.getValue();
        	
            if(val < 0) {
            	val += 2;
            }
            val *= 100;

            chartValue.setValue(val);
            chartValue.setDescription("<div>" + chartValue.getFormattedValue() + "</div>" + chartValue.getDescription()
                + "\nRaw Value: " + origVal.toString());
        }

        return chartValuesCopy;
    }
	
}
