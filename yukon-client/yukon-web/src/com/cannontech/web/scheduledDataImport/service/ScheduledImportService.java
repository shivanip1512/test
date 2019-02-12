package com.cannontech.web.scheduledDataImport.service;

import com.cannontech.web.scheduledDataImport.ScheduledDataImportResult;

public interface ScheduledImportService {
    /**
     * Initiate import process based on import type.
     * Create ScheduledDataImportResult that contain success file , error file and also file import result.
     */
    ScheduledDataImportResult initiateImport(String scheduleName, String importPath, String errorFileOutputPath);

}
