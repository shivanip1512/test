package com.cannontech.web.stars.action.workorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.gui.ReportBean;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.bean.WorkOrderBean;
import com.cannontech.web.stars.action.StarsWorkorderActionController;

public class CreateReportController extends StarsWorkorderActionController {

    @Autowired private EnergyCompanyService ecService;

    @Override
    public void doAction(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final StarsYukonUser user, final LiteStarsEnergyCompany energyCompany) throws Exception {

        // A filename for downloading the report to.
        String ext = "pdf";
        String param = request.getParameter("ext");
        if (param != null) {
            ext = param;
        }
        String fileName = "WorkOrders";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        fileName += "_" + format.format(new Date()) + "." + ext;
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        MasterReport report = createReport(user, session);
        final ServletOutputStream out = response.getOutputStream();
        try {
            ReportBean reportBean = (ReportBean) session.getAttribute(ServletUtil.ATT_REPORT_BEAN);
            if (ext.equalsIgnoreCase("pdf")) {
                response.setContentType("application/pdf");
                response.addHeader("Content-Type", "application/pdf");
                
                ReportFuncs.outputYukonReport(report, ext, out, reportBean.getModel());
            } else if (ext.equalsIgnoreCase("csv")) {
                ReportFuncs.outputYukonReport(report, ext, out, reportBean.getModel());
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String redirect = this.getRedirect(request);
        response.sendRedirect(redirect);
    }

    private MasterReport createReport(StarsYukonUser user, HttpSession session) {

        WorkOrderBean workOrderBean = (WorkOrderBean) session.getAttribute("workOrderBean");
        LiteStarsEnergyCompany liteStarsEC = StarsDatabaseCache.getInstance().getEnergyCompany(user.getEnergyCompanyID());

        ReportBean reportBean = new ReportBean();
        reportBean.setType(ReportTypes.EC_WORK_ORDER);
        reportBean.setUserID(user.getUserID());
        ((WorkOrderModel) reportBean.getModel()).loadData(liteStarsEC, workOrderBean.getWorkOrderList());
        TimeZone ecTimezone = ecService.getDefaultTimeZone(workOrderBean.getEnergyCompany().getEnergyCompanyId());
        reportBean.getModel().setTimeZone(ecTimezone);
        reportBean.getModel().setEnergyCompanyID(workOrderBean.getEnergyCompany().getEnergyCompanyId());

        MasterReport report = null;
        // Create the report
        try {
            // NOTE: Don't use the reportBean.createReport() method. We don't want to call collectData() since we are using the
            // filtered list instead!
            YukonReportBase reportBase = ReportFuncs.createYukonReport(reportBean.getModel());
            report = reportBase.createReport();
            report.setDataFactory(new TableDataFactory("default", reportBase.getModel()));
        } catch (FunctionProcessingException e) {
            e.printStackTrace();
        }
        return report;
    }

}
