package com.cannontech.simplereport;

import java.io.IOException;
import java.io.OutputStream;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.user.YukonUserContext;

public interface SimpleReportOutputter {
    
    public void outputCsvReport(YukonReportDefinition<? extends BareReportModel> reportDefinition, BareReportModel reportModel, OutputStream outputStream, YukonUserContext userContext) throws IOException;
    public void outputPdfReport(YukonReportDefinition<? extends BareReportModel> reportDefinition, BareReportModel reportModel, OutputStream outputStream, YukonUserContext userContext) throws IOException;

}
