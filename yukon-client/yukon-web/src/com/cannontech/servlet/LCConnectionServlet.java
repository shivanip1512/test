package com.cannontech.servlet;

/**
 * Maintains a connection to a LC Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.gui.manualentry.ResponseProg;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.roles.loadcontrol.DirectLoadcontrolRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.loadcontrol.ILCCmds;
import com.cannontech.web.loadcontrol.LMCmdMsgFactory;
import com.cannontech.web.loadcontrol.LMSession;
import com.cannontech.web.loadcontrol.LoadcontrolCache;
import com.cannontech.web.loadcontrol.WebCmdMsg;
import com.cannontech.web.util.ParamUtil;

public class LCConnectionServlet extends ErrorAwareInitializingServlet implements java.util.Observer {
		
	// Key used to store instances of this in the servlet context
	public static final String SERVLET_CONTEXT_ID = "LCConnection";

	private LoadControlClientConnection clientConnection = null;
	private com.cannontech.web.loadcontrol.LoadcontrolCache cache = null;
    
    private DateFormattingService dateFormattingService = null;
    private AuthDao authDao = null;

/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 11:36:13 AM)
 */
public void destroy() 
{
	// Pull this out of the servlet context
	getServletContext().removeAttribute(SERVLET_CONTEXT_ID);
	
	if( clientConnection != null )
    {
    	clientConnection.disconnect();
    }
	
	super.destroy();
}
/**
 * Creation date: (6/25/2001 1:04:28 PM)
 * @return com.cannontech.web.loadcontrol.LoadcontrolCache
 */
public LoadcontrolCache getCache() {
	return cache;
}
/**
 * Creation date: (3/21/2001 1:41:38 PM)
 * @return com.cannontech.macs.LClientConnection
 */
public LoadControlClientConnection getConnection() {
	return clientConnection;
}
/**
 * Makes a connection to Loadcontrol and stores a reference to this in
 * the servlet context.
 * Creation date: (3/21/2001 11:35:57 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void doInit(ServletConfig config) throws ServletException {

	dateFormattingService = YukonSpringHook.getBean("dateFormattingService", DateFormattingService.class);
    authDao = YukonSpringHook.getBean("authDao", AuthDao.class);
	clientConnection = YukonSpringHook.getBean("loadControlClientConnection", LoadControlClientConnection.class);

	clientConnection.addObserver(this);
	
	// Create a load control cache
	cache = new LoadcontrolCache(clientConnection);
	clientConnection.addObserver(cache);

	// Add this to the context so other servlets can access the connection
	getServletContext().setAttribute(SERVLET_CONTEXT_ID, this);		
}

public void update(java.util.Observable obs, Object o) {}

/**
 * Allows commands to be executed through a URL interface.
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * 
 * Expected session params:
 * redirectURL - Where do take the user after the post to the servlet
 * 
 * cmd - the string representation of the command selected
 * 
 * itemid - the ID of the item that the command will affect
 * 
 */
public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{
	String redirectURL = req.getParameter("redirectURL");
	Map<String, Object> optionalProps = new Hashtable<String, Object>(10);

	//handle any commands that we may need to send to the server from any page here
	String cmd = req.getParameter("cmd");
	String itemid = req.getParameter("itemid");
	String resendSyncMsgs = req.getParameter("resendSyncMsgs");
    String adjustments = ParamUtil.getString(req, "adjustments", null);
    String cancelPrev = ParamUtil.getString(req, "cancelPrev", null);
    String currentUserID = req.getParameter("currentUserID");

	//add any optional properties here
    optionalProps = getOptionalParams( req );
	ResponseProg[] violatResp = null;
    boolean allowStopGear = currentUserID != null && authDao.checkRoleProperty(Integer.parseInt(currentUserID), DirectLoadcontrolRole.ALLOW_STOP_GEAR_ACCESS);
    if(allowStopGear && ILCCmds.PROG_STOP.equals(cmd) ) {
        optionalProps.put("allowStopGear", "true");
    }
    
	if( cmd != null )
	{
		try
		{
            //if we had target cycle adjustments - save them
            if (cancelPrev != null)
            {
                adjustments = null;
            }
            if (adjustments != null)
            {
                setAdditionalInfoForProgram(adjustments, itemid);
            }
            
            WebCmdMsg msg = LMCmdMsgFactory.createCmdMsg( 
					cmd, new Integer(itemid), (Hashtable<String, Object>) optionalProps, getCache() );

			CTILogger.info("LM_COMMAND: " + req.getServletPath() +
				"	cmd = " + cmd +
				", itemID = " + itemid +
				", OptionalProp Cnt = " + optionalProps.size() );

			//send the LMCommand to the LoadControl server
			if( msg.genLCCmdMsg() != null )
			{
				if( LMCmdMsgFactory.isSyncMsg(cmd) || 
                        (optionalProps.get("stopgearnum") != null && LMCmdMsgFactory.isSyncMsg(msg.getCmd())))
				{					
					violatResp = sendSyncMsg( msg );
					CTILogger.info("   Synchronous command was sent and responded to " + 
						"(ResponseCount= " + (violatResp==null ? 0 : violatResp.length) + ")" );
				}				
				else
				{
					getConnection().write( msg.genLCCmdMsg() );
					CTILogger.info("   Command was sent");
				}
			}
			else
				CTILogger.info("   Command was not sent since it did not have a message defined for it");
		}
		catch( Exception e )
		{
			CTILogger.warn( "LC Command was attempted but failed for the following reason:", e );
		}
	}
	else if( resendSyncMsgs != null )
	{
		//String adjs = getAddtionalInfo(optionalProps, itemid);
        //optionalProps.put("adjustments", adjs);
        resendSyncMsgs( req, (Double[])optionalProps.get("dblarray1") );
	}
	else
		CTILogger.warn( "LC Command servlet was hit, but NO command was sent" );

	//sets any responses we got back from the server
	getLMSession(req).setResponseProgs( violatResp );

	//always forward the client to the specified URL
	if( redirectURL != null ) {
	    redirectURL = ServletUtil.createSafeRedirectUrl(req, redirectURL);
        resp.sendRedirect( resp.encodeRedirectURL(req.getContextPath() + redirectURL) );
    }
}
private void setAdditionalInfoForProgram(String additionalInfo, String programID) 
{
    LMProgramDirect prg = (LMProgramDirect)getCache().getProgram(new Integer (programID) );
    String baseString = "adjustments";

    if (additionalInfo != null) 
    {
        
        if ((additionalInfo.length() > baseString.length() ) && additionalInfo.startsWith(baseString))
        {
            prg.setAddtionalInfo( additionalInfo );
            
        }
    }
}

private void resendSyncMsgs( HttpServletRequest req, Double[] progIds )
{
	HttpSession session = req.getSession( false );
	if( session.getAttribute("lmSession") != null )
	{
		LMSession lmSess = (LMSession)session.getAttribute("lmSession");
        String buttonPressed = req.getParameter("submitChoice");

		try
		{
			if( !"Resubmit".equalsIgnoreCase(buttonPressed) )
				return;

			//ResponseProg[] resProgArr = new ResponseProg[ progIds.length ];
			
			/* Oh well, i j loop should contain a low number of data in each */
			for( int i = 0; i < lmSess.getResponseProgs().length; i++ ) {

                ResponseProg resProgArr = lmSess.getResponseProgs()[i];

                for( int j = 0; progIds != null && j < progIds.length; j++ ) {
                    int progID = progIds[j].intValue();
                    if( progID == resProgArr.getLmProgramBase().getYukonID().intValue() )
                        resProgArr.setOverride( Boolean.TRUE );
                }
                
                resProgArr.getLmRequest().setConstraintFlag(
                        resProgArr.getOverride().booleanValue()
                        ? LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE
                        : LMManualControlRequest.CONSTRAINTS_FLAG_USE );                
			}
	
			if( lmSess.getResponseProgs().length > 0 )	
				LCUtils.executeSyncMessage( lmSess.getResponseProgs() );
		}
		finally
		{
			lmSess.clearSyncMessages();
		}

	}
	else
		throw new IllegalStateException("Unable to find the LMSession object for resending synchronous messages");
}

/**
 * Processes a given WebCmd message for synchrounous messaging. The request and
 * response is handled inside this method. Our thread is locked in here until
 * all responses have returned OR a timeout is encountered. Returns null
 * if all messages where successfull.
 *  
 * @param cmdMsg
 * @return ResponseProg[] the message responses 
 */
private ResponseProg[] sendSyncMsg( final WebCmdMsg cmdMsg )
{
	Multi multMsg = new Multi();
	if( cmdMsg.genLCCmdMsg() instanceof Multi )
		multMsg = (Multi)cmdMsg.genLCCmdMsg();
	else
		multMsg.getVector().add( cmdMsg.genLCCmdMsg() ); //a multi of 1


	LMManualControlRequest[] lmReqs =
		new LMManualControlRequest[ multMsg.getVector().size() ];

	ResponseProg[] programResps =
		new ResponseProg[ multMsg.getVector().size() ];

    //if we have at least 1 check constraint set, then lets
    // always show a result box
    boolean isCheckConstraints = false;

    for( int i = 0; i < multMsg.getVector().size(); i++ )
	{
		lmReqs[i] = (LMManualControlRequest)multMsg.getVector().get(i);


		//better be a program for this Request Message!
		LMProgramBase progBase =
			getCache().getProgram( new Integer(lmReqs[i].getYukonID()) );

		//may or may not be an error, just warn for now
		if( progBase == null )
			CTILogger.warn( " ** A LMManualControlRequest message was sent without a defined LMProgramBase object");

        isCheckConstraints |=
            lmReqs[i].getConstraintFlag() ==
                LMManualControlRequest.CONSTRAINTS_FLAG_CHECK;
        handleTargetCycleAjustments(lmReqs[i], progBase);
        programResps[i] = new ResponseProg( lmReqs[i], progBase );
	}

				
	boolean success = LCUtils.executeSyncMessage( programResps );
	
	if( !success || isCheckConstraints  ) {
        
        //add a "Successful check" output if there are not
        // any constraints violated
        if( isCheckConstraints ) {
            for( int i = 0; i < programResps.length; i++ ) {

                if( programResps[i].getViolations().size() <= 0 ) {
                    programResps[i].setAction(
                        ResponseProg.NO_VIOLATION_ACTION );

                    programResps[i].setNoViolationsMessage(
                        " No Constraints Violated");
                }
            }                       
        }
        
	    return programResps;
    }
    else
        return null;
}

private void handleTargetCycleAjustments(LMManualControlRequest lmReq, LMProgramBase progBase) {
    if (progBase instanceof LMProgramDirect) 
    {
        String additionalInfo = ((LMProgramDirect)progBase).getAddtionalInfo();
        if (additionalInfo != null)
            lmReq.setAddditionalInfo(additionalInfo);
    }
}

/**
 * Gets the LMSession from this user request
 * Never returns NULL
 * @param req
 */
private LMSession getLMSession( HttpServletRequest req )
{
	HttpSession httpSession = req.getSession( false );
	LMSession lmSession = (LMSession)httpSession.getAttribute("lmSession");
	
	if( lmSession == null )
	{
		CTILogger.warn( " Unable to find LMSession, creating a new one");
		
		//we must create an LMSession if we do not have one
		lmSession = new LMSession();
		httpSession.setAttribute( "lmSession", lmSession );
	}
	
		
	return lmSession;
}

private Map<String, Object> getOptionalParams( HttpServletRequest req )
{
	Map<String, Object> optionalProps = new Hashtable<String, Object>(10);
	
	if( req.getParameter("duration") != null )
		optionalProps.put( "duration",
			CtiUtilities.getIntervalSecondsValue(req.getParameter("duration")) );

	if( req.getParameter("constraint") != null )
		optionalProps.put( "constraint", req.getParameter("constraint") );
	
	if( req.getParameter("gearnum") != null )
		optionalProps.put( "gearnum", new Integer(req.getParameter("gearnum")) );

    YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(req);
    Calendar gcStart = dateFormattingService.getCalendar(userContext);

	if( req.getParameter("startbutton") != null
		 && req.getParameter("startbutton").equals("startat") )
	{
		
		int secs = CtiUtilities.decodeStringToSeconds( req.getParameter("startTime1") );
		
		try {
		    gcStart.setTime( dateFormattingService.flexibleDateParser(req.getParameter("startdate"), userContext) );
        } catch (ParseException e) {
            throw new RuntimeException("The start date is not a valid date.", e);
        }
		gcStart.set( GregorianCalendar.HOUR, 0 );
		gcStart.set( GregorianCalendar.MINUTE, 0 );
		gcStart.set( GregorianCalendar.SECOND, secs );
		optionalProps.put( "startdate", gcStart.getTime() );
	} else if ((req.getParameter("cmd") != null) && (req.getParameter("cmd").equalsIgnoreCase(ILCCmds.AREA_DAILY_CHG))) {

	    int secs = CtiUtilities.decodeStringToSeconds( req.getParameter("startTime1") );

        Calendar tempCal = Calendar.getInstance();
        
        TimeZone userTimeZone = userContext.getTimeZone();
        tempCal.set( GregorianCalendar.HOUR, 0 );
        tempCal.set( GregorianCalendar.MINUTE, 0 );
        tempCal.set( GregorianCalendar.SECOND, 0 );
        tempCal.setTimeZone(userTimeZone);
        
        String dateFormat = dateFormattingService.format(tempCal.getTime(), DateFormatEnum.DATE, userContext);

        try {
            gcStart.setTime( dateFormattingService.flexibleDateParser(dateFormat, userContext));
        } catch (ParseException e) {
        	throw new RuntimeException("The start date is not a valid date.", e);
        }
        
        gcStart.set( GregorianCalendar.HOUR, 0 );
        gcStart.set( GregorianCalendar.MINUTE, 0 );
        gcStart.set( GregorianCalendar.SECOND, secs );
        
        optionalProps.put( "startdate", gcStart.getTime() );
        
	} else 	{
		//assume they want to start now
        gcStart = CtiUtilities.get1990GregCalendar();
		optionalProps.put( "startdate", gcStart.getTime() );
	}

    Calendar gcStop = dateFormattingService.getCalendar(userContext);
    
	if( req.getParameter("stopbutton") != null
		 && req.getParameter("stopbutton").equals("stopat") )
	{
		int secs = CtiUtilities.decodeStringToSeconds( req.getParameter("stopTime1") );
		
        try {
            Date stopDate = dateFormattingService.flexibleDateParser(req.getParameter("stopdate"), userContext);
            gcStop.setTime( stopDate );
        } catch (ParseException e) {
        	throw new RuntimeException("The stop date is not a valid date.", e);
        }
		gcStop.set( GregorianCalendar.HOUR, 0 );
		gcStop.set( GregorianCalendar.MINUTE, 0 );
		gcStop.set( GregorianCalendar.SECOND, secs );

		optionalProps.put( "stopdate", gcStop.getTime() );
	}
	else if( req.getParameter("stopbutton") != null
				 && req.getParameter("stopbutton").equals("stopnow") )
	{
		//assume they want to stop now
        gcStop = CtiUtilities.get1990GregCalendar();
		optionalProps.put( "stopdate", gcStop.getTime() );
	} else if ((req.getParameter("cmd") != null) && (req.getParameter("cmd").equalsIgnoreCase(ILCCmds.AREA_DAILY_CHG))) {
        int secs = CtiUtilities.decodeStringToSeconds( req.getParameter("stopTime1") );

        Calendar tempCal = Calendar.getInstance();
        
        TimeZone userTimeZone = userContext.getTimeZone();
        tempCal.set( GregorianCalendar.HOUR, 0 );
        tempCal.set( GregorianCalendar.MINUTE, 0 );
        tempCal.set( GregorianCalendar.SECOND, 0 );
        tempCal.setTimeZone(userTimeZone);
	        
        String dateFormat = dateFormattingService.format(tempCal.getTime(), DateFormatEnum.DATE, userContext);

        try {
            gcStop.setTime( dateFormattingService.flexibleDateParser(dateFormat, userContext));
	    } catch (ParseException e) {
	    	throw new RuntimeException("The stop date is not a valid date.", e);
	    }
	        
	    gcStop.set( GregorianCalendar.HOUR, 0 );
	    gcStop.set( GregorianCalendar.MINUTE, 0 );
	    gcStop.set( GregorianCalendar.SECOND, secs );
	        
	    optionalProps.put( "stopdate", gcStop.getTime() );

	} else {
		//set the stop time to 1 year from now if no stop selected
		Calendar c = dateFormattingService.getCalendar(userContext);
		c.add( Calendar.YEAR, 1 );
		optionalProps.put( "stopdate", c.getTime() );
	}


	if( req.getParameterValues("dblarray1") != null )
	{
		Double[] threshArr = new Double[req.getParameterValues("dblarray1").length];
		for( int i = 0; i < req.getParameterValues("dblarray1").length; i++ )
			threshArr[i] = new Double( req.getParameterValues("dblarray1")[i] );
		
		optionalProps.put( "dblarray1", threshArr );
	}

	if( req.getParameter("dblarray2") != null )
	{
		Double[] restArr = new Double[req.getParameterValues("dblarray2").length];
		for( int i = 0; i < req.getParameterValues("dblarray2").length; i++ )
			restArr[i] = new Double( req.getParameterValues("dblarray2")[i] );
		
		optionalProps.put( "dblarray2", restArr );
	}
	
	if( req.getParameter("cyclepercent") != null )
		optionalProps.put( "cyclepercent", new Integer(req.getParameter("cyclepercent")) );

	if( req.getParameter("periodcnt") != null )
		optionalProps.put( "periodcnt", new Integer(req.getParameter("periodcnt")) );
	
	
	if( req.getParameter("startTime1") != null )
	{
        if( req.getParameter("startTime1").length() <= 0 ) {
            optionalProps.put("starttime", new Integer(LMControlArea.INVALID_INT));
        } else {
            optionalProps.put("starttime",
                              new Integer( CtiUtilities.decodeStringToSeconds( req.getParameter("startTime1") )) );
        }
	}
	
	
	if( req.getParameter("stopTime1") != null )
	{
        if( req.getParameter("stopTime1").length() <= 0 ) {
		    optionalProps.put( "stoptime", new Integer(LMControlArea.INVALID_INT) );
        } else {
		    optionalProps.put( "stoptime",
		                       new Integer( CtiUtilities.decodeStringToSeconds( req.getParameter("stopTime1") )) );
	    }
	}
    
    if( req.getParameter("stopGearNum") != null) {
        optionalProps.put( "stopgearnum", req.getParameter("stopGearNum") );
    }
	
	return optionalProps;
}

}