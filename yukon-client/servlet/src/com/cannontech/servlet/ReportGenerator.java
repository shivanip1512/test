package com.cannontech.servlet;

/**
 * Parameters
 * 
 * start	- Start date string, (mm/dd/yy)
 * stop     - Stop date string, undefined (mm/dd/yy)
 * type 	- the type of report to create, value int from ReportTypes
 * period	- undefined
 * ext 		- gif | png | jpg | svg
 * page		- the page number (0 based) of the report to view/return (PNG)
 * fileName - the name of the file to download to
 * ACTION 	- the action to perform - DownloadReport | PagedReport
 * NoCache	- no cache will be stored in the session when this attribute exists
 * REDIRECT - 
 * REFERRER - 
 * 
 * Type specific parameters: 
 * EC_WORK_ORDER_DATA - 
 *  	OrderID (the workOrderID <Integer>), AccountID (the accountID <Integer>), ServiceStatus (status of work orders to search for)
 * 		SearchColumn (the column/field to search by, valid values in WorkOrderModel class <int>)
 * @author: Stacey Nebben
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.report.JFreeReport;
import org.jfree.report.modules.output.pageable.base.PageableReportProcessor;
import org.jfree.report.modules.output.pageable.base.ReportStateList;
import org.jfree.report.modules.output.pageable.graphics.G2OutputTarget;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.WorkOrderModel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.util.ServletUtil;

public class ReportGenerator extends javax.servlet.http.HttpServlet
{
			
	/**
	 * 
	 * Creation date: (12/9/99 3:24:30 PM)
	 * @param req javax.servlet.http.HttpServletRequest
	 * @param resp javax.servlet.http.HttpServletResponse
	 * @exception javax.servlet.ServletException The exception description.
	 * @exception java.io.IOException The exception description.
	 */
	public synchronized void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException
	{
		String destURL = req.getParameter( ServletUtil.ATT_REDIRECT );	//successsful action URL
		String errorURL = req.getParameter( ServletUtil.ATT_REFERRER );	//failed action URL

		try
		{	 	
			HttpSession session = req.getSession(false);

			//a string value for unique reports held in session.
			//ECID + type + startDate.toString() + stopDate.toString()
			String reportKey = "";
			boolean isKeyIncomplete = false;


//			DEBUG
			java.util.Enumeration enum1 = req.getParameterNames();
			  while (enum1.hasMoreElements()) {
				String ele = enum1.nextElement().toString();
				 System.out.println(" --" + ele + "  " + req.getParameter(ele));
			 }

			LiteYukonUser liteYukonUser = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
			//Default energycompany properties in case we can't find one?
			int energyCompanyID = -1;	//default?
			TimeZone tz = TimeZone.getDefault();	//init to the timezone of the running program
			if( EnergyCompanyFuncs.getEnergyCompany(liteYukonUser) != null)
			{
				energyCompanyID = EnergyCompanyFuncs.getEnergyCompany(liteYukonUser).getEnergyCompanyID();
				//Get the EnergyCompany user to find the TimzeZone
				LiteYukonUser ecUser = EnergyCompanyFuncs.getEnergyCompanyUser(energyCompanyID);
				tz = TimeZone.getTimeZone(AuthFuncs.getRolePropertyValue(ecUser, EnergyCompanyRole.DEFAULT_TIME_ZONE));
			}
			
			//Add ECId to the reportkey.
			reportKey += String.valueOf(energyCompanyID);
			
			boolean noCache = req.getParameter("NoCache") != null;

			String param;	//holder for the requested parameter
			int type=ReportTypes.SYSTEM_LOG_DATA;
			String ext = "pdf", action="";
			Date startDate = ServletUtil.getToday(), stopDate = ServletUtil.getTomorrow();

			//The report Type (see com.cannontech.analysis.ReportTypes)
			// Uses the type stored in the session if one can't be found, this is needed for PagedReport action
			param = req.getParameter("type");
			if( param != null)
			{
				type = Integer.valueOf(param).intValue();
				reportKey += String.valueOf(type);
			}
			else
				isKeyIncomplete = true;
			
			//The starting date for the report data.
			param = req.getParameter("start");
			if( param != null)
			{
				startDate= ServletUtil.parseDateStringLiberally(param, tz);
				reportKey += startDate.toString();	
			}
			else
				isKeyIncomplete = true;
				
			
			//The stop date for the data.
			param = req.getParameter("stop");
			if( param != null)
			{
				stopDate = ServletUtil.parseDateStringLiberally(param, tz);
				reportKey += stopDate.toString();
			}
			else
				isKeyIncomplete = true;	

			//The extension for the report, the format it is to be generated in
			param = req.getParameter("ext");
			if( param != null)
				ext = param.toLowerCase();
				
			//A filename for downloading the report to.
			String fileName = "Report";
			param = req.getParameter("fileName");
			if( param != null)
				fileName = param.toString();

			fileName += "." + ext;

			//The action of generating a report, content-disposition changes based on downloading or viewing option.
			param = req.getParameter("ACTION");
			if( param != null)
				action = param;
				
			if( param != null && param.equalsIgnoreCase("DownloadReport"))
				resp.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			else
				resp.setHeader("Content-Disposition", "inline; filename="+fileName);					

			//Define start and stop parameters for a default 90 day report.
/*			java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
			cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
			cal.set(java.util.Calendar.MINUTE, 0);
			cal.set(java.util.Calendar.SECOND, 0);
			cal.set(java.util.Calendar.MILLISECOND, 0);
			cal.add(java.util.Calendar.DATE, 1);
			long stop = cal.getTimeInMillis();
			cal.add(java.util.Calendar.DATE, -90);
			long start = cal.getTimeInMillis();
*/
			
			// Work order model specific parameters
			Integer orderID = null;
			Integer accountID = null;
			Integer serviceStatus = null;
			int searchColumn = WorkOrderModel.SEARCH_COL_NONE;
			
			if (type == ReportTypes.EC_WORK_ORDER_DATA) {
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
					if (serviceStatus.intValue() == 0) serviceStatus = null;
					reportKey += " SerStat" + serviceStatus;
				}
				param = req.getParameter("SearchColumn");
				if (param != null) {
					searchColumn = Integer.parseInt(param);
					reportKey += " SeaCol" + String.valueOf(searchColumn);
				}
			}
			
			if (!noCache) {
				if( !isKeyIncomplete ) //last "key" added to reportKey, through the current one it into the session
				{
					CTILogger.info("KEY " + reportKey);
					session.setAttribute("ReportKey", reportKey);
				}
				else
				{
					if(session.getAttribute("ReportKey") != null)
						reportKey = (String)session.getAttribute("ReportKey");
				}
			}
			
			//Create the report
			JFreeReport report = (JFreeReport)session.getAttribute(reportKey + "Report");
			if( noCache || report == null )
			{			
				//Initialize the report data and populate the TableModel (collectData).
				YukonReportBase rpt = ReportFuncs.createYukonReport(type);
				rpt.getModel().setECIDs(new Integer(energyCompanyID));
				rpt.getModel().setStartTime(startDate.getTime());
				rpt.getModel().setStopTime(stopDate.getTime());
				
				/** Set Model specific parameters */
				if( type == ReportTypes.EC_WORK_ORDER_DATA) {
					((WorkOrderModel)rpt.getModel()).setOrderID( orderID );
					((WorkOrderModel)rpt.getModel()).setAccountID( accountID );
					((WorkOrderModel)rpt.getModel()).setServiceStatus( serviceStatus );
					((WorkOrderModel)rpt.getModel()).setSearchColumn( searchColumn );
				}
				
				rpt.getModel().collectData();
				
				report = rpt.createReport();
				report.setData(rpt.getModel());
				
				if( !noCache )
					session.setAttribute(reportKey + "Report", report);
			}
			
			final ServletOutputStream out = resp.getOutputStream();
			
			if (!ext.equalsIgnoreCase("png")) {
				if (ext.equalsIgnoreCase("pdf")) {
					resp.setContentType("application/pdf");
					resp.addHeader("Content-Type", "application/pdf");
				}
				/*else if (ext.equalsIgnoreCase("jpg")) {
					resp.setContentType("image/jpeg");
				}*/
				
				ReportFuncs.outputYukonReport( report, ext, out );
				out.flush();
			}
			else {
				java.awt.print.PageFormat pageFormat = report.getDefaultPageFormat();
				
				//create buffered image
				BufferedImage image = ReportFuncs.createImage(pageFormat);
				final Graphics2D g2 = image.createGraphics();
				g2.setPaint(Color.white);
				g2.fillRect(0,0, (int) pageFormat.getWidth(), (int) pageFormat.getHeight());
				
				resp.setHeader("Content-Type", "image/png");

				final G2OutputTarget target = new G2OutputTarget(g2, pageFormat);
				final PageableReportProcessor processor = new PageableReportProcessor(report);
				processor.setOutputTarget(target);

				Object statelist = session.getAttribute(reportKey + "StateList");
				if( noCache || statelist == null)
				{			
					statelist = processor.repaginate();
					if( !noCache )
						session.setAttribute(reportKey + "StateList", statelist);
				}
				else
				{
					if( action.equalsIgnoreCase("PagedReport"))
					{
						int page = 0;  //The page number of the report to generate
						param = req.getParameter("page");
						if( param != null)
							page = Integer.valueOf(param).intValue();

						target.open();
						processor.processPage(((ReportStateList)statelist).get(page), target);
						ReportFuncs.encodePNG(out, image);
						target.close();
					}
				}
				out.flush();
			}
		}
		catch( Throwable t )
		{
			CTILogger.error("An exception was throw in GraphGenerator:  ");
			t.printStackTrace();
		}
		finally
		{
			if( destURL!= null ) {
				resp.sendRedirect(destURL);
			}
	//		else {
	//			resp.sendRedirect(req.getContextPath() + "/user/CILC/user_reporting.jsp");
	//		}
		}		
	}
}