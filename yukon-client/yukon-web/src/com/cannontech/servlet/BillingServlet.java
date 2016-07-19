package com.cannontech.servlet;

/**
 * Creation date: (10/19/2001 3:32:09 PM)
 * @author: 
 */
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.mainprograms.BillingBean;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.util.ServletUtil;


public class BillingServlet extends HttpServlet
{
    private final static String BASE_URL_PATH = "/billing/home";
    private final static int DEFAULT_DEMAND_DAYS = 30;
    private final static int DEFAULT_ENERGY_DAYS = 7;

    String []exportArray = null;

    // Included for proper log-on forwarding:
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + BASE_URL_PATH);
    }

/**
 * Creation date: (12/9/99 3:39:10 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        javax.servlet.http.HttpSession session = req.getSession(false);

        Enumeration<?> enum1 = req.getParameterNames();
        while (enum1.hasMoreElements()) {
            String ele = enum1.nextElement().toString();
            CTILogger.info(" --" + ele + "  " + req.getParameter(ele));
        }

//      resp.setHeader("Cache-Control", "no-store"); //HTTP 1.1
//      resp.setHeader("Pragma", "no-cache"); //HTTP 1.0
        resp.setDateHeader("Expires", 0); //prevents caching at the proxy server
        resp.setContentType("text/x-comma-separated-values");
    
        BillingBean localBean = (BillingBean)session.getAttribute(ServletUtil.ATT_BILLING_BEAN);
        if(localBean == null)
        {
            localBean = new BillingBean();
            CTILogger.debug("Billing Bean is Null, generate new instance in session.");
            session.setAttribute(ServletUtil.ATT_BILLING_BEAN, localBean);
        }
        localBean.setErrorMsg(null);
        
        String fileFormat = req.getParameter("fileFormat");
        final int fileFormatValue = (fileFormat != null) ?
                Integer.parseInt(fileFormat) : FileFormatTypes.INVALID;

        String removeMultiplier = req.getParameter("removeMultiplier");
        String demandDays = req.getParameter("demandDays");
        String energyDays = req.getParameter("energyDays");
        String endDate = req.getParameter("endDate");

        final int demandDaysValue = (demandDays != null) ? Integer.parseInt(demandDays) : DEFAULT_DEMAND_DAYS;

        final int energyDaysValue = (energyDays != null) ? Integer.parseInt(energyDays) : DEFAULT_ENERGY_DAYS;

        localBean.setFileFormat(fileFormatValue);
        localBean.setAppendToFile(false);
        localBean.setRemoveMult(removeMultiplier != null);
        localBean.setDemandDaysPrev(demandDaysValue);
        localBean.setEnergyDaysPrev(energyDaysValue);
        
        if( endDate != null)
            localBean.setEndDateStr(endDate);
        else
            localBean.setEndDate(ServletUtil.getToday());
        
        String[] billGroupArray = req.getParameterValues("billGroup");
        if (fileFormatValue != FileFormatTypes.CURTAILMENT_EVENTS_ITRON) {
            if (billGroupArray == null) {
                localBean.setErrorMsg("A billing group must be selected.");
                resp.sendRedirect(req.getContextPath() + BASE_URL_PATH);
                return;
            }
            localBean.setBillGroup(Arrays.asList(billGroupArray));
        }
        
        SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd");
        final StringBuilder fileName = new StringBuilder("billing");
        fileName.append(fileNameFormat.format(localBean.getEndDate()));
        
        fileName.append(FileFormatTypes.getFileExtensionByFormatId(fileFormatValue));

        resp.addHeader("Content-Disposition", "attachment;filename=\"" + fileName.toString()+"\"");
        
        javax.servlet.ServletOutputStream out = null;
        try
        {
            out = resp.getOutputStream();
            localBean.generateFile( out );
            out.flush();
            CTILogger.debug("*** Just tried to flush the out!");
        }
        catch (java.io.IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
}