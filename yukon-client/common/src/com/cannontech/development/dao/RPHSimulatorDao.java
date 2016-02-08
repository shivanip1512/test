package com.cannontech.development.dao;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;

public interface RPHSimulatorDao {
    
    /**
     * Insert point data for existing devices.
     */
    void insertPointData(List<Integer> devicesId, String type, double valueLow, double valueHigh,
            Instant start, Instant stop, Duration standardDuration);
   
}
