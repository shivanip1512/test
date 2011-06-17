package com.cannontech.web.bulk.service;

import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Interval;

import com.cannontech.common.bulk.model.DeviceArchiveData;
import com.cannontech.common.bulk.model.DevicePointValuesHolder;
import com.cannontech.web.bulk.model.ArchiveDataAnalysisBackingBean;

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
     * Returns a date/time range whose start and end points are exactly one interval earlier than
     * those of the supplied range.
     */
    public Interval getDateTimeRangeForDisplay(Interval dateRange, Duration intervalLength);
}
