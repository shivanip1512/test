package com.cannontech.servlet;

/**
 * Maintains a connection to a LC Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * Creation date: (3/21/2001 11:31:54 AM)
 * @author: Aaron Lauinger
 */
import java.util.GregorianCalendar;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.gui.manualentry.ResponseProg;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.loadcontrol.LMCmdMsgFactory;
import com.cannontech.web.loadcontrol.LMSession;
import com.cannontech.web.loadcontrol.LoadcontrolCache;
import com.cannontech.web.loadcontrol.WebCmdMsg;

public class LCConnectionServlet extends javax.servlet.http.HttpServlet implements java.util.Observer {
		
	// Key used to store instances of this in the servlet context
	public static final String SERVLET_CONTEXT_ID = "LCConnection";

	private LoadControlClientConnection conn = null;
	private com.cannontech.web.loadcontrol.LoadcontrolCache cache = null;
	

/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 11:36:13 AM)
 */
public void destroy() 
{
	// Pull this out of the servlet context
	getServletContext().removeAttribute(SERVLET_CONTEXT_ID);
	
	try
	{
		if( conn != null )
		{
			conn.disconnect();
		}
	}
	catch( java.io.IOException ioe )
	{
		CTILogger.error("An exception occured disconnecting from load control");
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
	return conn;
}
/**
 * Makes a connection to Loadcontrol and stores a reference to this in
 * the servlet context.
 * Creation date: (3/21/2001 11:35:57 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException
{
	super.init(config);


	String lcHost = "127.0.0.1";
	int lcPort = 1920;

	try {
		lcHost = RoleFuncs.getGlobalPropertyValue( SystemRole.LOADCONTROL_MACHINE );

		lcPort = Integer.parseInt(
			RoleFuncs.getGlobalPropertyValue( SystemRole.LOADCONTROL_PORT ) );


	} catch (Exception e) {
		com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
	}

	CTILogger.info("Will attempt to connect to loadcontrol @" + lcHost + ":" + lcPort);
	conn = LoadControlClientConnection.getInstance();
	conn.addObserver(this);
	
	if( lcHost != null )
		conn.setHost(lcHost);

	if( lcPort != -1 )
		conn.setPort(lcPort);
		
	conn.setAutoReconnect(true);	
	conn.setTimeToReconnect(30);

	try
	{
		conn.connectWithoutWait();
	}
	catch( java.io.IOException io )
	{
		io.printStackTrace();
		CTILogger.error("An error occured connecting with load control", io);
	}

	// Create a load control cache
	cache = new LoadcontrolCache();
	conn.addObserver(cache);

	// Add this to the context so other servlets can access the connection
	getServletContext().setAttribute(SERVLET_CONTEXT_ID, this);		
	
	DBChangeListener dbl = new DBChangeListener() {
		ClientConnection dispatchConnection = null;
		public com.cannontech.message.util.ClientConnection getClientConnection() 
		{
			if (dispatchConnection == null) {

			String host;
			int port;

			try {
				host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );

				port = Integer.parseInt(
						RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) );

			} catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
				return null;
			}

			dispatchConnection = new com.cannontech.message.dispatch.ClientConnection();
			com.cannontech.message.dispatch.message.Registration reg =
				new com.cannontech.message.dispatch.message.Registration();
			reg.setAppName(
				"LCConnectionServlet @" + com.cannontech.common.util.CtiUtilities.getUserName());
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay(300); // 5 minutes should be OK

			//conn.addObserver(this);
			dispatchConnection.setHost(host);
			dispatchConnection.setPort(port);
			dispatchConnection.setAutoReconnect(true);
			dispatchConnection.setRegistrationMsg(reg);

			try {
				dispatchConnection.connectWithoutWait();
			} catch (Exception e) {
				com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
			}
		}
		
			return dispatchConnection;
		}
		public void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase lBase ) { }	
	};
	
	DefaultDatabaseCache.getInstance().addDBChangeListener(dbl);
}
/**
 * This is registered with the MACS Connection to receive updates
 * on the connection state.
 * Creation date: (3/21/2001 11:43:09 AM)
 * @param conn java.util.Observable
 * @param o java.lang.Object
 */
public void update(java.util.Observable obs, Object o) 
{
/*	if( obs == conn ) was spamming so commented out
	{		
		if( conn.isValid() )
			CTILogger.info("Connection established to " + conn.getHost() + ":" + conn.getPort());
		else
			CTILogger.info("Connection to " + conn.getHost() + ":" + conn.getPort() + " is down");
	}
	else
	{
		CTILogger.info("Warning!  received an update from an unknown observable!");
	}*/
}

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
	getLMSession(req).setRefreshRate(LMSession.REF_SECONDS_PEND);
	
	String redirectURL = req.getParameter("redirectURL");
	Hashtable optionalProps = new Hashtable(8);


	//handle any commands that we may need to send to the server from any page here
	String cmd = req.getParameter("cmd");
	String itemid = req.getParameter("itemid");
	String override = req.getParameter("override");
	
	//add any optional properties here
	optionalProps = getOptionalParams( req );

	ResponseProg[] violatResp = null;

	if( cmd != null )
	{
		try
		{
			WebCmdMsg msg = LMCmdMsgFactory.createCmdMsg( 
					cmd, new Integer(itemid), optionalProps, getCache() );


			CTILogger.info("LM_COMMAND: " + req.getServletPath() +
				"	cmd = " + cmd +
				", itemID = " + itemid +
				", OptionalProp Cnt = " + optionalProps.size() );

			//send the LMCommand to the LoadControl server
			if( msg.genLCCmdMsg() != null )
			{
				if( LMCmdMsgFactory.isSyncMsg(cmd) )
				{					
					violatResp = sendSyncMsg( msg );
					CTILogger.info("   Syncrhounous command was sent and responded to " + 
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
	else if( override != null )
	{
		resendSyncMsgs( req, (Double[])optionalProps.get("dblarray1") );
	}
	else
		CTILogger.warn( "LC Command servlet was hit, but NO command was sent" );
	

	//sets any responses we got back from the server
	getLMSession(req).setResponseProgs( violatResp );


	//always forward the client to the specified URL
	if( redirectURL != null )
		resp.sendRedirect( resp.encodeRedirectURL(req.getContextPath() + redirectURL) );
}

/**
 * Resends response messages withe the override flag set for the specified progIds.
 * 
 * @param req
 * @param progIds
 */
private void resendSyncMsgs( HttpServletRequest req, Double[] progIds )
{
	HttpSession session = req.getSession( false );
	if( session.getAttribute("lmSession") != null )
	{
		LMSession lmSess = (LMSession)session.getAttribute("lmSession");
		
		try
		{
			if( progIds == null )
				return;


			ResponseProg[] resProgArr = new ResponseProg[ progIds.length ];
			
			/* Oh well, i j loop should contain a low number of data in each */
			for( int i = 0; i < progIds.length; i++ )
			{
				int progID = progIds[i].intValue();
				for( int j = 0; j < lmSess.getResponseProgs().length; j++ )
				{
					if( progID == lmSess.getResponseProgs()[j].getLmProgramBase().getYukonID().intValue() )
					{
						resProgArr[i] = lmSess.getResponseProgs()[j];
						resProgArr[i].getLmRequest().setOverrideConstraints( true );
						break;
					}
				}
			}
	
			if( resProgArr.length > 0 )	
				LCUtils.executeSyncMessage( resProgArr );
		}
		finally
		{
			lmSess.clearSyncMessages();
		}

	}
	else
		throw new IllegalStateException("Unable to find the LMSession object for resending synchrounous messages");
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

	for( int i = 0; i < multMsg.getVector().size(); i++ )
	{
		lmReqs[i] = (LMManualControlRequest)multMsg.getVector().get(i);


		//better be a program for this Request Message!
		LMProgramBase progBase =
			getCache().getProgram( new Integer(lmReqs[i].getYukonID()) );

		//may or may not be an error, just warn for now
		if( progBase == null )
			CTILogger.warn( " ** A LMManualControlRequest message was sent without a defined LMProgramBase object");

		programResps[i] = new ResponseProg( lmReqs[i], progBase );
	}

				
	boolean success = LCUtils.executeSyncMessage( programResps );
	
	if( success )
		return null;
	else
		return programResps;
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


private Hashtable getOptionalParams( HttpServletRequest req )
{
	Hashtable optionalProps = new Hashtable(8);
	
	if( req.getParameter("duration") != null )
		optionalProps.put( "duration",
			CtiUtilities.getIntervalSecondsValue(req.getParameter("duration")) );

	
	if( req.getParameter("gearnum") != null )
		optionalProps.put( "gearnum", new Integer(req.getParameter("gearnum")) );


	if( req.getParameter("startbutton") != null
		 && req.getParameter("startbutton").equals("startat") )
	{
		GregorianCalendar gc = new GregorianCalendar();
		int secs = CtiUtilities.decodeStringToSeconds( req.getParameter("startTime1") );
		
		gc.setTime( ServletUtil.parseDateStringLiberally(req.getParameter("startdate")) );
		gc.set( GregorianCalendar.HOUR, 0 );
		gc.set( GregorianCalendar.MINUTE, 0 );
		gc.set( GregorianCalendar.SECOND, secs );

		optionalProps.put( "startdate", gc.getTime() );
	}
	else
	{
		//assume they want to start now
		optionalProps.put( "startdate", CtiUtilities.get1990GregCalendar().getTime() );
	}


	if( req.getParameter("stopbutton") != null
		 && req.getParameter("stopbutton").equals("stopat") )
	{
		GregorianCalendar gc = new GregorianCalendar();
		int secs = CtiUtilities.decodeStringToSeconds( req.getParameter("stopTime1") );
		
		gc.setTime( ServletUtil.parseDateStringLiberally(req.getParameter("stopdate")) );
		gc.set( GregorianCalendar.HOUR, 0 );
		gc.set( GregorianCalendar.MINUTE, 0 );
		gc.set( GregorianCalendar.SECOND, secs );

		optionalProps.put( "stopdate", gc.getTime() );
	}
	else if( req.getParameter("stopbutton") != null
				 && req.getParameter("stopbutton").equals("stopnow") )
	{
		//assume they want to stop now
		optionalProps.put( "stopdate", CtiUtilities.get1990GregCalendar().getTime() );
	}
	else
	{
		//set the stop time to 1 year from now if no stop selected
		GregorianCalendar c = new GregorianCalendar();
		c.add( c.YEAR, 1 );
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
		if( req.getParameter("startTime1").length() <= 0 )
			optionalProps.put("starttime", new Integer(LMControlArea.INVAID_INT));
		else
		{
			optionalProps.put("starttime",
				new Integer( CtiUtilities.decodeStringToSeconds( req.getParameter("startTime1") )) );
		}
	}
	
	
	if( req.getParameter("stopTime1") != null )
	{
		if( req.getParameter("stopTime1").length() <= 0 )
			optionalProps.put( "stoptime", new Integer(LMControlArea.INVAID_INT) );
		else
		{
			optionalProps.put( "stoptime",
				new Integer( CtiUtilities.decodeStringToSeconds( req.getParameter("stopTime1") )) );
		}
	}

	
	return optionalProps;
}


}