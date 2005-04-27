package com.cannontech.servlet;

/**
 * Maintains a cache with the CBC Server.
 * Stores usable objects into the servlet context.
 *
 * @author: ryan
 */ 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.cannontech.cbc.web.CBCCommandExec;
import com.cannontech.cbc.web.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ParamUtil;

import javax.servlet.http.HttpServlet;

public class CBCServlet extends HttpServlet 
{		
	// Key used to store instances of this in the servlet context, this is the same
	//   as the application scope on JSP pages
	public static final String CBC_CACHE_STR = "capControlCache";

	//insures only 1 set of these Strings for the servlet
	private CBCCommandExec cbcExecutor = null;

	public static final String REF_SECONDS_DEF = "60";
	public static final String REF_SECONDS_PEND = "5";

	public static final String CMD_SUB = "SUB_CNTRL";
	public static final String CMD_FEEDER = "FEEDER_CNTRL";
	public static final String CMD_CAPBANK = "CAPBANK_CNTRL";


/**
 * Removes any resources used by this servlet
 *
 */
public void destroy() 
{
	// Removing any application scope varaibles
	getServletContext().removeAttribute(CBC_CACHE_STR);

	super.destroy();
}

/**
 * Creates a cache if one is not alread created
 *
 */
private CapControlCache _getCapControlCache()
{
	CapControlCache cbcCache =
		(CapControlCache)getServletContext().getAttribute(CBC_CACHE_STR);

	if( cbcCache == null )
	{
		// Add application scope variables to the context
		cbcCache = new CapControlCache();
		getServletContext().setAttribute(CBC_CACHE_STR, cbcCache);
	}
	
	return (CapControlCache)getServletContext().getAttribute(CBC_CACHE_STR);
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

	// Call the getters to init our objects in the context
	_getCapControlCache();
}


/**
 * Creation date: (12/7/99 9:54:51 AM)
 * @param req javax.servlet.http.HttpServletRequest
 * @param resp javax.servlet.http.HttpServletResponse
 * 
 * Expected session params:
 * redirectURL - Where do take the user after the post to the servlet
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
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);

	//handle any commands that we may need to send to the server from any page here
	//String cmdExecute = ParamUtil.getString( req, "cmdExecute" );
	String redirectURL = ParamUtil.getString( req, "redirectURL" );


	if( user != null )
	{
		try
		{
			int cmdID = ParamUtil.getInteger( req, "cmdID" );
			int paoID = ParamUtil.getInteger( req, "paoID" );
			String controlType = ParamUtil.getString( req, "controlType" );
			int manChange = ParamUtil.getInteger( req, "manualChange" );
			
			
			CTILogger.debug(req.getServletPath() +
				"	  cmdID = " + cmdID +
				", controlType = " + controlType +
				", paoID = " + paoID +
				", manualChng = " + manChange  );
			
			//send the command with the id, type, paoid
			_executeCommand(
				cmdID,
				controlType,
				paoID,
				manChange,
				user.getUsername() );
			
			//cbcAnnex.setRefreshRate( CapControlWebAnnex.REF_SECONDS_PEND );
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
private synchronized void _executeCommand( int cmdID_, String cmdType_, int paoID_,
			int manChange_, String userName_ )
{
	//We are FORCED pass in a CBCWebAnnex so we can find out the control point for
	// things like capbanks. Sub confirm commands need additional data alsow. The 
	// server should do this automitcally, but it does not. 
	cbcExecutor = new CBCCommandExec( _getCapControlCache(), userName_ );
		
	if( CBCServlet.CMD_SUB.equals(cmdType_) )
		cbcExecutor.execute_SubCmd( cmdID_, paoID_ );
	
	if( CBCServlet.CMD_FEEDER.equals(cmdType_) )
		cbcExecutor.execute_FeederCmd( cmdID_, paoID_ );

	if( CBCServlet.CMD_CAPBANK.equals(cmdType_) )
		cbcExecutor.execute_CapBankCmd( cmdID_, paoID_, manChange_ );
}

}