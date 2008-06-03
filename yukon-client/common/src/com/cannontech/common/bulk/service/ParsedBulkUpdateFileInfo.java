package com.cannontech.common.bulk.service;

import com.cannontech.common.bulk.field.BulkFieldColumnHeader;

public class ParsedBulkUpdateFileInfo extends ParsedBulkFileInfo {
    
    private BulkFieldColumnHeader identifierBulkFieldColumnHeader;
    
    public ParsedBulkUpdateFileInfo(BulkFileInfo bulkFileInfo) {
        super(bulkFileInfo);
    }

    // IDENTIFIER COLUMN
    public BulkFieldColumnHeader getIdentifierBulkFieldColumnHeader() {
        return identifierBulkFieldColumnHeader;
    }
    public void setIdentifierBulkFieldColumnHeader(BulkFieldColumnHeader identifierBulkFieldColumnHeader) {
        this.identifierBulkFieldColumnHeader = identifierBulkFieldColumnHeader;
    }
}