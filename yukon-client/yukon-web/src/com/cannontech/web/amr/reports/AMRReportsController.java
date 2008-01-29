package com.cannontech.web.amr.reports;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.simplereport.SimpleReportViewJsp;
import com.cannontech.simplereport.YukonReportDefinition;

public class AMRReportsController extends MultiActionController  {
    
    private PointDao pointDao;
    private SimpleReportService simpleReportService;
    
    /**
     * For viewing the Archived Data report with crumbs to device detail page
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView archivedDataReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        String jspPath = SimpleReportViewJsp.ARCHIVED_DATA_REPORT.getJspPath();
        ModelAndView mav = new ModelAndView(jspPath);
        
        // model stuff
        String definitionName = ServletRequestUtils.getRequiredStringParameter(request, "def");
        Integer pointId = ServletRequestUtils.getRequiredIntParameter(request, "pointId");
        Long startDate = ServletRequestUtils.getRequiredLongParameter(request, "startDate");
        Long stopDate = ServletRequestUtils.getRequiredLongParameter(request, "stopDate");
        
        mav.addObject("definitionName", definitionName);
        mav.addObject("pointId", pointId);
        mav.addObject("startDate", startDate);
        mav.addObject("stopDate", stopDate);
        
        // report title
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = reportDefinition.createBean();
        mav.addObject("reportTitle", reportModel.getTitle());
        
        // device id for crumbs
        LitePoint point = pointDao.getLitePoint(pointId);
        Integer deviceId = point.getPaobjectID();
        mav.addObject("deviceId", deviceId);
        
        return mav;
    }
    
    /**
     * For view Bulk Import Results report with crumbs to bulk importer page
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView bulkImportResultsReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        String jspPath = SimpleReportViewJsp.BULK_IMPORT_RESULTS.getJspPath();
        ModelAndView mav = new ModelAndView(jspPath);
        
        // model stuff
        String definitionName = ServletRequestUtils.getRequiredStringParameter(request, "def");
        String reportType = ServletRequestUtils.getRequiredStringParameter(request, "reportType");
        
        mav.addObject("definitionName", definitionName);
        mav.addObject("reportType", reportType);
        
        // report title
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = reportDefinition.createBean();
        mav.addObject("reportTitle", reportModel.getTitle());
        
        return mav;
    }
    
    /**
     * For viewing group Devices report with crumbs for group home and selected group
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView groupDevicesReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        String jspPath = SimpleReportViewJsp.GROUP_DEVICES.getJspPath();
        ModelAndView mav = new ModelAndView(jspPath);
        
        // model stuff
        String definitionName = ServletRequestUtils.getRequiredStringParameter(request, "def");
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        
        mav.addObject("definitionName", definitionName);
        mav.addObject("groupName", groupName);
        
        // report title
        YukonReportDefinition<BareReportModel> reportDefinition = simpleReportService.getReportDefinition(request);
        BareReportModel reportModel = reportDefinition.createBean();
        mav.addObject("reportTitle", reportModel.getTitle());
        
        return mav;
    }

    @Required
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }

    @Required
    public void setSimpleReportService(SimpleReportService simpleReportService) {
        this.simpleReportService = simpleReportService;
    }
    
  
    
    
  

}
