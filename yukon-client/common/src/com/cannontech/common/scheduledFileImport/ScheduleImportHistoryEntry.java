package com.cannontech.common.scheduledFileImport;

import org.joda.time.Instant;

public class ScheduleImportHistoryEntry {

    private int entryId;
    private String fileName;
    private Instant importDate;
    private String archiveFileName;
    private String archiveFilePath;
    private boolean archiveFileExists;
    private String failedFileName;
    private String failedFilePath;
    private int successCount;
    private int failureCount;

    public ScheduleImportHistoryEntry(int entryId, String fileName, Instant importDate, String archiveFileName,
            String archiveFilePath, boolean archiveFileExists, String failedFileName, String failedFilePath,
            int successCount, int failureCount) {
        this.entryId = entryId;
        this.fileName = fileName;
        this.importDate = importDate;
        this.archiveFileName = archiveFileName;
        this.archiveFilePath = archiveFilePath;
        this.archiveFileExists = archiveFileExists;
        this.failedFileName = failedFileName;
        this.failedFilePath = failedFilePath;
        this.successCount = successCount;
        this.failureCount = failureCount;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Instant getImportDate() {
        return importDate;
    }

    public void setImportDate(Instant importDate) {
        this.importDate = importDate;
    }

    public String getArchiveFileName() {
        return archiveFileName;
    }

    public void setArchiveFileName(String archiveFileName) {
        this.archiveFileName = archiveFileName;
    }

    public String getArchiveFilePath() {
        return archiveFilePath;
    }

    public void setArchiveFilePath(String archiveFilePath) {
        this.archiveFilePath = archiveFilePath;
    }

    public boolean isArchiveFileExists() {
        return archiveFileExists;
    }

    public void setArchiveFileExists(boolean archiveFileExists) {
        this.archiveFileExists = archiveFileExists;
    }

    public String getFailedFileName() {
        return failedFileName;
    }

    public void setFailedFileName(String failedFileName) {
        this.failedFileName = failedFileName;
    }

    public String getFailedFilePath() {
        return failedFilePath;
    }

    public void setFailedFilePath(String failedFilePath) {
        this.failedFilePath = failedFilePath;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getTotalCount() {
        return failureCount + successCount;
    }
}
