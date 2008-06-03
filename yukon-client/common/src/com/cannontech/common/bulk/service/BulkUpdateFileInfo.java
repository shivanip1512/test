package com.cannontech.common.bulk.service;

import org.springframework.core.io.FileSystemResource;

public class BulkUpdateFileInfo extends BulkFileInfo {
    
    private boolean ignoreInvalidIdentifiers;

    public BulkUpdateFileInfo(FileSystemResource fileResource, boolean ignoreInvalidCols, boolean ignoreInvalidIdentifiers) {
        
        super(fileResource, ignoreInvalidCols);
        this.ignoreInvalidIdentifiers = ignoreInvalidIdentifiers;
    }

    // ignoreInvalidIdentifiers
    public boolean isIgnoreInvalidIdentifiers () {
        return ignoreInvalidIdentifiers;
    }
}