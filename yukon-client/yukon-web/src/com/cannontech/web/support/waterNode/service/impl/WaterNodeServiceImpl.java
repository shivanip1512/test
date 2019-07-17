package com.cannontech.web.support.waterNode.service.impl;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.web.support.waterNode.batteryLevel.WaterNodeBatteryLevel;
import com.cannontech.web.support.waterNode.dao.WaterNodeDao;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.service.WaterNodeService;

public class WaterNodeServiceImpl implements WaterNodeService {
    private final WaterNodeDao waterNodeDao;

    @Autowired
    public WaterNodeServiceImpl(@Qualifier("database") WaterNodeDao waterNodeDao) {
        this.waterNodeDao = waterNodeDao;
    }

    @Override
    public List<WaterNodeDetails> getAnalyzedNodes(Instant intervalStart, Instant intervalEnd) {
        List<WaterNodeDetails> fullNodeData = waterNodeDao.getWaterNodeDetails(intervalStart, intervalEnd);
        List<WaterNodeDetails> prunedResults = pruneResultsList(fullNodeData);
        List<WaterNodeDetails> analyzedRows = analyzeBatteryData(prunedResults, intervalStart, intervalEnd);
        return analyzedRows;
    }

    @Override
    public List<String[]> getDetailedReportRows(Instant intervalStart, Instant intervalEnd) {
        return null; //This will be the detailed voltage data report
    }

    //Removes data taken more frequently than 1 reading/hr
    private List<WaterNodeDetails> pruneResultsList(List<WaterNodeDetails> resultsList) {
        ArrayList<Double> voltages;
        ArrayList<Instant> timestamps;

        for (WaterNodeDetails waterNodeDetails : resultsList) {
            voltages = waterNodeDetails.getVoltages();
            timestamps = waterNodeDetails.getTimestamps();

            ListIterator<Instant> timestampIterator = timestamps.listIterator();
            Instant currentTimestamp;
            Instant prevTimestamp = timestampIterator.next();

            while (timestampIterator.hasNext()) {
                currentTimestamp = timestampIterator.next();

                while (currentTimestamp.minus(Duration.standardMinutes(55)).isBefore(prevTimestamp)
                       && timestampIterator.hasNext()) {

                    voltages.remove(timestampIterator.nextIndex() - 1);
                    timestampIterator.remove();
                    currentTimestamp = timestampIterator.next();
                }
                
                //examine final element in the list
                if (currentTimestamp.minus(Duration.standardMinutes(55)).isBefore(prevTimestamp)) {
                    voltages.remove(timestampIterator.nextIndex() - 1);
                    timestampIterator.remove();
                }
                prevTimestamp = currentTimestamp;
            }
        }
        return resultsList;
    }

    private List<WaterNodeDetails> analyzeBatteryData(List<WaterNodeDetails> hourlyResults,
                                                      Instant intervalStart, Instant intervalEnd) {
        hourlyResults.forEach(details -> {
            setHighSleepingCurrent(details);
            setNodeCategory(details, intervalEnd);
        });
        return hourlyResults;
    }

    //runs a linear regression on the voltage/time data to determine the mV/hr of battery drain
    private void setHighSleepingCurrent(WaterNodeDetails waterNodeDetails) {
        ArrayList<Double> voltages = waterNodeDetails.getVoltages();
        ArrayList<Instant> timestamps = waterNodeDetails.getTimestamps();

        int numValues = timestamps.size();
        long timestampSum = 0;
        double voltageSum = 0.0;
        ListIterator<Instant> timestampIterator = timestamps.listIterator();

        // calculate average voltage and time
        while (timestampIterator.hasNext()) {
            voltageSum += voltages.get(timestampIterator.nextIndex());
            timestampSum += timestampIterator.next().getMillis();
        }
        double avgVoltage = voltageSum / numValues;
        long avgTime = timestampSum / numValues;

        // run regression to determine best-fit slope
        // m = (Sum(Ti - Tavg)(Vi - Vavg)) / (Sum(Ti - Tavg)^2)
        double numeratorSum = 0.0;
        double denomSum = 0.0;
        long timeDifFromAvg; //(Ti-Tavg)

        ListIterator<Double> voltageIterator = voltages.listIterator();
        while (voltageIterator.hasNext()) {
            timeDifFromAvg = timestamps.get(voltageIterator.nextIndex()).getMillis() - avgTime;
            numeratorSum += (voltageIterator.next() - avgVoltage) * timeDifFromAvg;
            denomSum += timeDifFromAvg * timeDifFromAvg;
        }

        double regressionSlope = (double) numeratorSum / denomSum;// slope is in V/ms
        double millivoltsPerHour = regressionSlope * Duration.standardHours(1).getMillis() * 1000;
        
        if (millivoltsPerHour > -0.4 && millivoltsPerHour < -0.1) {
            waterNodeDetails.setHighSleepingCurrentIndicator(true);
        }
    }
    
    private void setNodeCategory(WaterNodeDetails waterNodeDetails, Instant intervalEnd) {
        int numTimestamps = 0;
        double sum = 0.0;
        Double currentVoltage;

        Instant averagingIntervalStart = intervalEnd.minus(Duration.standardDays(3));
        ArrayList<Double> voltages = waterNodeDetails.getVoltages();
        ArrayList<Instant> timestamps = waterNodeDetails.getTimestamps();
        ListIterator<Instant> timestampIterator = timestamps.listIterator();

        // gather data for calculation
        while (timestampIterator.hasNext()) {
            if (timestampIterator.next().isAfter(averagingIntervalStart)) {
                currentVoltage = voltages.get(timestampIterator.nextIndex() - 1);
                sum += currentVoltage;
                numTimestamps++;
            }
        }

        WaterNodeBatteryLevel level;
        
        // if fewer than half of the expected data is present, set the node to unreported
        if (numTimestamps < 36) {
            level = WaterNodeBatteryLevel.UNREPORTED;
        } else {
            double movingVoltageAverage = sum / numTimestamps;
            if (movingVoltageAverage >= WaterNodeBatteryLevel.LOW.getThreshold()) {
                level = WaterNodeBatteryLevel.NORMAL;
            } else if (movingVoltageAverage >= WaterNodeBatteryLevel.CRITICALLY_LOW.getThreshold()) {
                level = WaterNodeBatteryLevel.LOW;
            } else {
                level = WaterNodeBatteryLevel.CRITICALLY_LOW;
            }
        }
        waterNodeDetails.setBatteryLevel(level);
    }
}
