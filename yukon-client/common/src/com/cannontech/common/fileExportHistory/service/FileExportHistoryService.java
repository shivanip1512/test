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
     * Creates an ExportHistoryEntry for the Archive and Export file(s).
     * @param exportFile -- may be null when no export file was created
     * @param fileName -- the name of the file
     * @return entryId -- the id of the history entry created
     * @throws FileNotFoundException -- if the archive file cannot be accessed
     * @throws IOException -- if a problem occurs getting the path of the export file.
     */
	public ExportHistoryEntry addHistoryEntry(File archiveFile, File exportFile, String fileName,
                                       FileExportType type, String initiator) 
                                               throws FileNotFoundException, IOException;
	
}
