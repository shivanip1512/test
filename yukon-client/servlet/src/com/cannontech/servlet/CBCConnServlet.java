package com.cannontech.servlet;

/**
 * Maintains a connection to a CBC Server.
 * Stores itself in its servlet context so other
 * servlets can access the connection.
 * @author: ryan
 */
 
import java.awt.Color;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.cbc.data.CBCClientConnection;
import com.cannontech.cbc.gui.CapBankTableModel;
import com.cannontech.cbc.gui.SubBusTableModel;
import com.cannontech.cbc.messages.CBCStates;
import com.cannontech.cbc.messages.CBCSubAreaNames;
import com.cannontech.cbc.web.CBCCommandExec;
import com.cannontech.cbc.web.CapControlWebAnnex;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.state.State;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;

public class CBCConnServlet extends javax.servlet.http.HttpServlet implements MessageListener 
{		
	// Key used to store instances of this in the servlet context
	public static final String SERVLET_CONTEXT_ID = "CBCConnection";

	private CBCClientConnection conn;

	//insures only 1 set of these Strings for the servlet
	private final Vector areaNames = new Vector(32);


	//insures only 1 set of these Strings for the servlet
	private CBCCommandExec cbcExecutor = null;


public synchronized boolean isConnected()
{
	return getConnection().isValid();
}

public synchronized CBCClientConnection getConnection()
{
	if( conn == null )  
	{
		//first time this app has been hit		
		Registration reg = new Registration();
		reg.setAppName("CBC_WEB_CACHE@" + CtiUtilities.getUserName() );
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 300 );  // 5 minutes

		//The CBC server does not take this registration for now, dont use it
		//conn = new CBCClientConnection( reg );
		conn = new CBCClientConnection();
		
		conn.addMessageListener( this );


		//start the conn!!!
		try
		{
			conn.connect( 15000 );	
		}
		catch( java.io.IOException ex )
		{
			CTILogger.error( ex );
		}
	}
			
	return conn;
}


public Vector getAreaNames()
{
	return areaNames;
}

/**
 * Insert the method's description here.
 * Creation date: (4/12/2002 1:53:27 PM)
 * @param areaNames com.cannontech.cbc.messages.CBCSubAreaNames
 */
private synchronized void updateAreaList(CBCSubAreaNames areaNames_) 
{
	// remove all the values in the list
	getAreaNames().removeAllElements();
	getAreaNames().add( SubBusTableModel.ALL_FILTER );

	// add all area names to the list	
	for( int i = 0; i < areaNames_.getNumberOfAreas(); i++ )
		getAreaNames().add( areaNames_.getAreaName(i) );
}

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
		getConnection().disconnect();
		
		getConnection().deleteObservers();
	}
	catch( java.io.IOException ioe )
	{
		CTILogger.error("An exception occured disconnecting from load control");
	}
	
	super.destroy();
}

/**
 * Makes a connection to CBC Sercer and stores a reference to this in
 * the servlet context.
 * Creation date: (3/21/2001 11:35:57 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void init(javax.servlet.ServletConfig config) throws javax.servlet.ServletException
{
	super.init(config);

	getConnection();

	// Add this to the context so other servlets can access the connection
	getServletContext().setAttribute(SERVLET_CONTEXT_ID, this);	
}


/**
 * Creation date: (12/7/99 9:54:51 AM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * 
 * Expected session params:
 * redirectURL - Where do take the user after the post to the servlet
 * 
 * cbcAnnex - the needed data structure for all items in capcontrol
 * 
 * cmdExecute - the string representation of the command selected
 * 
 * cmdID - id of the command to be executed
 * 
 * controlType - what object type the control is for
 * 
 * paoID - item to be executed on
 * 
 * manualChange (optional)- the state selected (if applicable) of a manual change command
 */
public void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{
	HttpSession session = req.getSession( false );
	String redirectURL = req.getParameter("redirectURL");


	//the necessary (but not a great idea / explained below) web annex 
	CapControlWebAnnex cbcAnnex = (CapControlWebAnnex) session.getAttribute("cbcAnnex");
	
	//handle any commands that we may need to send to the server from any page here
	String cmdExecute = req.getParameter("cmdExecute");


	if( cbcAnnex != null && "Submit".equals(cmdExecute) )
	{
		try
		{
			Integer cmdID = new Integer( req.getParameter("cmdID") );
			String controlType = req.getParameter("controlType");
			Integer paoID = new Integer( req.getParameter("paoID") );
			String manChange = req.getParameter("manualChange");
			
			
			CTILogger.debug(req.getServletPath() +
				"	  cmdID = " + cmdID +
				", controlType = " + controlType +
				", paoID = " + paoID +
				", manualChng = " + manChange  );
			
			//send the command with the id, type, paoid
			executeCommand(
							cmdID.intValue(),
							controlType,
							paoID.intValue(),
							(manChange == null ? null : new Integer(manChange)),
							cbcAnnex );
			
			cbcAnnex.setRefreshRate( CapControlWebAnnex.REF_SECONDS_PEND );
		}
		catch( Exception e )
		{
			CTILogger.warn( "Command was attempted but failed for the following reason:", e );
		}
	}
	else
		CTILogger.warn( "CBC Command servlet was hit, but NO command was sent" );
	

	//always forward the client to the specified URL
	resp.sendRedirect( resp.encodeRedirectURL(req.getContextPath() + redirectURL) );
}

/**
 * 
 * Allows the execution of commands to the cbc server.
 * @param cmdID_ int : the id of the command from CBCCommand to be executed
 * @param cmdType_ String : type of command to execute ( CMD_SUB,CMD_FEEDER,CMD_CAPBANK ) 
 * @param rowID_ int : the row from a types data model to execute 
 * @param manChange_ Integer : the state index field for a capbank, null if not present
 */
private synchronized void executeCommand( int cmdID_, String cmdType_, int paoID_,
			Integer manChange_, CapControlWebAnnex annex_ )
{
	//We are FORCED pass in a CBCWebAnnex so we can find out the control point for
	// things like capbanks. Sub confirm commands need additional data alsow. The 
	// server should do this automitcally, but it does not. 
	cbcExecutor = new CBCCommandExec( annex_ );
		
	if( CapControlWebAnnex.CMD_SUB.equals(cmdType_) )
		cbcExecutor.execute_SubCmd( cmdID_, paoID_ );
	
	if( CapControlWebAnnex.CMD_FEEDER.equals(cmdType_) )
		cbcExecutor.execute_FeederCmd( cmdID_, paoID_ );

	if( CapControlWebAnnex.CMD_CAPBANK.equals(cmdType_) )
		cbcExecutor.execute_CapBankCmd( cmdID_, paoID_, manChange_ );
}


public void messageReceived( MessageEvent e )
{
	Message in = e.getMessage();

	CTILogger.debug( getClass().getName() + ": received type: " + in.getClass());

	if( in instanceof CBCSubAreaNames )
	{
		updateAreaList( (CBCSubAreaNames)in );
	}
	else if( in instanceof CBCStates )
	{
		CBCStates cbcStates = (CBCStates)in;
		State[] states = new State[cbcStates.getNumberOfStates()];
		
		for( int i = 0; i < cbcStates.getNumberOfStates(); i++ )
		{
			cbcStates.getState(i).setRawState( new Integer(i) ); // set the rawstate value
			states[i] = cbcStates.getState(i);
		}		


		Color[][] colors = new Color[states.length][2];
		String[] stateNames = new String[states.length];
			
		for( int i = 0; i < states.length; i++ )
		{
			stateNames[i] = states[i].getText();
			colors[i][0] = Colors.getColor( states[i].getForegroundColor().intValue() );
			colors[i][1] = Colors.getColor( states[i].getBackgroundColor().intValue() );
		}

		CapBankTableModel.setStates( colors, stateNames );			
	}
}

}