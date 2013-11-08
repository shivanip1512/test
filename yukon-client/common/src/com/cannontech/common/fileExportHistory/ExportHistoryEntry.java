package com.cannontech.common.fileExportHistory;

import java.util.Comparator;

import javax.activation.MimetypesFileTypeMap;

import org.joda.time.Instant;

/**
 * Represents information related to a single occurrence of a file export.
 */
public final class ExportHistoryEntry implements Comparable<ExportHistoryEntry>{
	private static final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
	
	static {
	    fileTypeMap.addMimeTypes("text/csv csv CSV");
	    fileTypeMap.addMimeTypes("application/xml xml XML");
	}
	
	private final int entryId;
	private final String fileName;
	private final String originalFileName;
	private final FileExportType type;
	private final String initiator;
	private final Instant date;
	private final String exportPath;
	private final boolean archiveFileExists;
	
	public ExportHistoryEntry(int entryId, String originalFileName, String fileName, FileExportType type, 
	                          String initiator, Instant date, String exportPath, boolean archiveFileExists) {
		this.entryId = entryId;
		this.originalFileName = originalFileName;
		this.fileName = fileName;
		this.type = type;
		this.initiator = initiator;
		this.date = date;
		this.exportPath = exportPath;
		this.archiveFileExists = archiveFileExists;
	}
	
	public int getId() {
		return entryId;
	}
	
	/**
	 * This is the name of the file that was originally generated.
	 */
	public String getOriginalFileName() {
		return originalFileName;
	}
	
	/**
	 * This is the name of the copy of the file, stored in the export archive.
	 * It differs from the original file name - a unique id is tacked on to ensure
	 * a unique name.
	 */
	public String getFileName() {
		return fileName;
	}
	
	public FileExportType getType() {
		return type;
	}

	/**
	 * The initiator is the origin of the original file export.
	 */
	public String getInitiator() {
		return initiator;
	}

	public Instant getDate() {
		return date;
	}
	
	/**
	 * The location where the original file was written.
	 */
	public String getExportPath() {
		return exportPath;
	}
	
	public String getFileMimeType() {
		return fileTypeMap.getContentType(originalFileName);
	}
	
	public boolean isArchiveFileExists() {
	    return archiveFileExists;
	}
	
	@Override
	public int compareTo(ExportHistoryEntry other) {
		int comparison = date.compareTo(other.date);
		if(comparison != 0) return comparison * -1; //sort newest to oldest
		
		comparison = initiator.compareTo(other.initiator);
		if(comparison != 0) return comparison;
		
		comparison = originalFileName.compareTo(other.originalFileName);
		if(comparison != 0) return comparison;
		
		return originalFileName.compareTo(other.originalFileName);
	}
	
	@Override
	public boolean equals(Object other) {
	    if(other instanceof ExportHistoryEntry) {
	        return compareTo((ExportHistoryEntry)other) == 0;
	    }
	    return false;
	}
	
	public static final Comparator<ExportHistoryEntry> OLDEST_TO_NEWEST_COMPARATOR = new Comparator<ExportHistoryEntry>() {
	    @Override
        public int compare(ExportHistoryEntry entry1, ExportHistoryEntry entry2) {
	        return entry1.date.compareTo(entry2.date); //oldest to newest
	    }
	};
}
