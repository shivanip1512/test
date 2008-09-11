package com.cannontech.servlet;

/**
 Creates a local bean, com.cannontech.gui.yc.YC, (if one not found) to contain
 the request parameter values. The calling jsp may create the bean instance to
 populate the required fields instead, though.  (Need some more work on opt 2.)
 
 A bean is used, created by the jsp or here, so that only one YC instance exists for 
 each session which can be reference throughout the session.
 Does not currently parse any of the properties from the bean, relies on 
 getting the values from the request message.
 
 If deviceID is specified, it will override any serialNumber specified!
 If routeID specified, it is only applicable for looplocate route command
  or when sending to a serialNumber as perhibited by YC.class and not by this servlet.
 (!Changes may be made to YC.class to handle route selection differently!)
 
PARAMETERS
-------------------------------------------
 deviceID           - id of the device to send the command to
 serialNumber 		- serial number of the meter
 [* deviceID or serialNumber must exist.]
 routeID			- route to send command on
 command            - specific command string to send to device
 function			- what should be done (?)
 clearResult		- flag (not null)indicating to clear the resultText data field
 timeOut			- Time in millis to wait for a response to come back from YC before ignoring it.
 REDIRECT			- where the servlet should go after the post
 updateDB			- flag for writing command result to the database(RPH)
 action				- values [updateDB]
*/

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.util.ServletUtil;
import com.cannontech.yc.bean.YCBean;


public class CommanderServlet extends javax.servlet.http.HttpServlet 
{
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
	{	
		javax.servlet.http.HttpSession session = req.getSession(false);
		if (session == null)
		{
			resp.sendRedirect(req.getContextPath() + "/login.jsp");
			return;
		}

		/**Create a YC bean for the session if one does not already exist.*/
		YCBean localBean = (YCBean)session.getAttribute(ServletUtil.ATT_YC_BEAN);
		if(localBean == null)
		{
			session.setAttribute(ServletUtil.ATT_YC_BEAN, new YCBean());
			localBean = (YCBean)session.getAttribute(ServletUtil.ATT_YC_BEAN);
		}

        String numDays = req.getParameter("numDays");
        if( numDays != null)
            localBean.setPeakProfileDays(Integer.valueOf(numDays));
		
		/**Debug print of all parameter names.*/
/*		java.util.Enumeration enum1 = req.getParameterNames();
		  while (enum1.hasMoreElements()) {
			String ele = enum1.nextElement().toString();
			 CTILogger.info(" --" + ele + "  " + req.getParameter(ele));
		}
*/
		 
		String redirectURL = req.getParameter("REDIRECT");

		/** PointID is rarely collected, normally when no command is being issued, used for page reloads with pointID needed for rph data display*/
		String pointID = req.getParameter("pointID");
		if( pointID != null)
			localBean.setPointID(Integer.parseInt(pointID));
		
		/**deviceID(opt1) or SerialNumber(opt2) must exist!
		 * deviceID/serialNumber command is sent. */
		String deviceID = req.getParameter("deviceID");
		if( deviceID != null )
			localBean.setLiteYukonPao(Integer.parseInt(deviceID));
		else
			localBean.setLiteYukonPao(PAOGroups.INVALID);
		
		String serialNumber = req.getParameter("serialNumber");
		if( serialNumber != null)
			localBean.setSerialNumber(serialNumber);
		else
			localBean.setSerialNumber(PAOGroups.STRING_INVALID);
		
		String lpDate = req.getParameter("lpDate");	//only applicable for retrieving historical data (such as lp data)
		if( lpDate != null)
			localBean.setLPDateStr(lpDate);
			
		//Flag to clear the resultText, no commands sent
		String clear = req.getParameter("clearText");

		//Action to do:  "SelectDevice", set deviceID and redirect to commander page.
		String action = req.getParameter("action");

		if (clear != null)
			localBean.clearResultText();
		else if( action != null && action.equalsIgnoreCase("SavePeakReport"))
		{
			resp.addHeader("Content-Disposition", "attachment; filename=PeakReport.txt");
			
			final ServletOutputStream out = resp.getOutputStream();
		  	resp.setContentType("text/plain");
//		  	CSVQuoter quoter = new CSVQuoter(",");

            List<LitePoint> litePoints = DaoFactory.getPointDao().getLitePointsByPaObjectId(localBean.getLiteYukonPao().getLiteID());
			if( litePoints != null)
			{
                for (LitePoint point : litePoints) {
					if(point.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND)
					{
						out.write(new String("LP Peak Report - " + point.getPointName() + "\r\n").getBytes());
						
						PointData pointData = (PointData)localBean.getRecentPointData(localBean.getLiteYukonPao().getLiteID(), PointTypes.PT_OFFSET_LPROFILE_KW_DEMAND, PointTypes.LP_PEAK_REPORT);						
						if( pointData != null)
						{
							String tempStr = pointData.getStr();
							int beginIndex = 0;
							int endIndex = tempStr.indexOf("\n");
							while( endIndex > 0)
							{
								out.write((tempStr.substring(beginIndex, endIndex) + "\r\n").getBytes()); 
								tempStr = tempStr.substring(endIndex+1);
								endIndex = tempStr.indexOf("\n");
							}
							out.write(new String("\r\n").getBytes());
						}
					}
					else if(point.getPointOffset() == PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND)
					{
						out.write(new String("LP Peak Report - " + point.getPointName() + "\r\n").getBytes());
						
						PointData pointData = (PointData)localBean.getRecentPointData(localBean.getLiteYukonPao().getLiteID(), PointTypes.PT_OFFSET_LPROFILE_VOLTAGE_DEMAND, PointTypes.LP_PEAK_REPORT);						
						if( pointData != null)
						{
							String tempStr = pointData.getStr();
							int beginIndex = 0;
							int endIndex = tempStr.indexOf("\n");
							while( endIndex > 0)
							{
								out.write( (tempStr.substring(beginIndex, endIndex) + "\r\n").getBytes()); 
								tempStr = tempStr.substring(endIndex+1);
								endIndex = tempStr.indexOf("\n");
							}
							out.write(new String("\r\n").getBytes());
						}
					}
				}
			} 
			return;
		}
		else if( action != null && action.equalsIgnoreCase("LoadRPHData"))
		{
			localBean.loadRPHData();	//reload the current RPH Data vector, based on selected pointid and timestamp		    
		}
		else if( action!= null && action.equalsIgnoreCase("SelectDevice"))
		{
			redirectURL = redirectURL.substring(0, redirectURL.lastIndexOf('/')+1).concat("CommandDevice.jsp");
			System.out.println("Redirect: "+req.getParameter("REDIRECT"));
			System.out.println("Referrer: "+req.getParameter("REFERRER"));
		}
		else
		{
			String function = req.getParameter("function");	//user friendly command string
			String command = req.getParameter("command");	//system command string
			/** Specific route to send on, only used in the case of loops or serial number is used
			 * When sending to a device, the route is ignored and the porter connection takes care
			 * of sending the command on the device's assigned route. */
			String routeID = req.getParameter("routeID");
			if( routeID != null)
				localBean.setRouteID(Integer.valueOf(routeID).intValue());

			//HANDLE LP command - special case for "getvalue lp channel" command
			if ( command.toLowerCase().startsWith("getvalue lp channel"))
			{
				DeviceLoadProfile dlp = new DeviceLoadProfile();
				try
				{
					dlp.setDeviceID(new Integer(localBean.getLiteYukonPao().getLiteID()));
					Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, dlp);
					dlp = (DeviceLoadProfile)t.execute();
				}
				catch(Exception e)
				{
					CTILogger.error(e.getMessage(), e);
				}

				Integer rate = null;
				//get the load profile channel and it's interval
				if (command.indexOf("channel 1") > 0)	// kw lp
					rate = dlp.getLoadProfileDemandRate();
				else if (command.indexOf("channel 4") > 0)	//voltage lp
					rate = dlp.getVoltageDmdRate();

				if( rate != null)
				{
                    if( command.length() < 40) {    //If the length is greater than 40 then we have a stop time also so we should not submit 2 commands.
    					String execCommand = command.substring(0, command.length() - 16).trim();
    					String cmdDatePart = command.substring(command.length() - 16);	//16 for "mm/dd/yyyy HH:mm" date length
    					Date queryDate = ServletUtil.parseDateStringLiberally(cmdDatePart);
    					if( queryDate != null)
    					{
    						GregorianCalendar cal = new GregorianCalendar();
    						cal.setTime(queryDate);
    						cal.add(Calendar.SECOND, (rate.intValue() * 6));
    						
    						SimpleDateFormat lpFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    						//Add on another command to send a second command for lp interval data.
    						command += " & " + execCommand + " " + lpFormat.format(cal.getTime()); 
    					}
                    }
				}
			}
			else if (command.toLowerCase().startsWith("putconfig tou"))
			{
				String schedID = req.getParameter("scheduleID");
				if( schedID != null)
					command = localBean.buildTOUScheduleCommand(Integer.valueOf(schedID).intValue());
			}
			//Set our yc bean command string by using a function lookup if null
			if( command == null )
			{
				/** If we have no command string, see if there is a function and find it's
				 * corresponding command string.  (Will substitute based on key=value
				 * pairs found in the commands file directory for a particular device. */ 
				if( function != null)
					command = localBean.getCommandFromLabel(function);
				else
					//WE HAVE NO COMMAND?
					command = "";
			}

			if( command.length() > 0 ) {
				
			    try {
					/** Time to wait for return to calling jsp
					 * Timeout is used to <hope to> assure there is some resultText to display when we do go back. */
					String timeOut = req.getParameter("timeOut");
					if( timeOut == null)
						timeOut = "8000";	// 8 secs default
					if( timeOut != null && command != null)	//adjust the timeout for multiple command strings, separated by '&'
					{
						int commandCount = 1;
						for (int i = 0; i < command.length(); i++)
						{
							if( command.charAt(i) == '&')
								commandCount++;
						}
						timeOut = String.valueOf(commandCount * Integer.valueOf(timeOut).intValue());
					}
			
					localBean.setTimeOut(new Integer(timeOut).intValue());
                        localBean.setCommandString(command);
					localBean.clearErrorMsg();
                    
					localBean.executeCommand();
		
					/** Don't return to the jsp until we have the message or we've timed out.*/
					while( (localBean.getRequestMessageIDs_Executing().size() > 0 && localBean.isWatchRunning()))
					{
						try
						{
							Thread.sleep(5000);
						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					CTILogger.debug("ExecutingMessageIDs:" + localBean.getRequestMessageIDs_Executing().size() + " | Watching:" + localBean.isWatchRunning());

					if ( command.toLowerCase().startsWith("getvalue lp channel"))
					{
					    localBean.loadRPHData();
					}

                } catch (PaoAuthorizationException e) {
                    localBean.setErrorMsg("You do not have permission to execute command: " + e.getPermission());
			    }
			}
			
		}
		
		if( redirectURL != null ) {
			resp.sendRedirect(redirectURL);
		}
		else {
			resp.sendRedirect(req.getContextPath() + "/operator/Operations.jsp");
		}
	}
}