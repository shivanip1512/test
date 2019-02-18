package com.cannontech.servlet;

/**
 * Parameters
 * 
 * 
 * start    - Start date string, (mm/dd/yy)
 * stop     - Stop date string, undefined (mm/dd/yy)
 * type     - the type of report to create, value int from ReportTypes
 * period   - undefined
 * ext      - gif | png | jpg
 * page     - the page number (0 based) of the report to view/return (PNG)
 * fileName - the name of the file to download to
 * ACTION   - the action to perform - DownloadReport | PagedReport | LoadParameters
 * NoCache  - no cache will be stored in the session when this attribute exists
 * REDIRECT -
 * REFERRER -
 * 
 * Type specific parameters:
 * EC_WORK_ORDER_DATA -
 *       OrderID (the workOrderID <Integer>), AccountID (the accountID <Integer>), ServiceStatus (status of work orders to search for)
 *       SearchColumn (the column/field to search by, valid values in WorkOrderModel class <int>)
 * 
 * @author: Stacey Nebben
 */

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.report.JFreeReport;
import org.jfree.report.modules.output.csv.CSVQuoter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.WebUtils;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.gui.ReportBean;
import com.cannontech.analysis.tablemodel.MeterReadModel;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class ReportGenerator extends javax.servlet.http.HttpServlet {
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        String destURL = req.getParameter(ServletUtil.ATT_REDIRECT); // successsful action URL

        // a string value for unique reports held in session.
        // ECID + type + startDate.toString() + stopDate.toString()
        String reportKey = "";

        // DEBUG
        java.util.Enumeration enum1 = req.getParameterNames();
        while (enum1.hasMoreElements()) {
            String ele = enum1.nextElement().toString();
            System.out.println(" --" + ele + "  " + req.getParameter(ele));
        }

        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(req);
        TimeZone tz = yukonUserContext.getTimeZone();

        YukonSpringHook.getBean(RolePropertyDao.class).verifyRole(YukonRole.REPORTING,  yukonUserContext.getYukonUser());

        // Default energycompany properties in case we can't find one?
        LiteYukonUser liteYukonUser = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
        Integer energyCompanyID =
            YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(liteYukonUser).getId();

        File tempDir = WebUtils.getTempDir(getServletContext());
        File tempFile = File.createTempFile("reportCache", ".tmp", tempDir);
        FileOutputStream tempStream = new FileOutputStream(tempFile);
        BufferedOutputStream bufferedTemp = new BufferedOutputStream(tempStream);

        ReportBean reportBean = (ReportBean) session.getAttribute(ServletUtil.ATT_REPORT_BEAN);
        if (reportBean == null) {
            session.setAttribute(ServletUtil.ATT_REPORT_BEAN, new ReportBean());
            reportBean = (ReportBean) session.getAttribute(ServletUtil.ATT_REPORT_BEAN);
        }

        verifyAccess(yukonUserContext, reportBean);
        
        synchronized (reportBean) {

            String param; // holder for the requested parameter
            String ext = "pdf", action = "";

            // The report Type (see com.cannontech.analysis.ReportTypes)
            // Uses the type stored in the session if one can't be found, this is needed for PagedReport
            // action
            param = req.getParameter("type");
            if (param != null) {
                reportBean.setType(ReportTypes.valueOf(param));
            }
            // Setting the type will reset the model instance if the type has changed.
            if (reportBean.getReportType() != null) {
                reportKey += String.valueOf(reportBean.getReportType());
            }

            // save start and stop date
            param = req.getParameter("startDate");
            reportBean.setStart(param);
            param = req.getParameter("stopDate");
            reportBean.setStop(param);

            reportBean.createController();
            reportBean.getModel().setTimeZone(tz);
            // reportBean.getModel().setParameters(req);
            reportBean.getReportController().setRequestParameters(req);
            reportBean.getModel().setEnergyCompanyID(energyCompanyID);
            reportBean.getModel().setUserID(liteYukonUser.getUserID());

            // Add ECId to the reportkey.
            reportKey += String.valueOf(energyCompanyID);

            // The starting date for the report data.
            reportKey += reportBean.getStartDate().toString();

            // The stop date for the data.
            reportKey += reportBean.getStopDate();

            // The extension for the report, the format it is to be generated in
            param = req.getParameter("ext");
            if (param != null) {
                ext = param.toLowerCase();
            }

            // A filename for downloading the report to.
            String fileName = "Report";
            param = req.getParameter("fileName");
            if (param != null) {
                fileName = param.toString();
            }
            fileName += "." + ext;

            // The action of generating a report, content-disposition changes based on downloading or viewing
            // option.
            param = req.getParameter("ACTION");
            if (param != null) {
                action = param;
            }

            if (param != null && param.equalsIgnoreCase("DownloadReport")) {
                resp.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            } else if (param != null && param.equalsIgnoreCase("GenerateMissedMeterList")) {
                resp.addHeader("Content-Disposition", "attachment; filename=MissedList.txt");
            } else {
                resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            }

            // Work order model specific parameters
            Integer orderID = null;
            Integer accountID = null;
            Integer serviceStatus = null;
            int searchColumn = WorkOrderModel.SEARCH_COL_NONE;

            if (reportBean.getReportType() == ReportTypes.EC_WORK_ORDER) {
                param = req.getParameter("OrderID");
                if (param != null) {
                    orderID = Integer.valueOf(param);
                    reportKey += " OrdID" + orderID;
                }
                param = req.getParameter("AccountID");
                if (param != null) {
                    accountID = Integer.valueOf(param);
                    reportKey += " AcctID" + accountID;
                }
                param = req.getParameter("ServiceStatus");
                if (param != null) {
                    serviceStatus = Integer.valueOf(param);
                    if (serviceStatus.intValue() == 0) {
                        serviceStatus = null;
                    }
                    reportKey += " SerStat" + serviceStatus;
                }
                param = req.getParameter("SearchColumn");
                if (param != null) {
                    searchColumn = Integer.parseInt(param);
                    reportKey += " SeaCol" + String.valueOf(searchColumn);
                }
            }

            // Only continue on if our ACTION is to do so.
            if (action.equalsIgnoreCase("DownloadReport") || action.equalsIgnoreCase("PagedReport")) {

                // Create the report
                JFreeReport report = null;
                /* Set Model specific parameters */
                if (reportBean.getReportType() == ReportTypes.EC_WORK_ORDER) {
                    ((WorkOrderModel) reportBean.getModel()).setOrderID(orderID);
                    ((WorkOrderModel) reportBean.getModel()).setAccountID(accountID);
                    ((WorkOrderModel) reportBean.getModel()).setServiceStatus(serviceStatus);
                    ((WorkOrderModel) reportBean.getModel()).setSearchColumn(searchColumn);
                }

                report = reportBean.createReport();
                ReportFuncs.outputYukonReport(report, ext, bufferedTemp);
                bufferedTemp.close();

                if (!ext.equalsIgnoreCase("png")) {
                    if (ext.equalsIgnoreCase("pdf")) {
                        resp.setContentType("application/pdf");
                        // resp.addHeader("Content-Type", "application/pdf");
                    }
                }
                if (ext.equalsIgnoreCase("csv")) {
                    resp.setContentType("text/csv");
                }

            } else if (action.equalsIgnoreCase("GenerateMissedMeterList")) {
                // Create the report
                JFreeReport report = null;
                // Force a MISSED MeterRead report
                ((MeterReadModel) reportBean.getModel()).setMeterReadType(MeterReadModel.MISSED_METER_READ_TYPE);
                report = reportBean.createReport();

                resp.setContentType("text/plain");
                CSVQuoter quoter = new CSVQuoter(",");

                // Write data
                for (int r = 0; r < report.getData().getRowCount(); r++) {
                    String rawValue = String.valueOf(report.getData().getValueAt(r, MeterReadModel.DEVICE_NAME_COLUMN));
                    bufferedTemp.write(quoter.doQuoting(rawValue).getBytes());
                    bufferedTemp.write("\r\n".getBytes());
                }
                bufferedTemp.close();
            } else {
                if (action.equalsIgnoreCase("LoadParameters")) {
                    if (reportBean.getReportType() == ReportTypes.DEVICE_READ_STATISTICS_SUMMARY) {
                        // 20100804 - SN - (YUK-8940) Requirement for this report to default to startDate=3
                        // days ago, stopDate=midnight tonight
                        Date midnightTonight = TimeUtil.getMidnightTonight(tz);
                        Date threeDaysAgo = TimeUtil.getMidnight(tz, -2); // use -2 because this method starts
                                                                          // at 00:00 this morning
                        reportBean.getStartDate().setTime(threeDaysAgo.getTime());
                        reportBean.getStopDate().setTime(midnightTonight.getTime());
                    }
                }

                if (destURL != null) {
                    destURL = ServletUtil.createSafeRedirectUrl(req, destURL);
                    resp.sendRedirect(destURL);
                }
            }
        } // close sync block
        long fileSize = tempFile.length();
        resp.setContentLength((int) fileSize);
        FileCopyUtils.copy(new FileInputStream(tempFile), resp.getOutputStream());
        tempFile.delete();
    }

    private void verifyAccess(YukonUserContext yukonUserContext, ReportBean reportBean) {
        switch (reportBean.getReportGroup()) {
        case ADMINISTRATIVE:
        case SETTLEMENT:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyProperty(YukonRoleProperty.ADMIN_REPORTS_GROUP, yukonUserContext.getYukonUser());
            break;
        case CAP_CONTROL:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyProperty(YukonRoleProperty.CAP_CONTROL_REPORTS_GROUP, yukonUserContext.getYukonUser());
            break;
        case CCURT:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyProperty(YukonRoleProperty.CI_CURTAILMENT_REPORTS_GROUP, yukonUserContext.getYukonUser());
            break;
        case DATABASE:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyProperty(YukonRoleProperty.DATABASE_REPORTS_GROUP, yukonUserContext.getYukonUser());
            break;
        case LOAD_MANAGEMENT:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyProperty(YukonRoleProperty.LOAD_MANAGEMENT_REPORTS_GROUP, yukonUserContext.getYukonUser());
            break;
        case METERING:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyProperty(YukonRoleProperty.AMR_REPORTS_GROUP, yukonUserContext.getYukonUser());
            break;
        case STARS:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyProperty(YukonRoleProperty.STARS_REPORTS_GROUP, yukonUserContext.getYukonUser());
            break;
        case STATISTICAL:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyProperty(YukonRoleProperty.STATISTICAL_REPORTS_GROUP, yukonUserContext.getYukonUser());
            break;
        case OTHER:
        default:
            YukonSpringHook.getBean(RolePropertyDao.class).verifyRole(YukonRole.REPORTING, yukonUserContext.getYukonUser());
            break;
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(javax.servlet.http.HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}