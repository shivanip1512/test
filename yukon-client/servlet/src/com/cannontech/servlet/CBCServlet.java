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
import com.cannontech.servlet.nav.DBEditorNav;
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
private CapControlCache getCapControlCache()
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
	getCapControlCache();
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
	if( user != null && CBCWebUtils.hasControlRights(session) ) {
		
		try {
			//send the command with the id, type, paoid
			executeCommand( req, user.getUsername() );
		}
		catch( Exception e ) {
			CTILogger.warn( "Servlet request was attempted but failed for the following reason:", e );
		}
	}
	else
		CTILogger.warn( "CBC Command servlet was hit, but NO action was taken" );
	

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

	//handle any commands that a client may want to send to the CBC server
	String redirectURL = ParamUtil.getString( req, "redirectURL", null );

	if( user != null )
	{
		String type = ParamUtil.getString( req, "type", "xml" );

		try {
			if( "nav".equalsIgnoreCase(type) ) {
				//handle navigation here
				redirectURL = createNavigation( req );

				CTILogger.debug("servlet nav to: " + redirectURL );
			}
			else {
				//by default, treat this as a XML request
				Writer writer = resp.getWriter();
				writer.write( createXMLResponse(req, resp) );
				writer.flush();
			}

		}
		catch( Exception e ) {
			CTILogger.warn( "Servlet request was attempted but failed for the following reason:", e );
		}		
	}
	else
		CTILogger.warn( "CBCServlet received a GET, but NO action was taken due to a missing YUKON_USER" );	


	//always forward the client to the specified URL if present
	if( redirectURL != null )
		resp.sendRedirect( resp.encodeRedirectURL(req.getContextPath() + redirectURL) );
}

/**
 * Forms the response for an XML request of data
 */
private String createXMLResponse(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {

	resp.setHeader("Content-Type", "text/xml");
	
	//force this request not to be cached
	resp.setHeader("Pragma", "no-cache");
	resp.setHeader("Expires", "0");
	resp.setHeader("Cache-Control", "no-store");

	String type = ParamUtil.getString( req, "type" );
	String method = ParamUtil.getString( req, "method" );
	String[] ids = ParamUtil.getStrings( req, "id" );

	try
	{
		ResultXML[] xmlMsgs = new ResultXML[ ids.length ];
		boolean fnd = false;
		
		for( int i = 0; i < ids.length; i++, fnd = false ) {
			//go get the XML data for the specific type of element
			if(!fnd) fnd |= handleSubGET(ids[i], xmlMsgs, i);
			if(!fnd) fnd |= handleFeederGET(ids[i], xmlMsgs, i);
			if(!fnd) fnd |= handleCapBankGET(ids[i], xmlMsgs, i);
		}


		CTILogger.debug(req.getServletPath() +
			"	  type = " + type +
			", method = " + method );
		CTILogger.debug("URL = " + 
			req.getRequestURL().toString() + "?" + req.getQueryString() );

		return DynamicUpdate.createXML(method, xmlMsgs);
	}
	catch( Exception e ) {
		CTILogger.warn( "Unable to execute GET for the following reason:", e );
	}


	//we could not understand the request
	return "Unable to form response";
}


/**
 * Sets the XML data for the given SubBus id. Return true if the given
 * id is a SubBus id, else returns false.
 *  
 */
private boolean handleSubGET( String ids, ResultXML[] xmlMsgs, int indx )
{
	SubBus sub =
		getCapControlCache().getSubBus( new Integer(ids) );

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
private boolean handleFeederGET( String ids, ResultXML[] xmlMsgs, int indx )
{
	Feeder fdr =
		getCapControlCache().getFeeder( new Integer(ids) );

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
private boolean handleCapBankGET( String ids, ResultXML[] xmlMsgs, int indx )
{
	CapBankDevice capBank =
		getCapControlCache().getCapBankDevice( new Integer(ids) );

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
 * Determine where the request with the given parameters should be
 * redirected to. The parmeters for the new page are passed on directly
 * to the next page.
 * 
 * /servlet/CBCServlet?type=nav&pageType=editor&modType=capcontrol
 */
private String createNavigation( HttpServletRequest req ) {

	String pageType = ParamUtil.getString( req, "pageType" );
	String modType = ParamUtil.getString( req, "modType", "" );
	
	//keep the param list that follows modType=
	String[] realStrs = req.getQueryString().split("modType=.*?&" );
	String realParams =	realStrs.length > 0 ? realStrs[1] : "";

	String retURL = ""; 

	if( DBEditorNav.PAGE_TYPE_EDITOR.equals(pageType) ) {
		retURL = DBEditorNav.getEditorURL(modType);
	}
	else if( DBEditorNav.PAGE_TYPE_DELETE.equals(pageType) ) {
		retURL = DBEditorNav.getDeleteURL(modType);
	}

	//add any additional parameters need for the page we are redirecting to
	if( realParams.length() > 0 )
		retURL += (retURL.indexOf("?") > 0 ? "&" : "?") + realParams;		

	return retURL;
}


/**
 * Allows the execution of commands to the cbc server for all
 * CBC object types.
 */
private synchronized void executeCommand( HttpServletRequest req, String userName ) {

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
			
	//Creates a new command executor and gives it a reference to
	// the cache.
	cbcExecutor = new CBCCommandExec( getCapControlCache(), userName );
		
	//send the command with the id, type, paoid
	if( CBCServlet.TYPE_SUB.equals(controlType) )
		cbcExecutor.execute_SubCmd( cmdID, paoID );
	
	if( CBCServlet.TYPE_FEEDER.equals(controlType) )
		cbcExecutor.execute_FeederCmd( cmdID, paoID );

	if( CBCServlet.TYPE_CAPBANK.equals(controlType) )
		cbcExecutor.execute_CapBankCmd( cmdID, paoID, optParams );
}

}