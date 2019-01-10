package com.cannontech.common.bulk.service;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;

public abstract class BulkFileInfo {

    // INPUT INFO
    private FileSystemResource fileResource;
    private boolean ignoreInvalidCols;
    private int lineCount = 0;
    private String id = null;
    private String originalFilename;
    private String importType;
    
    // PUBLIC GETTERS
    //----------------------------------------------------------------------------------------------

    // ID
    public String getId() {
        return id;
    }

    // FILE RESOURCE
    public FileSystemResource getFileResource() {
        return fileResource;
    }
    
    public boolean isIgnoreInvalidCols() {
        return this.ignoreInvalidCols;
    }

    public BulkFileInfo(FileSystemResource fileResource, boolean ignoreInvalidCols) {
        
        this.fileResource = fileResource;
        this.ignoreInvalidCols = ignoreInvalidCols;
        this.id = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }

    public int getLineCount() {
        return lineCount;
    }
    
    /**
     * This is the number of total lines in file, minus the header row,
     * it is used for display of how many actual device items will be processed.
     * @return The number of lines in file, not including the header row.
     */
    public int getDataCount() {
        return getLineCount() - 1;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }
}
