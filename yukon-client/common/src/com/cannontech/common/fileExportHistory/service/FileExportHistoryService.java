package com.cannontech.common.fileExportHistory.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;

public interface FileExportHistoryService {
	
	/**
	 * @return a File object for the archived copy.
	 */
	public File getArchivedFile(int exportHistoryEntryId) throws FileNotFoundException;
	
	/**
	 * Attempts to delete the archived file for the specified entry. If the file is successfully deleted, the DB entry
	 * will be marked as ArchiveFileExists = false.
	 * @return True if the file was deleted, otherwise false.
	 */
	public boolean deleteArchivedFile(int exportHistoryEntryId);
	
    /** 
     * Creates an ExportHistoryEntry for the Archive and Export file(s).
     * @param exportFile -- may be null when no export file was created
     * @param fileName -- the name of the file
     * @param jobName - Name if the schedule while running
     * @param jobGroupId - Original job group Id of the job
     * @throws FileNotFoundException -- if the archive file cannot be accessed
     * @throws IOException -- if a problem occurs getting the path of the export file.
     */
    public ExportHistoryEntry addHistoryEntry(File archiveFile, File exportFile, String fileName, FileExportType type,
            String jobName, int jobGroupId) throws FileNotFoundException, IOException;
}
