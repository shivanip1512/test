package com.cannontech.development.dao;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.development.dao.impl.RphSimulatorDaoImpl.RphSimulatorPointType;

public interface RphSimulatorDao {
    
    /**
     * Insert point data for existing devices.
     */
    void insertPointData(List<Integer> devicesId, RphSimulatorPointType type, double valueLow, double valueHigh,
            Instant start, Instant stop, Duration standardDuration);
   
}
