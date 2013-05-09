package com.cannontech.web.scheduledFileExport.tasks;

/**
 * Interface for scheduled file export tasks that have a persisted format.
 */
public interface PersistedFormatTask {
    
    public int getFormatId();
    
}
