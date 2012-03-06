package com.cannontech.common.bulk.service;

import java.io.IOException;



public interface BulkImportService {

    public ParsedBulkImportFileInfo createParsedBulkImportFileInfo(BulkImportFileInfo info,  BulkImportType bulkImportType);
            
    public String startBulkImport(ParsedBulkImportFileInfo bulkImportFileInfo) throws IOException;
}
