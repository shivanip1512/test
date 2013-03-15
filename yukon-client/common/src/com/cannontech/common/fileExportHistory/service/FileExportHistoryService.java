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
	 * Copies an existing file into the export archive and adds a file export history entry.
	 * @throws FileNotFoundException if the original file cannot be accessed
	 * @throws IOException if a problem occurs copying the original file to the archive.
	 */
	public ExportHistoryEntry copyFile(File originalFile, FileExportType type, String initiator) throws FileNotFoundException, IOException;
	
}
