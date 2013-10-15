package com.cannontech.amr.archivedValueExporter.service;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.user.YukonUserContext;

public interface ExportReportGeneratorService {

    /**
     * Generates report.
     * If stop date is null the preview  will be generated. 
     */
    public List<String> generateReport(List<? extends YukonPao> meters, ExportFormat format, DataRange dataRange,
        YukonUserContext userContext, Attribute[] attributes);
    
    /**
     * Generates preview.
     */
    public List<String> generatePreview(ExportFormat format, YukonUserContext userContext);
}