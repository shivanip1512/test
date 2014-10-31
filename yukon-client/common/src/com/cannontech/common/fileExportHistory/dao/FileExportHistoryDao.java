package com.cannontech.common.fileExportHistory.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.google.common.collect.Multimap;

public interface FileExportHistoryDao {
    public int insertEntry(String originalFileName, String fileName, FileExportType type, String jobName,
            String exportPath, Integer jobGroupId);

    public List<ExportHistoryEntry> getAllEntries();

    /**
     * Retrieve a filtered subset of all entries. Name fragment or Schedule fragment or both may be
     * supplied - unneeded parameters can be passed as null. Returns values whose originalFileName
     * contains nameFragment (if not null) and whose Schedule contains Schedule (if not null).
     */
    public List<ExportHistoryEntry> getFilteredEntries(String nameFragment, String jobName, FileExportType jobType,
            Integer jobGroupId);

    public ExportHistoryEntry getEntry(int entryId);

    /**
     * Marks the DB entry as no longer having an archived file associated with it.
     * This should be done in conjunction with actually deleting the file.
     */
    public boolean markArchiveDeleted(int entryId);

    /**
     * Retrieves all entries that have an archive file, mapped by Schedule string.
     */
    public Multimap<String, ExportHistoryEntry> getEntriesWithArchiveBySchedule();

    /**
     * Retrieves all entries that have an archive file, mapped by creation date.
     */
    public Multimap<Instant, ExportHistoryEntry> getEntriesWithArchiveByDate();
}
