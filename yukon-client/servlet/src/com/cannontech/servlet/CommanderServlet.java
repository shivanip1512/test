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
*/

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.yc.gui.YC;

public class CommanderServlet extends javax.servlet.http.HttpServlet 
{
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
	{	
		final HttpSession session = req.getSession( false );

		//I'm sure I need this for something but not sure what yet.  SN 12/18/03
		LiteYukonUser user = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);

		/**Create a YC bean for the session if one does not already exist.*/
		YC localBean = (YC)session.getAttribute(ServletUtil.ATT_YC_BEAN);
		if(localBean == null)
		{
			session.setAttribute(ServletUtil.ATT_YC_BEAN, new YC());
			localBean = (YC)session.getAttribute(ServletUtil.ATT_YC_BEAN);
		}
		

		/**Debug print of all parameter names.*/
/*		java.util.Enumeration enum1 = req.getParameterNames();
		  while (enum1.hasMoreElements()) {
		  	String ele = enum1.nextElement().toString();
			 System.out.println(" --" + ele);
		 }
*/		
		/**deviceID(opt1) or SerialNumber(opt2) must exist!
		 * deviceID/serialNumber command is sent. */
		String deviceID = req.getParameter("deviceID");
		String serialNumber = req.getParameter("serialNumber");
		
		/** Specific route to send on, only used in the case of loops or serial number is used
		 * When sending to a device, the route is ignored and the porter connection takes care
		 * of sending the command on the device's assigned route. */
		String routeID = req.getParameter("routeID");
		//Flag to clear the resultText, no commands sent
		String clear = req.getParameter("clearText");

		if (clear != null)
			localBean.clearResultText();
		else
		{					
			String function = req.getParameter("function");	//user friendly command string
			String command = req.getParameter("command");	//system command string
			/** Time to wait for return to calling jsp
			 * Wait is used to <hope to> assure there is some resultText to display when we do go back. */ 
			String timeOut = req.getParameter("timeOut");
			if( timeOut == null)
				timeOut = "8000";	// 8 secs default
	
			localBean.setTimeOut(new Integer(timeOut).intValue());
			
			//Set our yc bean command string
			if( command != null)
				localBean.setCommandString(command);
			if( command == null )
			{
				/** If we have no command string, see if there is a function and find it's
				 * corresponding command string.  (Will substitute based on key=value
				 * pairs found in the commands file directory for a particular device. */ 
				if( function != null)
				{
					command = localBean.getCommandFromLabel(function);
				}
				else
				{
					//QUIT - WE HAVE NO COMMAND?
				}
			}
			//Send the command out on deviceID/serialNumber
			if( deviceID != null )
			{	
				localBean.setDeviceID(Integer.parseInt(deviceID));
				localBean.handleDevice();
			}
			else if( serialNumber != null)
			{
				localBean.setSerialNumber(serialNumber);
				localBean.handleSerialNumber();
			}

			/** Don't return to the jsp until we have the message or we've timed out.*/
			while( localBean.getRequestMessageIDs().size() > 0 && localBean.isWatchRunning())
			{
				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		String redirectURL = req.getParameter("REDIRECT");
		
		if( redirectURL != null ) {
			resp.sendRedirect(redirectURL);
		}
		else {
			resp.sendRedirect(req.getContextPath() + "/operator/Operations.jsp");
		}
	}
}
