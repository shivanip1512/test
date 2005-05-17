package com.cannontech.servlet;

/**
 * Maintains a cache with the CBC Server.
 * Stores usable objects into the servlet context.
 *
 * @author: ryan
 */ 
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.cannontech.cbc.web.CBCCommandExec;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.cbc.web.CapControlCache;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.xml.DynamicUpdate;
import com.cannontech.servlet.xml.ResultXML;
import com.cannontech.util.ParamUtil;
import com.cannontech.yukon.cbc.CBCDisplay;
import com.cannontech.yukon.cbc.CBCUtils;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;

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

	public static final String TYPE_SUB = "SUB_TYPE";
	public static final String TYPE_FEEDER = "FEEDER_TYPE";
	public static final String TYPE_CAPBANK = "CAPBANK_TYPE";


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
 * 
 * Expected session params:
 * redirectURL - Where do take the user after the post to the servlet
 * 
 * cmdID - id of the command to be executed
 * 
 * controlType - what object type the control is for
 * 
 * paoID - item to be executed on
 * 
 * opt (optional)- optional parameters for things like:
 *		manual change command
 * 		temp move command
 * 		...etc
 */
public void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{
	HttpSession session = req.getSession( false );
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);

	//handle any commands that a client may want to send to the CBC server
	String redirectURL = ParamUtil.getString( req, "redirectURL", null );

	//be sure we have a valid user and that user has the rights to control
	if( user != null && CBCWebUtils.hasControlRights(session) )
	{
		try
		{
			int cmdID = ParamUtil.getInteger( req, "cmdID" );
			int paoID = ParamUtil.getInteger( req, "paoID" );
			String controlType = ParamUtil.getString( req, "controlType" );
			int[] optParams =
				StringUtils.toIntArray( ParamUtil.getStrings(req, "opt") );


			CTILogger.debug(req.getServletPath() +
				"	  cmdID = " + cmdID +
				", controlType = " + controlType +
				", paoID = " + paoID +
				", opt = " + optParams  );
			
			//send the command with the id, type, paoid
			_executeCommand(
				cmdID,
				controlType,
				paoID,
				optParams,
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
	

	//always forward the client to the specified URL if present
	if( redirectURL != null )
		resp.sendRedirect( resp.encodeRedirectURL(req.getContextPath() + redirectURL) );
}

/**
 * Returns a XML block.
 * 
 */
public void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException
{
	HttpSession session = req.getSession( false );
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
	resp.setHeader("Content-Type", "text/xml");
	
	//force this request not to be cached
	resp.setHeader("Pragma", "no-cache");
	resp.setHeader("Expires", "0");
	resp.setHeader("Cache-Control", "no-store");

	String type = ParamUtil.getString( req, "type" );
	String method = ParamUtil.getString( req, "method" );
	String[] ids = ParamUtil.getStrings( req, "id" );

	if( user != null )
	{
		try
		{
			ResultXML[] xmlMsgs = new ResultXML[ ids.length ];
			boolean fnd = false;
			
			for( int i = 0; i < ids.length; i++, fnd = false )
			{
				//go get the XML data for the specific type of element
				if(!fnd) fnd |= _handleSubGET(ids[i], xmlMsgs, i);
				if(!fnd) fnd |= _handleFeederGET(ids[i], xmlMsgs, i);
				if(!fnd) fnd |= _handleCapBankGET(ids[i], xmlMsgs, i);
			}

			Writer writer = resp.getWriter();
			writer.write(
				DynamicUpdate.createXML(method, xmlMsgs) );

			writer.flush();
			

			CTILogger.debug(req.getServletPath() +
				"	  type = " + type +
				", method = " + method );
			CTILogger.debug("URL = " + 
				req.getRequestURL().toString() + "?" + req.getQueryString() );

		}
		catch( Exception e )
		{
			CTILogger.warn( "Unable to execute GET for the following reason:", e );
		}
	}
	else
		CTILogger.warn( "CBCServlet received a GET, but NO action was taken due to a missing YUKON_USER" );	
}

/**
 * Sets the XML data for the given SubBus id. Return true if the given
 * id is a SubBus id, else returns false.
 *  
 */
private boolean _handleSubGET( String ids, ResultXML[] xmlMsgs, int indx )
{
	SubBus sub =
		_getCapControlCache().getSubBus( new Integer(ids) );

	if( sub == null )
		return false;

	String[] optParams =
	{
		/*param0*/CBCDisplay.getHTMLFgColor(sub),
		/*param1*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_TARGET_COLUMN).toString(),
		/*param2*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_VAR_LOAD_COLUMN).toString(),
		/*param3*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_TIME_STAMP_COLUMN).toString(),
		/*param4*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_POWER_FACTOR_COLUMN).toString(),
		/*param5*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_WATTS_COLUMN).toString(),
		/*param6*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN).toString()
	};

	xmlMsgs[indx] = new ResultXML(
		sub.getCcId().toString(),
		CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_CURRENT_STATE_COLUMN).toString(),		
		optParams );


	return true;
}

/**
 * Sets the XML data for the given Feeder id. Return true if the given
 * id is a Feeder id, else returns false.
 *  
 */
private boolean _handleFeederGET( String ids, ResultXML[] xmlMsgs, int indx )
{
	Feeder fdr =
		_getCapControlCache().getFeeder( new Integer(ids) );

	if( fdr == null )
		return false;

	String[] optParams =
	{
		/*param0*/CBCDisplay.getHTMLFgColor(fdr),
		/*param1*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_TARGET_COLUMN).toString(),
		/*param2*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_VAR_LOAD_COLUMN).toString(),
		/*param3*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_TIME_STAMP_COLUMN).toString(),
		/*param4*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_POWER_FACTOR_COLUMN).toString(),
		/*param5*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_WATTS_COLUMN).toString(),
		/*param6*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN).toString()
	};

	xmlMsgs[indx] = new ResultXML(
		fdr.getCcId().toString(),
		CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_CURRENT_STATE_COLUMN).toString(),
		optParams );


	return true;
}

/**
 * Sets the XML data for the given CapBank id. Return true if the given
 * id is a CapBank id, else returns false.
 *  
 */
private boolean _handleCapBankGET( String ids, ResultXML[] xmlMsgs, int indx )
{
	CapBankDevice capBank =
		_getCapControlCache().getCapBankDevice( new Integer(ids) );

	if( capBank == null )
		return false;

	String[] optParams =
	{
		/*param0*/CBCDisplay.getHTMLFgColor(capBank),
		/*param1*/CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_TIME_STAMP_COLUMN).toString(),
		/*param2*/CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_OP_COUNT_COLUMN).toString()
	};

	xmlMsgs[indx] = new ResultXML(
		capBank.getCcId().toString(),
		CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN).toString(),
		optParams );

	return true;
}



/**
 * Allows the execution of commands to the cbc server for all
 * CBC object types.
 * 
 */
private synchronized void _executeCommand( int _cmdID, String _cmdType, int _paoID,
			int[] _optParams, String _userName )
{
	//Creates a new command executor and gives it a reference to
	// the cache.
	cbcExecutor = new CBCCommandExec( _getCapControlCache(), _userName );
		
	if( CBCServlet.TYPE_SUB.equals(_cmdType) )
		cbcExecutor.execute_SubCmd( _cmdID, _paoID );
	
	if( CBCServlet.TYPE_FEEDER.equals(_cmdType) )
		cbcExecutor.execute_FeederCmd( _cmdID, _paoID );

	if( CBCServlet.TYPE_CAPBANK.equals(_cmdType) )
		cbcExecutor.execute_CapBankCmd( _cmdID, _paoID, _optParams );
}

}