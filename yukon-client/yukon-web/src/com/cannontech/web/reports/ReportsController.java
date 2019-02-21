package com.cannontech.web.reports;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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

@Controller
@RequestMapping("/simple/*")
public class ReportsController {
    
    @Autowired private SimpleReportService simpleReportService;
    @Autowired private SimpleReportOutputter simpleReportOutputter;
    
    @RequestMapping(value = "htmlView", method = RequestMethod.GET)
    public ModelAndView htmlView(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	// get report definition, model
        //-----------------------------------------------------------------------------------------
        Map<String, String> parameterMap = ServletUtil.getParameterMap(request);
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, true, userContext); // note we do actually load data
        
        // BASE
    	ModelAndView mav = baseHtmlExtView(reportDefinition, reportModel, request, response);
    	
        // data
        //-----------------------------------------------------------------------------------------
        List<List<String>> data = simpleReportService.getFormattedData(reportDefinition, reportModel, userContext);
        
        mav.addObject("pureHtml", true);
        mav.addObject("data", data);
        
    	return mav;
    }
    
    @RequestMapping(value = "extView", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public ModelAndView extView(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	// get report definition, model
        //-----------------------------------------------------------------------------------------
        Map<String, String> parameterMap = ServletUtil.getParameterMap(request);
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, false, userContext); // note we don't actually load data, that is the job of jsonData()
        
        // BASE
    	ModelAndView mav = baseHtmlExtView(reportDefinition, reportModel, request, response);
    	
    	// more optional values
        //-----------------------------------------------------------------------------------------
        boolean showLoadMask = ServletRequestUtils.getBooleanParameter(request, "showLoadMask", true);
        int refreshRate = ServletRequestUtils.getIntParameter(request, "refreshRate", 0);
        int width = ServletRequestUtils.getIntParameter(request, "width", 0);
        int height = ServletRequestUtils.getIntParameter(request, "height", 350);
        mav.addObject("showLoadMask", showLoadMask);
        mav.addObject("refreshRate", refreshRate);
        mav.addObject("width", width);
        mav.addObject("height", height);
        
        // jsonData URL
        //-----------------------------------------------------------------------------------------
        Map<String, String> inputMap = (Map<String, String>)mav.getModelMap().get("inputMap");
        String queryString = ServletUtil.buildSafeQueryStringFromMap(inputMap, true);
        String dataUrl = "/reports/simple/jsonData?" + queryString;
        dataUrl = ServletUtil.createSafeUrl(request, dataUrl);
        mav.addObject("dataUrl", dataUrl);
        
    	return mav;
    }
    
    /**
     * Prepares the mav with basic objects that both HTML and EXT report view need.
     */
    public ModelAndView baseHtmlExtView(YukonReportDefinition<BareReportModel> reportDefinition, BareReportModel reportModel, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        String viewJsp = ServletRequestUtils.getRequiredStringParameter(request, "viewJsp");
        String jspPath = SimpleReportViewJsp.valueOf(viewJsp).getJspPath();
        ModelAndView mav = new ModelAndView(jspPath);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // optional page module, showMenu, menuSelection values
        //-----------------------------------------------------------------------------------------
        String module = ServletRequestUtils.getStringParameter(request, "module", "blank");
        Boolean showMenu = ServletRequestUtils.getBooleanParameter(request, "showMenu", false);
        String menuSelection = ServletRequestUtils.getStringParameter(request, "menuSelection", "");
        String title = ServletRequestUtils.getStringParameter(request, "title", reportModel.getTitle());

        mav.addObject("module", module);
        mav.addObject("showMenu", showMenu);
        mav.addObject("menuSelection", menuSelection);
        
        // title
        //-----------------------------------------------------------------------------------------

        mav.addObject("reportTitle", StringEscapeUtils.escapeXml11(title));
        
        
        // column layout lists
        //-----------------------------------------------------------------------------------------
        ColumnLayoutData[] bodyColumns = reportDefinition.getReportLayoutData().getBodyColumns();
        List<ColumnInfo> columnInfo = simpleReportService.buildColumnInfoListFromColumnLayoutData(bodyColumns);
        
        mav.addObject("columnInfo", columnInfo);
        
        // include definition name and model in order to create links to other styles of the same report
        //-----------------------------------------------------------------------------------------
        mav.addObject("reportModel",reportModel);
        mav.addObject("definitionName", reportDefinition.getName());


        // include random info map
        //-----------------------------------------------------------------------------------------
        
        if (reportModel instanceof ReportModelMetaInfo) {
            ReportModelMetaInfo metaInfo = (ReportModelMetaInfo) reportModel;
            mav.addObject("metaInfo", metaInfo.getMetaInfo(userContext));
        }
        
        // include input values map
        //-----------------------------------------------------------------------------------------
        Map<String, String> inputMap = InputUtil.extractProperties(reportDefinition.getInputs(), reportModel);
        inputMap.put("def", reportDefinition.getName());
        mav.addObject("inputMap", inputMap);
        
        return mav;
    }

    @RequestMapping(value = "jsonData", method = RequestMethod.GET)
    public ModelAndView jsonData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // get report definition, model
        //-----------------------------------------------------------------------------------------
        Map<String, String> parameterMap = ServletUtil.getParameterMap(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, true, userContext);
        
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
    @RequestMapping(value = "csvView", method = RequestMethod.GET)
    public ModelAndView csvView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // get report definition, model
        //-----------------------------------------------------------------------------------------
        // Map<String, String> parameterMap = request.getParameterMap();
        Map<String, String> parameterMap = ServletUtil.getParameterMap(request);
        
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, true, userContext);
        String title = ServletRequestUtils.getStringParameter(request, "title", reportModel.getTitle());

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition","filename=\"" + ServletUtil.makeWindowsSafeFileName(title) + ".csv\"");
        OutputStream outputStream = response.getOutputStream();
        simpleReportOutputter.outputCsvReport(reportDefinition, reportModel, outputStream, userContext);
        
        return null;
    }


    /**
     * pdfView - export report data as a PDF file
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "pdfView", method = RequestMethod.GET)
    public ModelAndView pdfView(HttpServletRequest request, HttpServletResponse response) throws Exception {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
                
        Map<String, String> parameterMap = ServletUtil.getParameterMap(request);
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = simpleReportService.getReportModel(reportDefinition, parameterMap, true, userContext);
        String title = ServletRequestUtils.getStringParameter(request, "title", reportModel.getTitle());

        //force download of PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","attachment; filename=\"" + ServletUtil.makeWindowsSafeFileName(title) + ".pdf\"");
        
        OutputStream outputStream = response.getOutputStream();
        simpleReportOutputter.outputPdfReport(reportDefinition, reportModel, outputStream, userContext);
        
        return null;
    }

}
