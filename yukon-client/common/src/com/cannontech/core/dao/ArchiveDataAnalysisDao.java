package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.DeviceArchiveData;

public interface ArchiveDataAnalysisDao {
    
    /**
     * Sets up a new Analysis by populating the Analysis and Slots tables.
     * This step must be completed prior to inserting any slot data for a given Analysis.
     */
    public int createNewAnalysis(BuiltInAttribute attribute, 
                                 Duration intervalLength, 
                                 boolean excludeBadPointQualities, 
                                 Interval dateTimeRange);
    
    /**
     * Checks RawPointHistory for reads matching the given Analysis's slot dates and the given
     * pointId. If bad qualities are excluded, any points with a quality other than NORMAL will be
     * ignored. (This method does the actual Archive Data Analysis for a specific point).
     */
    public Map<Instant, Integer> getDeviceSlotValues(int analysisId, int pointId, boolean excludeBadPointQualities);
    
    /**
     * Inserts slot values for one device in a given Analysis.
     */
    public void insertSlotValues(int deviceId, int analysisId, DeviceArchiveData data);
    
    /**
     * Retrieve all slot values for all devices in a given analysis. Returned as a list of
     * DeviceAttributeIntervalData, each of which contains all slot values for a single device.
     * Use to collect results after analysis is complete.
     */
    public List<DeviceArchiveData> getSlotValues(int analysisId);
    
    /**
     * Retrieves the Analysis object for a given analysisId.
     */
    public Analysis getAnalysisById(int analysisId);
}
