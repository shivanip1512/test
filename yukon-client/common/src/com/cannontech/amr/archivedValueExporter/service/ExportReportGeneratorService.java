package com.cannontech.amr.archivedValueExporter.service;

import java.util.Date;
import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.user.YukonUserContext;

public interface ExportReportGeneratorService {

    /**
     * Generates report.
     * If stop date is null the preview  will be generated. 
     *
     * @param meters
     * @param format
     * @param stopDate 
     * @param userContext
     * @return list
     */
    public List<String> generateReport(List<Meter> meters, ExportFormat format, Date stopDate, YukonUserContext userContext);
    
    /**
     * Generates preview.
     *
     * @param meter
     * @param previewUOMValue
     * @param format
     * @param userContext
     * @return list
     */
    public List<String> generatePreview(Meter meter, String previewUOMValue, ExportFormat format, YukonUserContext userContext);
}
