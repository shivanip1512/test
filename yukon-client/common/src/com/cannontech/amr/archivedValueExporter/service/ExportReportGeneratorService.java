package com.cannontech.amr.archivedValueExporter.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.Preview;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.user.YukonUserContext;

public interface ExportReportGeneratorService {

    /**
     * Streams report to writer as its generated. New lines are defined by the system property
     * line.separator, and not necessarily a single newline ('\n') character.
     */
    void generateReport(List<? extends YukonPao> meters, ExportFormat format, DataRange dataRange,
        YukonUserContext userContext, Attribute[] attributes, BufferedWriter writer, boolean isOnInterval,
        TimeIntervals interval) throws IOException;

    Preview generatePreview(ExportFormat format, YukonUserContext userContext);
}