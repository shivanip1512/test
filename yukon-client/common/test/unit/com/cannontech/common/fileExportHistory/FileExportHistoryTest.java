package com.cannontech.common.fileExportHistory;

import junit.framework.Assert;

import org.joda.time.Instant;
import org.junit.Test;

public class FileExportHistoryTest {
    
    @Test
    public void exportHistoryEntryTest() {
        ExportHistoryEntry entry = new ExportHistoryEntry(0, "filename.csv", "filename.csv", FileExportType.BILLING, "initiator", Instant.now(), "C:\\exportpath", true);
        String fileMimeTypeCsv = entry.getFileMimeType();
        Assert.assertEquals("text/csv", fileMimeTypeCsv);
        
        ExportHistoryEntry entry2 = new ExportHistoryEntry(0, "filename.xml", "filename.xml", FileExportType.BILLING, "initiator", Instant.now(), "C:\\exportpath", true);
        String fileMimeTypeXml = entry2.getFileMimeType();
        Assert.assertEquals("application/xml", fileMimeTypeXml);
    }

}
