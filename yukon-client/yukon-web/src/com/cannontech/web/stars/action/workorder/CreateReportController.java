package com.cannontech.web.stars.action.workorder;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.report.JFreeReport;
import org.jfree.report.function.FunctionInitializeException;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.gui.ReportBean;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.bean.WorkOrderBean;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class CreateReportController extends StarsWorkorderActionController {

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response, 
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {
        
        //A filename for downloading the report to.
        String ext = "pdf";
        String param = request.getParameter("ext");
        if(param != null)
            ext = param;
        String fileName = "WorkOrders";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        fileName += "_"+format.format(new Date()) + "." + ext;          
        response.addHeader("Content-Disposition", "attachment; filename=" + fileName);

        JFreeReport report = createReport(user, session);
        final ServletOutputStream out = response.getOutputStream();
        try {
            if( ext.equalsIgnoreCase("pdf"))
            {
                response.setContentType("application/pdf");
                response.addHeader("Content-Type", "application/pdf");                  
                ReportFuncs.outputYukonReport( report, ext, out );
            }
            else if( ext.equalsIgnoreCase("csv")){
                ReportFuncs.outputYukonReport( report, ext, out);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }
    
    private JFreeReport createReport(StarsYukonUser user, HttpSession session) {
        
        WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
        LiteStarsEnergyCompany liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany( user.getEnergyCompanyID() );
        
        ReportBean reportBean = new ReportBean();
        reportBean.setType(ReportTypes.EC_WORK_ORDER);
        reportBean.setUserID(user.getUserID());
        ((WorkOrderModel)reportBean.getModel()).loadData(liteStarsEC, workOrderBean.getWorkOrderList());

        reportBean.getModel().setTimeZone(workOrderBean.getEnergyCompany().getDefaultTimeZone());
        reportBean.getModel().setEnergyCompanyID(workOrderBean.getEnergyCompany().getEnergyCompanyId());

        JFreeReport report = null;
        //Create the report
        try {
            //NOTE:  Don't use the reportBean.createReport() method.  We don't want to call collectData() since we are using the filtered list instead!
//          Create an instance of JFreeReport from the YukonReportBase
            YukonReportBase reportBase = ReportFuncs.createYukonReport(reportBean.getModel());
            report = reportBase.createReport();
            report.setData(reportBase.getModel());
        } catch (FunctionInitializeException e) {
            e.printStackTrace();
        }
        return report;
    }
    
}
