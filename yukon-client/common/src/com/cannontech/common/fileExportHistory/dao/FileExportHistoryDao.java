package com.cannontech.common.fileExportHistory.dao;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

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
	
	/**
	 * Marks the DB entry as no longer having an archived file associated with it.
	 * This should be done in conjunction with actually deleting the file.
	 */
	public boolean markArchiveDeleted(Iterable<Integer> entryIds);
	public boolean markArchiveDeleted(int entryId);
	
	/**
	 * Retrieves all entries that have an archive file, mapped by initiator string.
	 */
	public Multimap<String, ExportHistoryEntry> getEntriesWithArchiveByInitiator();
	
	/**
	 * Retrieves all entries that have an archive file, mapped by creation date.
	 */
	public Multimap<Instant, ExportHistoryEntry> getEntriesWithArchiveByDate();
}
