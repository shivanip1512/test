package com.cannontech.common.bulk.service;

import java.io.IOException;



public interface BulkUpdateService {

    public String startBulkUpdate(ParsedBulkUpdateFileInfo bulkUpdateFileInfo) throws IOException;
    
    public ParsedBulkUpdateFileInfo createParsedBulkUpdateFileInfo(BulkUpdateFileInfo info);
}
