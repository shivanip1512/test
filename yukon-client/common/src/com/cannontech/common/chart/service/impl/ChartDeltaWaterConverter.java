package com.cannontech.common.chart.service.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.ChartValue;
import com.cannontech.common.chart.service.ChartDataConverter;
import com.google.common.collect.ImmutableList;

/**
 * ChartDataConverter which converts raw usage values into a list of usage deltas.
 * Does not modify list of values passed in. 
 * Returns new list of ChartValue that is a copy of original list but the new ChartValues
 * have delta value/formattedValue.
 * 
 * For water, an average consumption over a day doesn't make sense, since there are several intervals that may have
 *  zero consumption. Therefore, this normalizer does not average out per day like the ChartNormalizedDeltaConverter (kWh) does.
 *  Instead, it attempts to "guess" at the expected interval based off the ChartInterval and delta between curr and prev.
 * This normalizer "discards" chartData if the delta interval is greater than expected.
 * 
 * This normalizer is fairly strict when determining the "expectedMaxDeltaInMillis". In the future, it may make sense
 *  to add an additional (acceptable) threshold past the max. ie. For an expected day, add an additional 5m past the max.
 *  This would allow for readings to be considered acceptable, even if they "just missed" the max.
 *  
 * If only daily readings actually exist, and this chartInterval is less than a day, then nothing will be returned
 *   because the expected (guess) max interval will be hour, but the true interval is a day.
 */
public class ChartDeltaWaterConverter implements ChartDataConverter {

	private Logger log = YukonLogManager.getLogger(ChartDeltaWaterConverter.class);
	
    public List<ChartValue<Double>> convertValues(List<ChartValue<Double>> chartValues,
            ChartInterval interval) {
        
        NumberFormat pointValueFormat = new DecimalFormat();
        pointValueFormat.setGroupingUsed(false);

        List<ChartValue<Double>> chartValuesCopy = ImmutableList.copyOf(chartValues);
        List<ChartValue<Double>> convertedValues = new ArrayList<ChartValue<Double>>();

        Double previousValue = null;
        Long previousTime = null;
        double expectedMaxDeltaInMillis = getExpectedMaxDeltaInMillis(interval);
        
        for (ChartValue<Double> chartValue : chartValuesCopy) {

            double currVal = chartValue.getValue();
            long currTime = chartValue.getTime();

            if (previousValue != null && previousTime != null) {

                double deltaValue = currVal - previousValue;

                // Only use value if delta is positive - usage should always grow
                if (deltaValue >= 0) {
                    long millisecondDelta = currTime - previousTime;

                    // if delta <= expectedDelta, then we'll keep the chart value.
                    if ( millisecondDelta <= expectedMaxDeltaInMillis) {
	                    chartValue.setValue(deltaValue);
	                    chartValue.setFormattedValue(pointValueFormat.format(deltaValue));
	                    convertedValues.add(chartValue);
	                    log.debug("millisecondDelta:" + millisecondDelta + "; deltaValue:" + deltaValue);
                    } else {	// otherwise discard and log
                    	log.warn("Delta is greater than expected delta...value will be skipped: " +
                    			chartValue.toString());
                    	log.debug("Interval: " + interval +
                    			"; Curr:" + new Date(currTime) + 
                    			"; Prev:" + new Date(previousTime) + 
                    			"; Millis: " + millisecondDelta + " > " + expectedMaxDeltaInMillis);
                    }
                }
            }

            previousValue = currVal;
            previousTime = currTime;
        }

        return convertedValues;
    }

    private double getExpectedMaxDeltaInMillis(ChartInterval interval) {
    	// set expectedDelta value to be relational to ChartInterval
    	double expectedDeltaInMillis = 1000 * 60 * 60;	//default hour
    	if (interval == ChartInterval.DAY) {
    		expectedDeltaInMillis = expectedDeltaInMillis * 24;	//day
    	} else if (interval == ChartInterval.WEEK) {
    		expectedDeltaInMillis = expectedDeltaInMillis * 24 * 7;	//week
    	}
    	return expectedDeltaInMillis;
    }
}
