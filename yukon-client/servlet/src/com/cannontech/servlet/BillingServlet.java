package com.cannontech.servlet;

/**
 * Creation date: (10/19/2001 3:32:09 PM)
 * @author: 
 */
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServlet;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.billing.mainprograms.BillingBean;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.device.DeviceMeterGroup;
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
			
		resp.setHeader("Cache-Control", "no-store"); //HTTP 1.1
		resp.setHeader("Pragma", "no-cache"); //HTTP 1.0
		resp.setDateHeader("Expires", 0); //prevents caching at the proxy server
		resp.setContentType("text/x-comma-separated-values");
	
		BillingBean localBean = (BillingBean)session.getAttribute(ServletUtil.ATT_BILLING_BEAN);
		if(localBean == null)
		{
			CTILogger.debug("Billing Bean is Null, generate new instance in session.");
			session.setAttribute(ServletUtil.ATT_BILLING_BEAN, new BillingBean());
		}
		
		String fileFormat = req.getParameter("fileFormat");
		String billGroupType = req.getParameter("billGroupType");
		String billGroup = req.getParameter("billGroup");
		String appendToFile = req.getParameter("appendToFile");
		String removeMultiplier = req.getParameter("removeMultiplier");
		String demandDays = req.getParameter("demandDays");
		String energyDays = req.getParameter("energyDays");
		String endDate = req.getParameter("endDate");
		
		if( fileFormat != null)
			localBean.setFileFormat(Integer.parseInt(fileFormat));
		else
			localBean.setFileFormat(FileFormatTypes.INVALID);
		
		if( billGroupType != null)
			localBean.setBillGroupType(Integer.parseInt(billGroupType));
		else
			localBean.setBillGroupType(DeviceMeterGroup.COLLECTION_GROUP);
		
		if( billGroup != null)
			localBean.setBillGroup(billGroup);
		else
			localBean.setBillGroup("");
		localBean.setAppendToFile(appendToFile != null);
		localBean.setRemoveMult(removeMultiplier != null);
	
		if( demandDays != null)
			localBean.setDemandDaysPrev(Integer.parseInt(demandDays));
		else
			localBean.setDemandDaysPrev(30);
		
		if( energyDays != null)
			localBean.setEnergyDaysPrev(Integer.parseInt(energyDays));
		else 
			localBean.setEnergyDaysPrev(7);
		    
		if( endDate != null)
			localBean.setEndDateStr(endDate);
		else
			localBean.setEndDate(ServletUtil.getToday());
		
		SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyyMMdd");
		String fileName = "billing";		
		fileName += fileNameFormat.format(localBean.getEndDate());
		fileName += ".csv";
		resp.addHeader("Content-Disposition", "filename=" + fileName);
		
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