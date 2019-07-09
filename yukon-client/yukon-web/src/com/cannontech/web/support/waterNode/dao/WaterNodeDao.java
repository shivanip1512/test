package com.cannontech.web.support.waterNode.dao;

import java.io.IOException;
import java.util.List;

import com.cannontech.web.support.waterNode.details.WaterNodeDetails;

import java.time.Instant;

public interface WaterNodeDao {

    /**
     * Creates WaterNodeDetails list that contains the details of water nodes
     * @param startTime the start of the interval for which data is being analyzed
     * @param stopTime the end of the interval for which data is being analyzed
     * @return ArrayList of WaterNodeDetails which have the voltage data and details of water nodes
     * @throws IOException 
     */
    public List<WaterNodeDetails> getWaterNodeDetails(Instant startTime, Instant stopTime);
}
