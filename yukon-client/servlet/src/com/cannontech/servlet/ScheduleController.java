package com.cannontech.servlet;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.IMACSConnection;

/**
 * parameters:
 * ID - The id of the schedule of interest 
 * ACTION - "START" | "STOP" | "STARTSTOP"
 * STARTAT - Time to start (must be adjusted to time zone??), 0 for now, -1 for don't start
 * STOPAT - Time to stop, 0 for now, -1 for don't stop
 * URL - Where to go after processing action
 *
 * In addition the current session must have a
 * com.cannontech.database.data.user.WebUser instance store as "USER"
 *
 * Example URL:
 * ScheduleController?ID=1&STARTAT=0&STOPAT=0&URL=ScheduleSummary.jsp
 * Would start program id 1 now.
 *
 * Creation date: (5/1/00 11:58:27 AM)
 * @author: Aaron Lauinger
 */

public class ScheduleController extends javax.servlet.http.HttpServlet {

	public static final int PROGRAM_VIEW_ACCESS = 0x0001;
	public static final int PROGRAM_CONTROL_ACCESS = 0x0002;

	// hackaround to set a schedule state until MACS replies
	private static final String PSEUDO_SCHEDULE_STATE = "Request Pending";
	
	private static java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");

	private static com.cannontech.message.dispatch.ClientConnection vangoghConn = null;

	private int soeTag = 0;

/**
 * ProgramController constructor comment.
 */
public ScheduleController() {
	super();
}

/**
 * Finds a schedule by id.
 * The linear search time isn't so good here.
 *
 * Creation date: (3/19/2001 3:52:13 PM)
 * @return com.cannontech.macs.Schedule
 * @param id long
 */
private com.cannontech.message.macs.message.Schedule findSchedule(long id) {
	com.cannontech.message.macs.message.Schedule retVal = null;

	ConnServlet connContainer = (ConnServlet)
		getServletContext().getAttribute(ConnServlet.SERVLETS_CONTEXT_ID);

	if( connContainer != null )
	{
	    //com.cannontech.macs.MACSClientConnection conn = connContainer.getConnection();
	    com.cannontech.message.macs.message.Schedule[] schedules = 
	    		connContainer.getIMACSConnection().retrieveSchedules();

	    if( schedules != null )
	    {
		    for( int i = schedules.length-1; i >= 0; i-- )
		    {
			    if( schedules[i].getId() == id )
			    {
				    retVal = schedules[i];
				    break;
			    }
		    }
	    }
	}
			
	return retVal;
}

/**
 * Insert the method's description here.
 * Creation date: (3/20/2001 7:08:53 PM)
 * @return java.util.Date
 * @param time java.lang.String
 */
private java.util.Date parseTime(String time) 
{
	// 0 == now
	if( time.equals("0") )
	{
		return new java.util.Date();
	}
	else // -1 never
	if( time.equals("-1") )
	{
		return null;
	}
	
	java.util.Date today = com.cannontech.util.ServletUtil.getToday();	
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	cal.setTime(today);

	try
	{
		java.util.StringTokenizer tok = new java.util.StringTokenizer(time,":");

		if( tok.hasMoreTokens() )
		{
			String s = tok.nextToken();				
			int hour = Integer.parseInt(s);
			cal.set( java.util.Calendar.HOUR_OF_DAY, hour );
		}
			
		if( tok.hasMoreTokens() )
		{
			String s = tok.nextToken();				
			int minute = Integer.parseInt(s);
			cal.set( java.util.Calendar.MINUTE, minute );
		}
			
		if( tok.hasMoreTokens() )
		{
			String s = tok.nextToken();				
			int second = Integer.parseInt(s);
			cal.set( java.util.Calendar.SECOND, second );			
		}

		return cal.getTime();
	}
	catch( Throwable t )
	{
	}

	return null;
}
/**
 * service method comment.
 */
public void service(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException 
{	
	javax.servlet.http.HttpSession session = req.getSession(false);

	if( session == null )
		return; //handle error?
			
	String scheduleIDStr = req.getParameter("ID");
	String action = req.getParameter("ACTION");
	String startAtStr = req.getParameter("STARTAT");
	String stopAtStr = req.getParameter("STOPAT");
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
	
	java.util.Date startDate = parseTime(startAtStr);
	java.util.Date stopDate  = parseTime(stopAtStr);

	
	com.cannontech.message.macs.message.Schedule sched = findSchedule(Integer.parseInt(scheduleIDStr));
	ConnServlet connContainer = (ConnServlet)
			getServletContext().getAttribute(ConnServlet.SERVLETS_CONTEXT_ID);
			
	if( sched != null && connContainer != null )
	{			
		IMACSConnection conn = connContainer.getIMACSConnection();
	
		if( (action.equalsIgnoreCase("start") || action.equalsIgnoreCase("startstop")) &&
			 startDate != null )
		{
			com.cannontech.message.macs.message.OverrideRequest startRequest = 
				new com.cannontech.message.macs.message.OverrideRequest();
			startRequest.setSchedId(sched.getId());
			startRequest.setAction(com.cannontech.message.macs.message.OverrideRequest.OVERRIDE_START);
			startRequest.setStart(startDate);
			conn.writeMsg(startRequest);

			setScheduleRequestPending(sched);
			CTILogger.info("Start schedule:  " + sched.getId() + " time:  " + startDate);
			
           
			/* Log this activity */
			ActivityLogger.logEvent(user.getUserID(), sched.getId(), ActivityLogActions.MANUAL_MACS_SCHEDULE_START_ACTION, "Manual control of MACS schedule requested, start: " + startRequest.getStart() + " stop: " + startRequest.getStop());            
		}		

		if( (action.equalsIgnoreCase("stop") || action.equalsIgnoreCase("startstop")) &&
			 stopDate != null )
		{			
			com.cannontech.message.macs.message.OverrideRequest stopRequest = 
		 		new com.cannontech.message.macs.message.OverrideRequest();
		 	stopRequest.setSchedId(sched.getId());
			stopRequest.setAction(com.cannontech.message.macs.message.OverrideRequest.OVERRIDE_STOP);
			stopRequest.setStop(stopDate);
			conn.writeMsg(stopRequest);

			setScheduleRequestPending(sched);
			CTILogger.info("Stop schedule:  " + sched.getId() + " time:  " );
			

			/* Log this activity */
			ActivityLogger.logEvent(user.getUserID(), sched.getId(), ActivityLogActions.MANUAL_MACS_SCHEDULE_STOP_ACTION, "Manual control of MACS schedule requested, stop: " + stopRequest.getStop());
		}
	}
	else
	{
		CTILogger.error("Received request for unknown schedule, id: " + scheduleIDStr);
		//do handle error
	}

	try
	{
		String nextUrl = req.getParameter("URL");
		nextUrl = java.net.URLDecoder.decode(nextUrl, "UTF-8");
		nextUrl = resp.encodeRedirectURL(nextUrl);
		resp.sendRedirect(nextUrl);
	}
	catch(Exception e )
	{
		CTILogger.error("exception", e);
	}
	
}
/**
 * Sets a schedule in a psuedo start pending state.
 * This is used in ther interim period between
 * a start request being sent to MACS and a response
 * with the new schedule state being received.
 * Creation date: (3/26/2001 7:19:50 PM)
 * @param id long
 */
private void setScheduleRequestPending(com.cannontech.message.macs.message.Schedule sched) 
{
	sched.setCurrentState(PSEUDO_SCHEDULE_STATE);
}
}
