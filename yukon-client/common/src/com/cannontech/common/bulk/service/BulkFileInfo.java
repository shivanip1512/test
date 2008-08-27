package com.cannontech.common.bulk.service;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.FileSystemResource;

public abstract class BulkFileInfo {

    // INPUT INFO
    private FileSystemResource fileResource;
    private boolean ignoreInvalidCols;
    private int lineCount = 0;
    private String id = null;
    
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
    
    // this is the number of total lines in file, minus the header row
    // it is used for display of how many actual device items will be processed
    public int getDataCount() {
        return getLineCount() - 1;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }
}
