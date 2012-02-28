package com.cannontech.common.bulk.service;

import java.io.IOException;



public interface BulkImportService {

    public ParsedBulkImportFileInfo createParsedBulkImportFileInfo(BulkImportFileInfo info, String deviceType);
            
    public String startBulkImport(ParsedBulkImportFileInfo bulkImportFileInfo) throws IOException;
}
