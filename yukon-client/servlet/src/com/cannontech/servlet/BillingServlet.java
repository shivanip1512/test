package com.cannontech.servlet;

/**
 * Creation date: (10/19/2001 3:32:09 PM)
 * @author: 
 */
import java.text.SimpleDateFormat;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.mainprograms.BillingBean;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.util.ServletUtil;

public class BillingServlet extends HttpServlet
{	
	static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
	
	String []exportArray = null;

/**
 * Creation date: (12/9/99 3:39:10 PM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * @exception javax.servlet.ServletException The exception description.
 * @exception java.io.IOException The exception description.
 */
public void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
	throws javax.servlet.ServletException, java.io.IOException
	{
		javax.servlet.http.HttpSession session = req.getSession(false);
		if (session == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.jsp");
			return;
		}

		java.util.Enumeration enum1 = req.getParameterNames();
		  while (enum1.hasMoreElements()) {
			String ele = enum1.nextElement().toString();
			 CTILogger.info(" --" + ele + "  " + req.getParameter(ele));
		}	
			
//		resp.setHeader("Cache-Control", "no-store"); //HTTP 1.1
//		resp.setHeader("Pragma", "no-cache"); //HTTP 1.0
		resp.setDateHeader("Expires", 0); //prevents caching at the proxy server
		resp.setContentType("text/x-comma-separated-values");
	
		BillingBean localBean = (BillingBean)session.getAttribute(ServletUtil.ATT_BILLING_BEAN);
		if(localBean == null)
		{
            localBean = new BillingBean();
			CTILogger.debug("Billing Bean is Null, generate new instance in session.");
			session.setAttribute(ServletUtil.ATT_BILLING_BEAN, localBean);
		}
		
		String fileFormat = req.getParameter("fileFormat");
		String billGroupStr = req.getParameter("billGroup");
		String[] billGroupArray = StringUtils.split(billGroupStr, ",");
        Validate.notNull(billGroupArray, "a billing group must be selected");
		String removeMultiplier = req.getParameter("removeMultiplier");
		String demandDays = req.getParameter("demandDays");
		String energyDays = req.getParameter("energyDays");
		String endDate = req.getParameter("endDate");
		
        final int fileFormatValue = (fileFormat != null) ?
                Integer.parseInt(fileFormat) : FileFormatTypes.INVALID;
                
        final int demandDaysValue = (demandDays != null) ?
                Integer.parseInt(demandDays) : 30;
        
        final int energyDaysValue = (energyDays != null) ?
                Integer.parseInt(energyDays) : 7;
               
        localBean.setFileFormat(fileFormatValue);
        localBean.setBillGroup(Arrays.asList(billGroupArray));
		localBean.setAppendToFile(false);
		localBean.setRemoveMult(removeMultiplier != null);
		localBean.setDemandDaysPrev(demandDaysValue);
		localBean.setEnergyDaysPrev(energyDaysValue);
        
		if( endDate != null)
			localBean.setEndDateStr(endDate);
		else
			localBean.setEndDate(ServletUtil.getToday());
		
		SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd");
        final StringBuilder fileName = new StringBuilder("billing");
        fileName.append(fileNameFormat.format(localBean.getEndDate()));
        
        if (fileFormatValue == FileFormatTypes.ITRON_REGISTER_READINGS_EXPORT) {
            fileName.append(".xml");
        } else {
            fileName.append(".txt");
        }

		resp.addHeader("Content-Disposition", "attachment;filename=" + fileName.toString());
		
		if( req.getParameter("generate") != null)
		{		    
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
}