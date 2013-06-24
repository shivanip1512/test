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
     * @param archiveFile
     * @param exportFile
     * @param fileName
     * @param type
     * @param initiator
     * @return entryId
     * @throws FileNotFoundException
     * @throws IOException
     */
	public ExportHistoryEntry addHistoryEntry(File archiveFile, File exportFile, String fileName,
                                       FileExportType type, String initiator) 
                                               throws FileNotFoundException, IOException;
	
}
