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
	private final String originalFileName;
	private final String fileName;
	private final FileExportType type;
	private final Instant date;
	private final String exportPath;
	private final boolean archiveFileExists;
    private final int jobGroupId;
    private final String jobName;
	
    public ExportHistoryEntry(int entryId, String originalFileName, String fileName, FileExportType type,
            Instant date, String exportPath, boolean archiveFileExists, int jobGroupId, String jobName) {
        this.entryId = entryId;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.type = type;
        this.date = date;
        this.exportPath = exportPath;
        this.archiveFileExists = archiveFileExists;
        this.jobGroupId = jobGroupId;
        this.jobName = jobName;
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
	
    /**
     * Groups the Data Export File History
     */
    public int getJobGroupId() {
        return jobGroupId;
    }
    
    /**
     * The jobName is the origin of the original file export.
     */
    public String getJobName() {
        return jobName;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((exportPath == null) ? 0 : exportPath.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + jobGroupId;
        result = prime * result + ((jobName == null) ? 0 : jobName.hashCode());
        result = prime * result + ((originalFileName == null) ? 0 : originalFileName.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    
    @Override
    public int compareTo(ExportHistoryEntry other) {
        if(this == other) return 0;
        
        //date
        int comparison = date.compareTo(other.date);
        if(comparison != 0) return comparison * -1; //sort newest to oldest
        
        //type
        comparison = type.compareTo(other.type);
        if(comparison != 0) return comparison;
        
        //originalFileName
        comparison = originalFileName.compareTo(other.originalFileName);
        if(comparison != 0) return comparison;
        
        //fileName
        comparison = fileName.compareTo(other.fileName);
        if(comparison != 0) return comparison;
        
        //jobName
        comparison = jobName.compareTo(other.jobName);
        if(comparison != 0) return comparison;
        
        //exportPath
        if(other.exportPath == null) {
            return -1;
        } else if(exportPath == null) {
            return 1;
        }
        comparison = exportPath.compareTo(other.exportPath);
        if(comparison != 0) return comparison;
        
        comparison = jobGroupId - other.jobGroupId ;
        return comparison; 
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExportHistoryEntry other = (ExportHistoryEntry) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (exportPath == null) {
            if (other.exportPath != null)
                return false;
        } else if (!exportPath.equals(other.exportPath))
            return false;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (jobGroupId != other.jobGroupId)
            return false;
        if (jobName == null) {
            if (other.jobName != null)
                return false;
        } else if (!jobName.equals(other.jobName))
            return false;
        if (originalFileName == null) {
            if (other.originalFileName != null)
                return false;
        } else if (!originalFileName.equals(other.originalFileName))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
    
    public static final Comparator<ExportHistoryEntry> OLDEST_TO_NEWEST_COMPARATOR =
        new Comparator<ExportHistoryEntry>() {
     @Override
        public int compare(ExportHistoryEntry entry1, ExportHistoryEntry entry2) {
            return entry1.date.compareTo(entry2.date); //oldest to newest
        }
    };
}
