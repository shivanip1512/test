package com.cannontech.web.stars.scheduledDataImport.dao;

import java.util.List;

import com.cannontech.common.scheduledFileImport.ScheduleImportHistoryEntry;

public interface ScheduledDataImportDao {

    /**
     * Retrieve the file import history of a job with specified jobGroupId.
     */
    public List<ScheduleImportHistoryEntry> getImportHistory(int jobGroupId);

}
