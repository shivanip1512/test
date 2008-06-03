package com.cannontech.common.bulk.service;


public class ParsedBulkImportFileInfo extends ParsedBulkFileInfo {
    
    private BulkImportMethod bulkImportMethod = null;
    
    public ParsedBulkImportFileInfo(BulkFileInfo bulkFileInfo) {
        super(bulkFileInfo);
    }
    
    // IMPORT METHOD
    public BulkImportMethod getBulkImportMethod() {
        return bulkImportMethod;
    }
    public void setBulkImportMethod(BulkImportMethod bulkImportMethod) {
        this.bulkImportMethod = bulkImportMethod;
    }
}