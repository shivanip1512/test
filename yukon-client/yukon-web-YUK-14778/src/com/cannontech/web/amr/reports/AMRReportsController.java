package com.cannontech.web.amr.reports;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.analysis.tablemodel.BareReportModel;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.simplereport.SimpleReportService;
import com.cannontech.simplereport.YukonReportDefinition;
import com.cannontech.simplereport.YukonReportDefinitionFactory;

public class AMRReportsController extends MultiActionController  {
    
    private PointDao pointDao;
    private SimpleReportService simpleReportService;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private YukonReportDefinitionFactory<BareReportModel> reportDefinitionFactory;
    @Autowired private DeviceDao deviceDao;
    
    /**
     * For viewing the Archived Data report crumbs back to high bill complaint page
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView hbcArchivedDataReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        ModelAndView mav = new ModelAndView("reports/htmlHBCArchivedDataReportView.jsp");
        
        setupArchivedDataReportMav(request, mav);
        
        // additional info needed to create a bread crumb link back to HBC
        mav.addObject("analyze", ServletRequestUtils.getRequiredStringParameter(request, "analyze"));
        mav.addObject("deviceId", ServletRequestUtils.getRequiredStringParameter(request, "deviceId"));
        mav.addObject("getReportStartDate", ServletRequestUtils.getRequiredStringParameter(request, "getReportStartDate"));
        mav.addObject("getReportStopDate", ServletRequestUtils.getRequiredStringParameter(request, "getReportStopDate"));
        mav.addObject("chartRange", ServletRequestUtils.getRequiredStringParameter(request, "chartRange"));
        
        return mav;
    }
    
    /**
     * For viewing the Archived Data report with crumbs to device detail page
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView archivedDataReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        // mav
        ModelAndView mav = new ModelAndView("reports/htmlArchivedDataReportView.jsp");
        
        setupArchivedDataReportMav(request, mav);
        
        return mav;
    }
    
    private void setupArchivedDataReportMav(HttpServletRequest request, ModelAndView mav) throws Exception {
        
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
        
        // information for bread crumbs
        LitePoint point = pointDao.getLitePoint(pointId);
        Integer deviceId = point.getPaobjectID();
        PaoType type = deviceDao.getYukonDevice(deviceId).getDeviceType();
        
        mav.addObject("isWaterMeter", type.isWaterMeter());
        mav.addObject("deviceId", deviceId);
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
        ModelAndView mav = new ModelAndView("reports/htmlBulkImportResultsView.jsp");
        
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
        
        ModelAndView mav = new ModelAndView("reports/htmlGroupDevicesReportView.jsp");
        
        // group
        String groupName = ServletRequestUtils.getRequiredStringParameter(request, "groupName");
        DeviceGroup deviceGroup = deviceGroupService.resolveGroupName(groupName);
        mav.addObject("deviceGroup", deviceGroup);
        
        // report title
        YukonReportDefinition<BareReportModel> reportDefinition = reportDefinitionFactory.getReportDefinition("deviceGroupDefinition");
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
