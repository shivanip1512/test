package com.cannontech.common.bulk.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.common.bulk.model.Analysis;
import com.cannontech.common.bulk.model.ArchiveAnalysisProfileReadResult;
import com.cannontech.common.bulk.model.ArchiveDataAnalysisBackingBean;

public interface ArchiveDataAnalysisService {
    
    /**
     * Sets up a new Analysis.
     * @return The ID of the newly created analysis.
     */
    public int createAnalysis(ArchiveDataAnalysisBackingBean archiveDataAnalysisBackingBean);

    /**
     * Creates a new Analysis with the same parameters as an existing Analysis.
     */
    public int createAnalysis(int oldAnalysisId);
    
    /**
     * Runs an analysis as a background task.
     */
    public String startAnalysis(ArchiveDataAnalysisBackingBean archiveDataAnalysisBackingBean, int analysisId);
    
    /**
     * Runs an analysis as a background task, using the same parameters as an existing Analysis.
     */
    public String startAnalysis(int oldAnalysisId, int newAnalysisId);
    
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
     * Returns a list of all interval end times for the specified analysis.
     */
    public List<Instant> getIntervalEndTimes(Analysis analysis);
}
