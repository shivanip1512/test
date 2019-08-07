package com.cannontech.web.support.waterNode.csvDao;

import java.io.File;
import java.util.List;

import org.joda.time.Instant;

import com.cannontech.web.support.waterNode.details.WaterNodeDetails;
import com.cannontech.web.support.waterNode.voltageDetails.VoltageDetails;

public interface WaterNodeCSVDao {

    /**
     * Creates WaterNodeDetails list that contains the details of water nodes
     * @param startTime the start of the interval for which data is being analyzed
     * @param stopTime the end of the interval for which data is being analyzed
     * @return ArrayList of WaterNodeDetails which have the voltage data and details of water nodes
     */
    public List<WaterNodeDetails> getWaterNodeDetails(Instant startTime, Instant stopTime, File file);
    
    /**
     * @param startTime
     * @param stopTime
     * @return ArrayList of VoltageDetails which have the voltage data and serial number of the water nodes
     */
    public List<VoltageDetails> getVoltageData(Instant startTime, Instant stopTime);
}
