package com.cannontech.simplereport;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jfree.report.JFreeReport;
import org.jfree.report.function.FunctionInitializeException;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.ReportModelMetaInfo;
import com.cannontech.user.YukonUserContext;
import com.opencsv.CSVWriter;

public class SimpleReportOutputterImpl implements SimpleReportOutputter {
    private SimpleReportService simpleReportService;
    
    public void outputCsvReport(YukonReportDefinition<? extends BareReportModel> reportDefinition,
                                BareReportModel reportModel,
                                OutputStream outputStream,
                                YukonUserContext userContext) throws IOException {
        // column names, formats
        //-----------------------------------------------------------------------------------------
        ColumnLayoutData[] bodyColumns = reportDefinition.getReportLayoutData().getBodyColumns();
        
        String[] columnNames = new String[bodyColumns.length];
        List<String> columnFormats = new ArrayList<String>();
        
        for(int colIdx = 0; colIdx < bodyColumns.length; colIdx++) {
            columnNames[colIdx] = bodyColumns[colIdx].getColumnName();
            columnFormats.add(bodyColumns[colIdx].getFormat());
        }
        
        // data grid
        //-----------------------------------------------------------------------------------------
        List<List<String>> data = simpleReportService.getFormattedData(reportDefinition, reportModel, userContext);
        
        // convert from List<List<String>> to List<String[]> that csvWriter likes to eat
        List<String[]> dataAsArray = new ArrayList<String[]>();
        for(List<String> strList : data) {
            String[] strArr = new String[strList.size()];
            for(int strArrIdx = 0; strArrIdx < strList.size(); strArrIdx++) {
                strArr[strArrIdx] = strList.get(strArrIdx);
            }
            dataAsArray.add(strArr);
        }
        
        // csv writer setup
        //-----------------------------------------------------------------------------------------
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
         
        
        // write to csv
        //-----------------------------------------------------------------------------------------
        csvWriter.writeNext(columnNames);
        csvWriter.writeAll(dataAsArray);
        csvWriter.close();
    }
    
    @Override
    public void outputPdfReport(YukonReportDefinition<? extends BareReportModel> reportDefinition,
                                BareReportModel reportModel,
                                OutputStream outputStream,
                                YukonUserContext userContext) throws IOException {
        
        // get report definition, model
        //-----------------------------------------------------------------------------------------
        BareReportModel stringReportModel = simpleReportService.getStringReportModel(reportDefinition, reportModel, userContext);
                
        SimpleYukonReport report = new SimpleYukonReport(reportDefinition, stringReportModel);
        
        if(reportModel instanceof ReportModelMetaInfo) {
            ReportModelMetaInfo metaInfoReport = (ReportModelMetaInfo) reportModel;
            LinkedHashMap<String, String> metaInfo = metaInfoReport.getMetaInfo(userContext);
            report.setMetaInfo(metaInfo);
        }
        
        try {
            JFreeReport jfreeReport = report.createReport();
            jfreeReport.setData(report.getModel());
            ReportFuncs.outputYukonReport( jfreeReport, "pdf", outputStream);
        } catch (FunctionInitializeException e) {
            throw new RuntimeException("Unable to output report", e);
        }
    }

    public void setSimpleReportService(SimpleReportService simpleReportService) {
        this.simpleReportService = simpleReportService;
    }

}
