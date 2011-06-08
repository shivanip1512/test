package com.cannontech.web.bulk.service;

import com.cannontech.web.bulk.model.ArchiveDataAnalysisBackingBean;

public interface ArchiveDataAnalysisService {
    
    public int createAnalysis(ArchiveDataAnalysisBackingBean archiveDataAnalysisBackingBean);
    
    public String startAnalysis(ArchiveDataAnalysisBackingBean archiveDataAnalysisBackingBean, int analysisId);
}
