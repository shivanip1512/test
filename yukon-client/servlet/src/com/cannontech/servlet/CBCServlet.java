package com.cannontech.servlet;

/**
 * Maintains a cache with the CBC Server.
 * Stores usable objects into the servlet context.
 *
 * @author: ryan
 */ 
import java.io.IOException;
import java.io.Writer;
import java.sql.Time;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CapbankCommentDao;
import com.cannontech.cbc.model.CapbankComment;
import com.cannontech.cbc.oneline.OnelineCBCBroker;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.cbc.web.CBCCommandExec;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.WebUpdatedDAO;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.servlet.nav.DBEditorNav;
import com.cannontech.servlet.xml.DynamicUpdate;
import com.cannontech.servlet.xml.ResultXML;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ParamUtil;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CBCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

@SuppressWarnings("serial")
public class CBCServlet extends ErrorAwareInitializingServlet 
{		
	// Key used to store instances of this in the servlet context, this is the same
	//   as the application scope on JSP pages
	public static final String CBC_CACHE_STR = "capControlCache";
	//start this up every time with web server
	public static final String CBC_ONE_LINE = "oneLineSubs";

	//insures only 1 set of these Strings for the servlet
	private CBCCommandExec cbcExecutor = null;

	public static final String REF_SECONDS_DEF = "60";
	public static final String REF_SECONDS_PEND = "5";

	public static final String TYPE_SUB = "SUB_TYPE";
    public static final String TYPE_SUBSTATION = "SUBSTATION_TYPE";
	public static final String TYPE_FEEDER = "FEEDER_TYPE";
	public static final String TYPE_CAPBANK = "CAPBANK_TYPE";
    public static final String TYPE_AREA = "AREA_TYPE";
    
/**
 * Removes any resources used by this servlet
 *
 */
public void destroy() 
{
	// clean up
    super.destroy();
}

/**
 * Creates a cache if one is not already created
 *
 */
private CapControlCache getCapControlCache() {
	CapControlCache cbcCache = (CapControlCache)getServletContext().getAttribute(CBC_CACHE_STR);

	if( cbcCache == null ) {
		// Add application scope variables to the context
		cbcCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
		getServletContext().setAttribute(CBC_CACHE_STR, cbcCache);
	}
	
	return cbcCache;
}

/**
 * Makes a connection to CBC Server and stores a reference to this in
 * the servlet context.
 * Creation date: (3/21/2001 11:35:57 AM)
 * @param config javax.servlet.ServletConfig
 * @exception javax.servlet.ServletException The exception description.
 */
public void doInit(ServletConfig config) throws ServletException
{
	// Call the getters to init our objects in the context
	getCapControlCache();
	//create first set of html files for all subs
    String absPath = config.getServletContext().getRealPath(CBCWebUtils.ONE_LINE_DIR);
    generateOnelineHtml(absPath);
    
}

private void generateOnelineHtml(String absPath) {
    OnelineCBCBroker oneLine = new OnelineCBCBroker();
    oneLine.setDirBase(absPath);
    oneLine.setStaticMode(true);
    oneLine.start();
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
	if (session != null)
    {
    LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
    String redirectURL = ParamUtil.getString( req, "redirectURL", null );

    	//handle any commands that a client may want to send to the CBC server
        int areaId = ParamUtil.getInteger(req, "areaId", -1);
        int specialAreaId = ParamUtil.getInteger(req, "specialAreaId", -1);
        int commentID = ParamUtil.getInteger(req, "selectedComment", 0);
        
    	//be sure we have a valid user and that user has the rights to control
    	if( user != null && CBCWebUtils.hasControlRights(session) ) {
    		
    		try {
    			
                Writer writer = resp.getWriter();
                if (areaId != -1){
                    updateSubAreaMenu(areaId, writer);
                }else if(specialAreaId != -1){
                    updateSubSpecialAreaMenu(specialAreaId, writer);
                }else if( commentID != 0){
                    processComment(commentID,req,user);
                }
                else
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
}

private void processComment( int cid, HttpServletRequest req, LiteYukonUser user) throws Exception
{
    Integer paoId = ParamUtil.getInteger(req, "paoID");
    String comment = ParamUtil.getString(req, "comment");
    //get Comment out of parameters.
    if( paoId == null ){
        throw new Exception("Parameter Required for processComment: paoID ");
    }
    if( comment == null ){
        throw new Exception("Parameter Required for processComment: comment ");
    }
    CapbankCommentDao dao = (CapbankCommentDao) YukonSpringHook.getBean("capbankCommentDao");
    if( cid == -1){//adding new
        CapbankComment c = new CapbankComment();
        c.setAltered(false);
        c.setPaoId(paoId);
        c.setTime(new Date());
        c.setUserId(user.getUserID());
        c.setComment(comment);

        dao.add(c);
    }else{//edit or delete
        Integer delete = ParamUtil.getInteger(req, "delete");
        if( delete == null ){
            throw new Exception("Parameter Required for processComment: delete ");
        }
        if( delete == -1 ){//edit
            CapbankComment c = dao.getById(cid);
            c.setComment(comment);
            c.setUserId(user.getUserID());
            c.setAltered(true);
            c.setTime(new Date());
            
            dao.update(c);
            
        }else if( delete == 1){//delete
            CapbankComment c = dao.getById(cid);
            dao.remove(c);
        }else{
            throw new Exception("Parameter Value incorrect for processComment: delete ");
        }
        
    }
}

private void updateSubAreaMenu(Integer areaId, Writer writer) throws IOException {
    CapControlCache cache = getCapControlCache();
    CBCArea area = cache.getCBCArea(areaId);
    Boolean state = (Boolean) getCapControlCache().getAreaStateMap().get(area.getPaoName());
    String msg = area.getPaoName() + ":" + areaId + ":";
    msg += (state)?"ENABLED":"DISABLED";
    if ( area.getOvUvDisabledFlag() ) {
        msg += "-V";
    }
    writer.write (msg);
    writer.flush();
}

private void updateSubSpecialAreaMenu(Integer areaId, Writer writer) throws IOException {
    CapControlCache cache = getCapControlCache();
    CBCSpecialArea area = cache.getCBCSpecialArea(areaId);
    Boolean state = (Boolean) getCapControlCache().getSpecialAreaStateMap().get(area.getPaoName());
    String msg = area.getPaoName() + ":" + areaId + ":";
    msg += (state)?"ENABLED":"DISABLED";
    if ( area.getOvUvDisabledFlag() ) {
        msg += "-V";
    }
    writer.write (msg);
    writer.flush();
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
				
                //Code to memorize the position of the page we are at.
				//We want to stop calling setNavigation when entering jsf pages
				//this sets them up so on the return we end up where we left jsp land.
				CBCNavigationUtil.bookmarkThisLocationCCSpecial(session);
                
                String val = ParamUtil.getString(req, "itemid", null);
                if( val != null){
                    session.setAttribute("lastAccessed", val);
                }
                CTILogger.debug("servlet nav to: " + redirectURL );
			}
			else {
                Writer writer = resp.getWriter();
                //by default, treat this as a XML request
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
	String[] allIds = ParamUtil.getStrings( req, "id" );
	Date timeFrame = new Date(ParamUtil.getLong(req, "lastUpdate"));
	//ParamUtil.getInts( req, "id");
	//filter the ids into the ones that has been updated by the server 
	//since the last update
	WebUpdatedDAO updatedObjMap = getCapControlCache().getUpdatedObjMap();
	String[] updatedIds = updatedObjMap.getUpdatedIdsSince (allIds, timeFrame);
	try
	{
		ResultXML[] xmlMsgs = new ResultXML[ updatedIds.length ];
		//check to see if the id was updated
				
		for( int i = 0; i < updatedIds.length; i++) {
			//go get the XML data for the specific type of element
            if(handleSubstationGET(updatedIds[i], xmlMsgs, i)){
                continue;
            }
			if(handleSubGET(updatedIds[i], xmlMsgs, i)) {
				continue;
			}
			if (handleFeederGET(updatedIds[i], xmlMsgs, i)) {
				continue;
			}
			if (handleCapBankGET(req, updatedIds[i], xmlMsgs, i)) {
				continue;
			}
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
private boolean handleSubstationGET( String ids, ResultXML[] xmlMsgs, int indx )
{
    SubStation sub = getCapControlCache().getSubstation( new Integer(ids) );

    if( sub == null ) {
        return false;
    }

    String[] optParams = {
        /*param0*/CBCDisplay.getHTMLFgColor(sub),
        /*param1*/CBCUtils.CBC_DISPLAY.getSubstationValueAt (sub, CBCDisplay.SUB_NAME_COLUMN).toString(),
        /*param2*/CBCUtils.CBC_DISPLAY.getSubstationValueAt(sub, CBCDisplay.SUB_POWER_FACTOR_COLUMN).toString(),
        /*param3*/(sub.getVerificationFlag().booleanValue())? "true" : "false"
    };

    xmlMsgs[indx] = new ResultXML(
        sub.getCcId().toString(),
        CBCUtils.CBC_DISPLAY.getSubstationValueAt(sub, CBCDisplay.SUB_CURRENT_STATE_COLUMN).toString(),     
        optParams );

    return true;
}

/**
 * Sets the XML data for the given SubBus id. Return true if the given
 * id is a SubBus id, else returns false.
 *  
 */
private boolean handleSubGET( String ids, ResultXML[] xmlMsgs, int indx ) {
	SubBus sub = getCapControlCache().getSubBus( new Integer(ids) );
	if( sub == null ) {
		return false;
    }

	String[] optParams = {
		/*param0*/CBCDisplay.getHTMLFgColor(sub),
		/*param1*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_TARGET_COLUMN).toString(),
		/*param2*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_VAR_LOAD_COLUMN).toString(),
		/*param3*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_TIME_STAMP_COLUMN).toString(),
		/*param4*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_POWER_FACTOR_COLUMN).toString(),
		/*param5*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_WATTS_COLUMN).toString(),
		/*param6*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN).toString(),
		/*param7*/(sub.getVerificationFlag().booleanValue())? "true" : "false",
		/*param8*/CBCUtils.CBC_DISPLAY.getSubBusValueAt (sub, CBCDisplay.SUB_NAME_COLUMN).toString(),
        /*param9*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_TARGET_POPUP).toString(),
        /*param10*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_VAR_LOAD_POPUP).toString(),
        /*param11*/CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_WARNING_POPUP).toString()
	};

	xmlMsgs[indx] = new ResultXML( sub.getCcId().toString(),
		CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_CURRENT_STATE_COLUMN).toString(),		
		optParams );
	xmlMsgs[indx].setWarning(CBCUtils.CBC_DISPLAY.getSubBusValueAt(sub, CBCDisplay.SUB_WARNING_IMAGE).toString());
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
		/*param6*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN).toString(),
		/*param7*/CBCUtils.CBC_DISPLAY.getFeederValueAt (fdr, CBCDisplay.FDR_NAME_COLUMN).toString(),
		/*param8*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_TARGET_POPUP).toString(),
        /*param9*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_VAR_LOAD_POPUP).toString(),
        /*param10*/CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_WARNING_POPUP).toString()
	};

	xmlMsgs[indx] = new ResultXML(
		fdr.getCcId().toString(),
		CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_CURRENT_STATE_COLUMN).toString(),
		optParams );
	xmlMsgs[indx].setWarning(CBCUtils.CBC_DISPLAY.getFeederValueAt(fdr, CBCDisplay.FDR_WARNING_IMAGE).toString());

	return true;
}

/**
 * Sets the XML data for the given CapBank id. Return true if the given
 * id is a CapBank id, else returns false.
 * @param req 
 *  
 */
private boolean handleCapBankGET( HttpServletRequest req, String ids, ResultXML[] xmlMsgs, int indx )
{
    HttpSession session = req.getSession( false );
    LiteYukonUser user = new LiteYukonUser();
    if (session != null) {
        user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
    }
	CapBankDevice capBank = getCapControlCache().getCapBankDevice( new Integer(ids) );
	//see if the user has the rights to change ov/uv
	String allow_ovuv = init_isOVUV(req);
	String liteStates = init_All_Manual_Cap_States();
	
	if( capBank == null )
		return false;

	//get all the system states and concat them into a string

	
	String[] optParams =
	{
		/*param0*/CBCDisplay.getHTMLFgColor(capBank),
		/*param1*/CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_TIME_STAMP_COLUMN, user).toString(),
		/*param2*/CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_DAILY_TOTAL_OP_COLUMN, user).toString(),
		/*param3*/CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN, user).toString(),
		/*param4*/allow_ovuv,
		/*param5*/liteStates,
        /*param6*/CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_POPUP, user).toString(),
        /*param7*/CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_WARNING_POPUP, user).toString()
	};

	xmlMsgs[indx] = new ResultXML(
		capBank.getCcId().toString(),
		CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN, user).toString(),
		optParams );
	xmlMsgs[indx].setWarning(CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_WARNING_IMAGE, user).toString());

	return true;
}

/**
 * @return
 */
private String init_All_Manual_Cap_States() {
	String liteStates = "";
	LiteState[] cbcStates = CBCDisplay.getCBCStateNames();
	//create a comma separated string of all states
	//"Any:-1,Open:0,Close:1"
	for (int i = 0; i < cbcStates.length; i++) {
		LiteState state = cbcStates[i];
		liteStates += state.toString() + ":" + state.getStateRawState();
		if (i < (cbcStates.length - 1))
			liteStates+= ",";
	}
	return liteStates;
}

/**
 * @param req
 * @return
 */
private String init_isOVUV(HttpServletRequest req) {
	HttpSession session = req.getSession(false);
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
	boolean allow_ovuv = DaoFactory.getAuthDao().checkRoleProperty(user, CBCSettingsRole.CBC_ALLOW_OVUV);
	return ("" + allow_ovuv).toLowerCase();
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
	
	else if (DBEditorNav.PAGE_TYPE_COPY.equals(pageType)) {
		retURL = DBEditorNav.getCopyURL(modType);
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
	float[] optParams = StringUtils.toFloatArray( ParamUtil.getStrings(req, "opt") );

	CTILogger.debug(req.getServletPath() +
		"	  cmdID = " + cmdID +
		", controlType = " + controlType +
		", paoID = " + paoID +
		", opt = " + optParams  );
			
	//Creates a new command executor and gives it a reference to
	// the cache.
	cbcExecutor = new CBCCommandExec( getCapControlCache(), userName );
		

    //send the command with the id, type, paoid
    if( CBCServlet.TYPE_SUBSTATION.equals(controlType) ) {
        cbcExecutor.execute_SubstationCmd( cmdID, paoID );
    }
	if( CBCServlet.TYPE_SUB.equals(controlType) ) {
		cbcExecutor.execute_SubCmd( cmdID, paoID );
    }
	if( CBCServlet.TYPE_FEEDER.equals(controlType) ) {
		cbcExecutor.execute_FeederCmd( cmdID, paoID );
    }
	if( CBCServlet.TYPE_CAPBANK.equals(controlType) ) {
		cbcExecutor.execute_CapBankCmd( cmdID, paoID, optParams );
    }
    if ( CBCServlet.TYPE_AREA.equals (controlType) ) { 
        cbcExecutor.execute_SubAreaCmd(cmdID, paoID);
    }
}

}