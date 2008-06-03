package com.cannontech.common.bulk.service;

import org.springframework.core.io.FileSystemResource;

public class BulkImportFileInfo extends BulkFileInfo {

    public BulkImportFileInfo(FileSystemResource fileResource, boolean ignoreInvalidCols) {
        
        super(fileResource, ignoreInvalidCols);
    }
    
}