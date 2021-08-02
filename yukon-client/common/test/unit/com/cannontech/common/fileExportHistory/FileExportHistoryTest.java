package com.cannontech.common.fileExportHistory;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.joda.time.Instant;
import org.junit.jupiter.api.Test;

public class FileExportHistoryTest {
    
    @Test
    public void exportHistoryEntryTest() {
        ExportHistoryEntry entry =
            new ExportHistoryEntry(0, "filename.csv", "filename.csv", FileExportType.BILLING, Instant.now(),
                "C:\\exportpath", true, 4543, "jobName");
        String fileMimeTypeCsv = entry.getFileMimeType();
        assertEquals("text/csv", fileMimeTypeCsv);
        
        ExportHistoryEntry entry2 =
            new ExportHistoryEntry(0, "filename.xml", "filename.xml", FileExportType.BILLING, Instant.now(),
                "C:\\exportpath", true, 343, "jobName");
        String fileMimeTypeXml = entry2.getFileMimeType();
        assertEquals("application/xml", fileMimeTypeXml);
    }

}
