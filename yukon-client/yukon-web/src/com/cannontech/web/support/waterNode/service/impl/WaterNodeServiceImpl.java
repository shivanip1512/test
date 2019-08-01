package com.cannontech.web.support.waterNode.service.impl;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.web.support.waterNode.batteryLevel.WaterNodeBatteryLevel;
import com.cannontech.web.support.waterNode.dao.WaterNodeDao;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.service.WaterNodeService;
import com.cannontech.web.support.waterNode.voltageDetails.VoltageDetails;

public class WaterNodeServiceImpl implements WaterNodeService {
    private static final Logger log = YukonLogManager.getLogger(WaterNodeServiceImpl.class);
    private final WaterNodeDao waterNodeDao;
    @Autowired
    public WaterNodeServiceImpl(@Qualifier("database") WaterNodeDao waterNodeDao) {
        this.waterNodeDao = waterNodeDao;
    }

    @Override
    public List<WaterNodeDetails> getAnalyzedNodes(Instant intervalStart, Instant intervalEnd) {
        List<WaterNodeDetails> nodeData = waterNodeDao.getWaterNodeDetails(intervalStart, intervalEnd);
        log.info("Battery analysis initiated: " + nodeData.size() + " water nodes found");
        nodeData.forEach(details -> {
            pruneResultsList(details.getVoltages(), details.getTimestamps());
        });
        List<WaterNodeDetails> analyzedRows = analyzeBatteryData(nodeData, intervalStart, intervalEnd);
        return analyzedRows;
    }

    @Override
    public List<VoltageDetails> getVoltageDetails(Instant intervalStart, Instant intervalEnd) {
        List<VoltageDetails> voltageData = waterNodeDao.getVoltageData(intervalStart, intervalEnd);
        voltageData.forEach(voltageDetails -> {
            pruneResultsList(voltageDetails.getVoltages(), voltageDetails.getTimestamps());
        });
        return voltageData;
    }

    /*
     * Given parallel lists of voltage and timestamp data, this method 
     * returns the same lists ith a pruned subset of data where all 
     * voltage-timestamp pairs are spaced at least 55 minutes later than the previous pair.
     */
    private void pruneResultsList(ArrayList<Double> voltages, ArrayList<Instant> timestamps) {
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

            // examine final element in the list
            if (currentTimestamp.minus(Duration.standardMinutes(55)).isBefore(prevTimestamp)) {
                voltages.remove(timestampIterator.nextIndex() - 1);
                timestampIterator.remove();
            }
            prevTimestamp = currentTimestamp;
        }
    }
        

    /*
     * Given a list of water nodes and their voltage/timestamp data, this method returns a list
     * of the same water nodes with node battery categories and high sleeping current indicators
     * corresponding to the voltage data given for each node.
     */
    private List<WaterNodeDetails> analyzeBatteryData(List<WaterNodeDetails> hourlyResults,
                                                      Instant intervalStart, Instant intervalEnd) {
        hourlyResults.forEach(details -> {
            setHighSleepingCurrent(details);
            setNodeCategory(details, intervalEnd);
        });
        return hourlyResults;
    }

    /*
     * Given a water node and its voltage/timestamp data, this method runs a linear regression on
     * voltages to determine the rate of battery depletion of the node. Nodes are considered to 
     * have a high sleeping current if the rate is between -0.1 mV/hr and -0.4 mV/hr. If the node
     * falls into this range, the high sleeping current indicator is set to TRUE.
     */
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
        log.debug("Calculated regression slope (mV/hr): " + millivoltsPerHour);
        
        if (millivoltsPerHour > -0.4 && millivoltsPerHour < -0.1) {
            waterNodeDetails.setHighSleepingCurrentIndicator(true);
            log.debug("High sleeping current set to TRUE for " + waterNodeDetails.getName());
        }
    }
    
    /*
     * Given a water node and its voltage/timestamp data, this method calculates the average
     * battery level of the node over a three-day interval and returns the WaterNodeBatteryLevel
     * corresponding to this average value.
     */
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
        String name = waterNodeDetails.getName();
        
        // if fewer than half of the expected data is present, set the node to unreported
        if (numTimestamps < 36) {
            level = WaterNodeBatteryLevel.UNREPORTED;
            log.debug("Inadequate data found for " + name + ". " + voltages.size() + " data entries were found.");
        } else {
            double movingVoltageAverage = sum / numTimestamps;
            log.debug("Average battery voltage for " + name + ": " + movingVoltageAverage);
            if (movingVoltageAverage >= WaterNodeBatteryLevel.LOW.getThreshold()) {
                level = WaterNodeBatteryLevel.NORMAL;
            } else if (movingVoltageAverage >= WaterNodeBatteryLevel.CRITICALLY_LOW.getThreshold()) {
                level = WaterNodeBatteryLevel.LOW;
            } else {
                level = WaterNodeBatteryLevel.CRITICALLY_LOW;
            }
        }
        log.debug("Setting battery level for " + name + " to " + level.toString());
        waterNodeDetails.setBatteryLevel(level);
    }
}
