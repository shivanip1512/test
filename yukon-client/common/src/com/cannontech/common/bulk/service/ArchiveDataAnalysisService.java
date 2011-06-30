package com.cannontech.common.bulk.service;

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
     * Builds load profile command strings for all missing intervals on all devices in a given
     * analysis, then sends those commands through command request executor.
     * Returns the id used to obtain results information from RecentResultsCache
     */
    public String runProfileReads(int analysisId, LiteYukonUser user);
    
    /**
     * Gets the results object for an ADA load profile read from its id.
     */
    public ArchiveAnalysisProfileReadResult getProfileReadResultById(String resultId);
}
