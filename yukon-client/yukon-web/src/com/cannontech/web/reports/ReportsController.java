package com.cannontech.web.reports;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.analysis.report.ColumnLayoutData;
import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.analysis.tablemodel.ReportModelMetaInfo;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.simplereport.ColumnInfo;
import com.cannontech.simplereport.SimpleReportOutputter;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.simplereport.SimpleReportViewJsp;
import com.cannontech.simplereport.YukonReportDefinition;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.input.InputUtil;

public class ReportsController extends MultiActionController  {
    
    private SimpleReportService simpleReportService = null;
    private SimpleReportOutputter simpleReportOutputter = null;
    
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
        boolean showLoadMask = ServletRequestUtils.getBooleanParameter(request, "showLoadMask", true);
        int refreshRate = ServletRequestUtils.getIntParameter(request, "refreshRate", 0);
        
        mav.addObject("module", module);
        mav.addObject("showMenu", showMenu);
        mav.addObject("menuSelection", menuSelection);
        mav.addObject("showLoadMask", showLoadMask);
        mav.addObject("refreshRate", refreshRate);
        
        
        // get report definition, model
        //-----------------------------------------------------------------------------------------
        Map<String, String> parameterMap = ServletUtil.getParameterMap(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, false);
        
        // title
        //-----------------------------------------------------------------------------------------
        mav.addObject("reportTitle", reportModel.getTitle());
        
        
        // column layout lists
        //-----------------------------------------------------------------------------------------
        ColumnLayoutData[] bodyColumns = reportDefinition.getReportLayoutData().getBodyColumns();
        List<ColumnInfo> columnInfo = simpleReportService.buildColumnInfoListFromColumnLayoutData(bodyColumns);
        
        mav.addObject("columnInfo", columnInfo);
        
        
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
        
        inputMap.put("def", definitionName);
        String queryString = ServletUtil.buildSafeQueryStringFromMap(inputMap, true);
        String dataUrl = "/spring/reports/simple/jsonData?" + queryString;
        dataUrl = ServletUtil.createSafeUrl(request, dataUrl);
        mav.addObject("dataUrl", dataUrl);
        
        return mav;
    }

    
    public ModelAndView jsonData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // get report definition, model
        //-----------------------------------------------------------------------------------------
        Map<String, String> parameterMap = ServletUtil.getParameterMap(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, true);
        
        // column layout lists
        //-----------------------------------------------------------------------------------------
        ColumnLayoutData[] bodyColumns = reportDefinition.getReportLayoutData().getBodyColumns();
        List<ColumnInfo> columnInfo = simpleReportService.buildColumnInfoListFromColumnLayoutData(bodyColumns);
        
        // data grid
        //-----------------------------------------------------------------------------------------
        List<List<String>> data = simpleReportService.getFormattedData(reportDefinition, reportModel, userContext);
        
        JsonReportDataUtils.outputReportData(data, columnInfo, response.getOutputStream());
        
        return null;
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
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, true);
        
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition","filename=\"" + ServletUtil.makeWindowsSafeFileName(reportModel.getTitle()) + ".csv\"");
        OutputStream outputStream = response.getOutputStream();
        simpleReportOutputter.outputCsvReport(reportDefinition, reportModel, outputStream, userContext);
        
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
                
        Map<String, String> parameterMap = getParameterMap(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, true);
        
        OutputStream outputStream = response.getOutputStream();
        
        simpleReportOutputter.outputPdfReport(reportDefinition, reportModel, outputStream, userContext);
        
        return null;
    }
    
    @Autowired
    public void setSimpleReportOutputter(SimpleReportOutputter simpleReportOutputter) {
        this.simpleReportOutputter = simpleReportOutputter;
    }

    @Required
    public void setSimpleReportService(SimpleReportService simpleReportService) {
        this.simpleReportService = simpleReportService;
    }

}
