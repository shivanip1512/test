package com.cannontech.common.bulk.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Interval;

import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.DevicePointValuesHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.common.bulk.model.ArchiveAnalysisProfileReadResult;
import com.cannontech.common.bulk.model.ArchiveDataAnalysisBackingBean;

public interface ArchiveDataAnalysisService {
    
    /**
     * Sets up a new Analysis.
     * @return The ID of the newly created analysis.
     */
    public int createAnalysis(ArchiveDataAnalysisBackingBean archiveDataAnalysisBackingBean);
    
    /**
     * Runs an analysis as a background task.
     */
    public String startAnalysis(ArchiveDataAnalysisBackingBean archiveDataAnalysisBackingBean, int analysisId);
    
    /**
     * Retrieves device point values for given analysis. Point values will be null whenever there 
     * was no value present for that specific device and interval.
     */
    public List<DevicePointValuesHolder> getDevicePointValuesList(List<DeviceArchiveData> dataList);
    
    /**
     * Builds load profile command strings for all missing intervals on all devices in a given
     * analysis, then sends those commands through command request executor.
     * Returns the id used to obtain results information from RecentResultsCache
     */
    public String runProfileReads(int analysisId, LiteYukonUser user);
    
    /**
     * Gets the results object for an ADA load profile read from its id.
     */
    public ArchiveAnalysisProfileReadResult getProfileReadResultById(String resultId);
    
    /**
     * Returns a date/time range whose start and end points are exactly one interval earlier than
     * those of the supplied range.
     */
    public Interval getDateTimeRangeForDisplay(Interval dateRange, Duration intervalLength);
}
