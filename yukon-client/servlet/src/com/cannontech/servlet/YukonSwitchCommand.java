package com.cannontech.servlet;

/**
 * Creation date: (4/27/00 5:11:58 PM)
 
PARAMETERS
-------------------------------------------
 groupid			- id of the versacom serial group
 serialNumber 		- serial number of the meter
 function			- what should be done
 restoreRelayNumber	- what relay
 stopRelayNumber
 shedRelayNumber
 startRelayNumber
 percent			- percent of cycle rate
 period				- cycle period
 periodCount		- number of periods
 time				- SHED time
 nextURL			- where the servlet should go after the post
 
*/

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.common.util.LogWriter;
import com.cannontech.database.data.web.Operator;
import com.cannontech.database.data.web.User;

public class YukonSwitchCommand extends javax.servlet.http.HttpServlet 
{
	private static com.cannontech.common.util.LogWriter logger = null;

	// increment this for every message
	private long userMessageIDCounter = 1;
/**
 * SwitchCommand constructor comment.
 */
public YukonSwitchCommand() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (12/7/99 9:54:51 AM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 */
public void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{	
	HttpSession session = req.getSession( false );

	// does not have a session, make them log in
	if( session == null )
	{
		String nextUrl = "/login.jsp?failed=true";
		nextUrl = resp.encodeRedirectURL(nextUrl);
		resp.sendRedirect(nextUrl);
	}

	String groupID = req.getParameter("groupid");
	String serialNumber = req.getParameter("serialNumber");
	String function = req.getParameter("function");

	// could be invoked by either operator or user...
	Operator operator = (Operator) session.getValue("OPERATOR");
	User user = (User) session.getValue("USER");
	boolean failed = false;

	StringBuffer command = new StringBuffer();
	StringBuffer target = new StringBuffer();

	//Build command
	if( function.equals("SHED Time") )
	{
		command.append("control shed ");
		command.append( parseCommandTime(req.getParameter("time")) );
	 	command.append(" relay ");
	 	command.append( req.getParameter("shedRelayNumber") ); 
	}
	else if( function.equals("Restore") )
	{
		command.append("control restore relay ");
		command.append(req.getParameter("restoreRelayNumber") );
	}
	else if( function.equals("Cycle Rate") )
	{
		command.append(" control cycle ");
		command.append(req.getParameter("percent"));
		command.append(" period ");
		command.append(req.getParameter("period"));
		command.append(" count ");
		command.append(req.getParameter("periodCount"));
		command.append(" relay ");
		command.append(req.getParameter("startRelayNumber"));	
	}
	else if( function.equals("Stop Cycle") )
	{
		command.append("control cycle terminate relay ");
		command.append(req.getParameter("stopRelayNumber"));		
	}
	else if( function.equals("Service Enable") )
	{
		command.append("putconfig service in");
	}
	else if( function.equals("Service Disable") )
	{
		command.append("putconfig service out");		
	}
	else if( function.equals("Cap Control Open") )
	{
		command.append("control open");		
	}
	else if( function.equals("Cap Control Closed") )
	{
		command.append("control close");		
	}
	else  // unknown function
	{
		failed = true;
		logger.log("Unknown function call '" + function + "' in doPOST in " + this.getClass(), LogWriter.ERROR );
	}
	
	//Build target - either serial number or group id
	if( !groupID.trim().equalsIgnoreCase("0") ) {
		//prefer going out by groupid
		command.append(" select id ");
		command.append(groupID);	
	}
	else
	if( !serialNumber.trim().equalsIgnoreCase("0") ) {
		command.append(" serial ");
		command.append(serialNumber);
	}
	else {
		failed = true;
		logger.log("Must specify serialNumber or groupid to use", LogWriter.ERROR );
	}
	
	sendCommand(command.toString());

	if( operator != null )
		logger.log( operator.getOperatorLogin().getUsername() + " - " + command.toString(), LogWriter.INFO );

	if( user != null )
		logger.log( user.getUsername() + " - " + command.toString(), LogWriter.INFO );	

	String redirectURL = req.getParameter("nextURL");
	
	if( redirectURL != null ) {
		resp.sendRedirect(redirectURL + "?sn=" + serialNumber);
	}
	else {
		resp.sendRedirect("/operator/switch_commands.jsp?sn=" + serialNumber );
	}
}
/**
 * Was used by older versoin.
 * Creation date: (5/2/00 2:11:31 PM)
 * Version: <version>
 * @return int
 * @param text java.lang.String
 */
private int getIntValue(String text ) 
{
	String temp = new String( text );
	String finalValue = new String();
		
	if( temp.length() > 3 )
		temp = temp.substring( 0, 3 );

	String percentValue = new String();
	int value = 0;
	
	for( int i = 0; i < temp.length(); i++ )
		if( Character.isDigit( temp.charAt(i) ) )		
			finalValue += temp.charAt(i);

	try
	{
		value = Integer.valueOf( finalValue ).intValue();
	}
	catch( NumberFormatException ex )
	{
		logger.log("Unable to convert an integer entered by user", LogWriter.INFO );
		return -1; //error	
	}

	return value;
}
/**
 * Inits a logger.
 * Creation date: (12/7/99 9:55:11 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException 
{
	super.init(config);
		
	synchronized (this)
	{		
		try
		{
			java.io.FileOutputStream out = new java.io.FileOutputStream("switch.log", true);
			java.io.PrintWriter writer = new java.io.PrintWriter(out, true);
			logger = new com.cannontech.common.util.LogWriter("SwitchCommand", com.cannontech.common.util.LogWriter.DEBUG, writer);
			logger.log("Starting up....", LogWriter.INFO );
		}
		catch( java.io.FileNotFoundException e )
		{
			e.printStackTrace();
		}
	}
}
/**
 * Returns the new time string, basically maps
 * what is seen on the jsp page into the format the command
 * can take.
 * Creation date: (3/22/2001 1:59:11 PM)
 * @return java.lang.String
 * @param time java.lang.String
 */
private String parseCommandTime(String time) {

	String retVal = null;
	
	try
	{
		java.util.StringTokenizer tok = new java.util.StringTokenizer(time);		
		int timePart = Integer.parseInt( (String) tok.nextToken() );
		String unitPart = (String) tok.nextToken();
		
		if( unitPart.equalsIgnoreCase("sec") )
		{
			unitPart = "s";
		}
		else
		if( unitPart.equalsIgnoreCase("min") )
		{
			unitPart = "m";
		}
		else
		if( unitPart.equalsIgnoreCase("hr") )
		{
			unitPart = "h";
		}
		else
		{
			unitPart = null;
		}

		if( unitPart != null )
		{
			retVal = timePart + unitPart;			
		}
		
	}
	catch(Throwable t )
	{
		retVal = null;
	}

	return retVal;	
}
/**
 * Sends a request to porter.
 * Creation date: (3/21/2001 3:02:43 PM)
 * @param command java.lang.String
 */
private void sendCommand(String command) 
{
	PILConnectionServlet connContainer = (PILConnectionServlet)
		getServletContext().getAttribute(PILConnectionServlet.SERVLET_CONTEXT_ID);

	if( connContainer != null )
	{
		com.cannontech.message.porter.ClientConnection conn = connContainer.getConnection();

		if( conn != null )
		{
			com.cannontech.message.porter.message.Request req = // no need for deviceid so send 0
				new com.cannontech.message.porter.message.Request( 0, command, userMessageIDCounter++ );

			conn.write(req);

			logger.log("Sent command to PIL:  " + command, LogWriter.INFO );
		}
		else
		{
			logger.log("Failed to retrieve a connection", LogWriter.ERROR );
		}
	}
	else
	{
		logger.log("Failed to retrieve PILConnectionServlet from servlet context",
					LogWriter.ERROR );
	}
}
}
