package com.cannontech.web.reports;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.report.JFreeReport;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.ReportModelMetaInfo;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.ColumnInfo;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.simplereport.SimpleReportViewJsp;
import com.cannontech.simplereport.SimpleYukonReport;
import com.cannontech.simplereport.YukonReportDefinition;
import com.cannontech.tools.csv.CSVWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.InputUtil;

public class ReportsController extends MultiActionController  {
    
    private SimpleReportService simpleReportService = null;
    
    /**
     * htmlView - export report data as an HTML table
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView htmlView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        String viewJsp = ServletRequestUtils.getRequiredStringParameter(request, "viewJsp");
        String jspPath = SimpleReportViewJsp.valueOf(viewJsp).getJspPath();
        ModelAndView mav = new ModelAndView(jspPath);
        
        String definitionName = ServletRequestUtils.getRequiredStringParameter(request, "def");
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // optional page module, showMenu, menuSelection values
        //-----------------------------------------------------------------------------------------
        String module = ServletRequestUtils.getStringParameter(request, "module", "blank");
        Boolean showMenu = ServletRequestUtils.getBooleanParameter(request, "showMenu", false);
        String menuSelection = ServletRequestUtils.getStringParameter(request, "menuSelection", "");
        
        mav.addObject("module", module);
        mav.addObject("showMenu", showMenu);
        mav.addObject("menuSelection", menuSelection);
        
        
        // get report definition, model
        //-----------------------------------------------------------------------------------------
        Map<String, String> parameterMap = getParameterMap(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap);
        
        
        // title
        //-----------------------------------------------------------------------------------------
        mav.addObject("reportTitle", reportModel.getTitle());
        
        
        // column layout lists
        //-----------------------------------------------------------------------------------------
        ColumnLayoutData[] bodyColumns = reportDefinition.getReportLayoutData().getBodyColumns();
        List<ColumnInfo> columnInfo = simpleReportService.buildColumnInfoListFromColumnLayoutData(bodyColumns);
        
        mav.addObject("columnInfo", columnInfo);
        
        
        // data grid
        //-----------------------------------------------------------------------------------------
        int columnCount = reportModel.getColumnCount();
        int rowCount = reportModel.getRowCount();
        List<List<String>> data = simpleReportService.getFormattedData(reportModel, columnInfo, userContext);
        
        mav.addObject("columnCount", columnCount);
        mav.addObject("rowCount", rowCount);
        mav.addObject("data", data);
        
        
        // include definition name and model in order to create links to other styles of the same report
        //-----------------------------------------------------------------------------------------
        mav.addObject("reportModel",reportModel);
        mav.addObject("definitionName", definitionName);


        // include random info map
        //-----------------------------------------------------------------------------------------
        
        if (reportModel instanceof ReportModelMetaInfo) {
            ReportModelMetaInfo metaInfo = (ReportModelMetaInfo) reportModel;
            mav.addObject("metaInfo", metaInfo.getMetaInfo(userContext));
        }
        
        // include input values map
        //-----------------------------------------------------------------------------------------
        Map<String, String> inputMap = InputUtil.extractProperties(reportDefinition.getInputs(), reportModel);
        mav.addObject("inputMap", inputMap);
        
        
        
        return mav;
    }
    
    
    /**
     * csvView - export report data as CSV file
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView csvView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // get report definition, model
        //-----------------------------------------------------------------------------------------
        // Map<String, String> parameterMap = request.getParameterMap();
        Map<String, String> parameterMap = getParameterMap(request);
        
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap);
        
        
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
        List<ColumnInfo> columnInfo = simpleReportService.buildColumnInfoListFromColumnLayoutData(bodyColumns);
        List<List<String>> data = simpleReportService.getFormattedData(reportModel, columnInfo, userContext);
        
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
        OutputStream outputStream = response.getOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        CSVWriter csvWriter = new CSVWriter(writer);
         
        
        // write to csv
        //-----------------------------------------------------------------------------------------
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition","filename=\"" + ServletUtil.makeWindowsSafeFileName(reportModel.getTitle()) + ".csv\"");

        csvWriter.writeNext(columnNames);
        csvWriter.writeAll(dataAsArray);
        csvWriter.close();

        return null;
    }
    
    
    @SuppressWarnings("unchecked")
    private Map<String, String> getParameterMap(HttpServletRequest request) {
        
        Map<String, String[]> parameterMapWithArrays = request.getParameterMap();
        
        Map<String, String> parameterMap = new HashMap<String, String>();
        
        for(String pKey : parameterMapWithArrays.keySet()) {
            String[] vals = parameterMapWithArrays.get(pKey);
            for(int i = 0; i < vals.length; i++) {
                parameterMap.put(pKey, vals[i]);
            }
        }
        
        return parameterMap;
    }
    
    
    /**
     * pdfView - export report data as a PDF file
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView pdfView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
                
        // get report definition, model
        //-----------------------------------------------------------------------------------------
        Map<String, String> parameterMap = getParameterMap(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap);
        BareReportModel stringReportModel = simpleReportService.getStringReportModel(reportDefinition, reportModel, parameterMap, userContext);
                
        SimpleYukonReport report = new SimpleYukonReport(reportDefinition, stringReportModel);
        
        if(reportModel instanceof ReportModelMetaInfo) {
            ReportModelMetaInfo metaInfoReport = (ReportModelMetaInfo) reportModel;
            LinkedHashMap<String, String> metaInfo = metaInfoReport.getMetaInfo(userContext);
            report.setMetaInfo(metaInfo);
        }
        
        JFreeReport jfreeReport = report.createReport();
        jfreeReport.setData(report.getModel());
        OutputStream outputStream = response.getOutputStream();
        ReportFuncs.outputYukonReport( jfreeReport, "pdf", outputStream);
        
        return null;
    }
    
   



    @Required
    public void setSimpleReportService(SimpleReportService simpleReportService) {
        this.simpleReportService = simpleReportService;
    }

}
