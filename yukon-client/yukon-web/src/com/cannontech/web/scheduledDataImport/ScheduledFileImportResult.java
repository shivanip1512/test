package com.cannontech.web.scheduledDataImport;

import org.joda.time.Instant;

import com.cannontech.common.scheduledFileImport.ScheduledImportType;

final public class ScheduledFileImportResult {

    private String fileName;
    private ScheduledImportType scheduledImportType;
    private Instant importDate;
    private String archiveFileName;
    private boolean archiveFileExists;
    private String failedFileName;
    private String failedFilePath;
    private int successCount;
    private int failureCount;

    public ScheduledFileImportResult(String fileName, ScheduledImportType scheduledImportType, Instant importDate,
            String archiveFileName, boolean archiveFileExists, String failedFileName, String failedFilePath,
            int successCount, int failureCount) {
        this.fileName = fileName;
        this.setScheduledImportType(scheduledImportType);
        this.importDate = importDate;
        this.archiveFileName = archiveFileName;
        this.archiveFileExists = archiveFileExists;
        this.failedFileName = failedFileName;
        this.failedFilePath = failedFilePath;
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public String getFileName() {
        return fileName;
    }


    public Instant getImportDate() {
        return importDate;
    }

    public String getArchiveFileName() {
        return archiveFileName;
    }

    public boolean isArchiveFileExists() {
        return archiveFileExists;
    }

    public String getFailedFileName() {
        return failedFileName;
    }

    public String getFailedFilePath() {
        return failedFilePath;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }
   

    public ScheduledImportType getScheduledImportType() {
        return scheduledImportType;
    }

    public void setScheduledImportType(ScheduledImportType scheduledImportType) {
        this.scheduledImportType = scheduledImportType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (archiveFileExists ? 1231 : 1237);
        result = prime * result + ((archiveFileName == null) ? 0 : archiveFileName.hashCode());
        result = prime * result + ((failedFileName == null) ? 0 : failedFileName.hashCode());
        result = prime * result + ((failedFilePath == null) ? 0 : failedFilePath.hashCode());
        result = prime * result + failureCount;
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((importDate == null) ? 0 : importDate.hashCode());
        result = prime * result + ((scheduledImportType == null) ? 0 : scheduledImportType.hashCode());
        result = prime * result + successCount;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScheduledFileImportResult other = (ScheduledFileImportResult) obj;
        if (archiveFileExists != other.archiveFileExists)
            return false;
        if (archiveFileName == null) {
            if (other.archiveFileName != null)
                return false;
        } else if (!archiveFileName.equals(other.archiveFileName))
            return false;
        if (failedFileName == null) {
            if (other.failedFileName != null)
                return false;
        } else if (!failedFileName.equals(other.failedFileName))
            return false;
        if (failedFilePath == null) {
            if (other.failedFilePath != null)
                return false;
        } else if (!failedFilePath.equals(other.failedFilePath))
            return false;
        if (failureCount != other.failureCount)
            return false;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (importDate == null) {
            if (other.importDate != null)
                return false;
        } else if (!importDate.equals(other.importDate))
            return false;
        if (scheduledImportType != other.scheduledImportType)
            return false;
        if (successCount != other.successCount)
            return false;
        return true;
    }

}
