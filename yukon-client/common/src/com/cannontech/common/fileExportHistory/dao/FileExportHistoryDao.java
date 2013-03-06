package com.cannontech.common.fileExportHistory.dao;

import java.util.List;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;

public interface FileExportHistoryDao {
	public int insertEntry(String originalFileName, String fileName, FileExportType type, String initiator, String exportPath);
	
	public List<ExportHistoryEntry> getAllEntries();
	
	/**
	 * Retrieve a filtered subset of all entries. Name fragment or initiator fragment or both may be
	 * supplied - unneeded parameters can be passed as null. Returns values whose originalFileName
	 * contains nameFragment (if not null) and whose initiator contains initiatorFragment (if not null).
	 */
	public List<ExportHistoryEntry> getFilteredEntries(String nameFragment, String initiatorFragment);
	
	public ExportHistoryEntry getEntry(int entryId);
	
	public boolean deleteEntry(int entryId);
}
