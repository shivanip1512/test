package com.cannontech.common.fileExportHistory.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;

public interface FileExportHistoryService {
	
	public List<ExportHistoryEntry> getAllEntries();
	
	public ExportHistoryEntry getEntry(int entryId);
	
	/**
	 * Retrieve a filtered subset of all entries. Name fragment or initiator fragment or both may be
	 * supplied - unneeded parameters can be passed as null. Returns values whose originalFileName
	 * contains nameFragment (if not null) and whose initiator contains initiatorFragment (if not null).
	 */
	public List<ExportHistoryEntry> getFilteredEntries(String nameFragment, String initiatorFragment);
	
	/**
	 * @return a File object for the archived copy.
	 */
	public File getArchivedFile(int exportHistoryEntryId) throws FileNotFoundException;
	
	/**
	 * Copies an existing file into the export archive and adds a file export history entry.s
	 */
	public ExportHistoryEntry copyFile(File originalFile, FileExportType type, String initiator) throws FileNotFoundException, IOException;
	
}
