package com.cannontech.servlet;

/**
 * Maintains a cache with the CBC Server.
 * Stores usable objects into the servlet context.
 *
 * @author: ryan
 */ 
import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.dao.CapControlCommentDao;
import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.cbc.util.CBCDisplay;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.cbc.web.CBCCommandExec;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.WebUpdatedDAO;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.servlet.nav.DBEditorNav;
import com.cannontech.servlet.xml.DynamicUpdate;
import com.cannontech.servlet.xml.ResultXML;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ParamUtil;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.yukon.cbc.CBCArea;
import com.cannontech.yukon.cbc.CBCCommand;
import com.cannontech.yukon.cbc.CBCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.CapControlConst;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

@SuppressWarnings("serial")
public class CBCServlet extends ErrorAwareInitializingServlet {
    // Key used to store instances of this in the servlet context, this is the same
    //   as the application scope on JSP pages
    public static final String CBC_CACHE_STR = "capControlCache";
    //start this up every time with web server
    public static final String CBC_ONE_LINE = "oneLineSubs";
    public static final String REF_SECONDS_DEF = "60";
    public static final String REF_SECONDS_PEND = "5";

    private CapControlCache cbcCache;

    /**
     * Removes any resources used by this servlet
     *
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * Makes a connection to CBC Server and stores a reference to this in
     * the servlet context.
     * Creation date: (3/21/2001 11:35:57 AM)
     * @param config javax.servlet.ServletConfig
     * @exception javax.servlet.ServletException The exception description.
     */
    @Override
    public void doInit(ServletConfig config) throws ServletException {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        cbcCache = (CapControlCache) context.getBean("cbcCache", CapControlCache.class);
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
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        HttpSession session = req.getSession( false );
        if (session != null) {
            LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
            String redirectURL = ParamUtil.getString( req, "redirectURL", null );
            //handle any commands that a client may want to send to the CBC server
            int areaId = ParamUtil.getInteger(req, "areaId", -1);
            int specialAreaId = ParamUtil.getInteger(req, "specialAreaId", -1);
            int commentID = ParamUtil.getInteger(req, "selectedComment", 0);
            //be sure we have a valid user and that user has the rights to control
            if( user != null && CBCWebUtils.hasControlRights(session) ) {
                try {
                    if (areaId != -1){
                        updateSubAreaMenu(areaId, resp, user);
                    }else if(specialAreaId != -1){
                        updateSubSpecialAreaMenu(specialAreaId, resp, user);
                    }else if( commentID != 0){
                        processComment(commentID,req,user);
                    } else {
                        //send the command with the id, type, paoid
                        executeCommand( req, resp, user );
                    }
                } catch( Exception e ) {
                    CTILogger.warn( "Servlet request was attempted but failed for the following reason:", e );
                }
            }
            else {
                CTILogger.warn( "CBC Command servlet was hit, but NO action was taken" );
            }
            //always forward the client to the specified URL if present
            if( redirectURL != null ) {
                resp.sendRedirect( resp.encodeRedirectURL(req.getContextPath() + redirectURL) );
            }
        }
    }

    private void processComment( int cid, HttpServletRequest req, LiteYukonUser user) throws Exception {
        Integer paoId = ParamUtil.getInteger(req, "paoID");
        String comment = ParamUtil.getString(req, "comment");
        //get Comment out of parameters.
        if( paoId == null ){
            throw new Exception("Parameter Required for processComment: paoID ");
        }
        if( comment == null ){
            throw new Exception("Parameter Required for processComment: comment ");
        }
        CapControlCommentDao dao = YukonSpringHook.getBean("capCommentDao", CapControlCommentDao.class);
        if( cid == -1){//adding new
            CapControlComment c = new CapControlComment();
            c.setAltered(false);
            c.setAction( CommentAction.USER_COMMENT.toString() );
            c.setPaoId(paoId);
            c.setTime(new Timestamp(System.currentTimeMillis()));
            c.setUserId(user.getUserID());
            c.setComment(comment);
            dao.add(c);
        }else{//edit or delete
            Integer delete = ParamUtil.getInteger(req, "delete");
            if( delete == null ){
                throw new Exception("Parameter Required for processComment: delete ");
            }
            if( delete == -1 ){//edit
                CapControlComment c = dao.getById(cid);
                c.setComment(comment);
                c.setAction( CommentAction.USER_COMMENT.toString() );
                c.setUserId(user.getUserID());
                c.setAltered(true);
                c.setTime(new Timestamp(System.currentTimeMillis()));
                dao.update(c);
            }else if( delete == 1){//delete
                CapControlComment c = dao.getById(cid);
                dao.remove(c);
            }else{
                throw new Exception("Parameter Value incorrect for processComment: delete ");
            }
        }
    }

    private void updateSubAreaMenu(Integer areaId, HttpServletResponse response,
            LiteYukonUser user) throws IOException {
        try {
            Writer writer = response.getWriter();
            String ovuvEnabled = getOvUvEnabled(user);
            CBCArea area = cbcCache.getCBCArea(areaId);
            String msg = area.getPaoName() + ":" + areaId + ":";
            msg += (area.getDisableFlag()) ? "DISABLED" : "ENABLED";
            if (area.getOvUvDisabledFlag()) {
                msg += "-V";
            }
            msg+= ":" + ovuvEnabled;
            writer.write (msg);
            writer.flush();
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void updateSubSpecialAreaMenu(Integer areaId, HttpServletResponse response,
            LiteYukonUser user) throws IOException {
        try {
            Writer writer = response.getWriter();
            String ovuvEnabled = getOvUvEnabled(user);
            CBCSpecialArea area = cbcCache.getCBCSpecialArea(areaId);
            String msg = area.getPaoName() + ":" + areaId + ":";
            msg += (area.getDisableFlag())? "DISABLED" : "ENABLED";
            if (area.getOvUvDisabledFlag()) { 
                msg += "-V";
            }
            msg+= ":" + ovuvEnabled;
            writer.write (msg);
            writer.flush();
        } catch (NotFoundException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Returns a XML block.
     * 
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        HttpSession session = req.getSession( false );
        LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);

        //handle any commands that a client may want to send to the CBC server
        String redirectURL = ParamUtil.getString( req, "redirectURL", null );

        if( user != null ) {
            String type = ParamUtil.getString( req, "type", "xml" );
            try {
                if( "nav".equalsIgnoreCase(type) ) {
                    //handle navigation here
                    redirectURL = createNavigation( req );

                    //Code to memorize the position of the page we are at.
                    //We want to stop calling setNavigation when entering jsf pages
                    //this sets them up so on the return we end up where we left jsp land.

                    CtiNavObject navObject = (CtiNavObject) session.getAttribute("CtiNavObject");
                    navObject.setModuleExitPage(navObject.getCurrentPage());
                    //Hack to preserve an address that will normally fall off our 2 page history.
                    navObject.setPreservedAddress(navObject.getPreviousPage());
                    navObject.setNavigation(redirectURL);
                    navObject.clearHistory();

                    String val = ParamUtil.getString(req, "itemid", null);
                    if( val != null){
                        session.setAttribute("lastAccessed", val);
                    }
                    CTILogger.debug("servlet nav to: " + redirectURL );
                } else {
                    Writer writer = resp.getWriter();
                    //by default, treat this as a XML request
                    writer.write( createXMLResponse(req, resp, user) );
                    writer.flush();
                }
            } catch( Exception e ) {
                CTILogger.warn( "Servlet request was attempted but failed for the following reason:", e );
            }		
        } else {
            CTILogger.warn( "CBCServlet received a GET, but NO action was taken due to a missing YUKON_USER" );	
        }

        //always forward the client to the specified URL if present
        if( redirectURL != null ) {
            resp.sendRedirect( resp.encodeRedirectURL(req.getContextPath() + redirectURL) );
        }
    }
    
    private Integer[] toIntegerArray(String[] array) {
        Integer[] result = new Integer[array.length];
        for (int x = 0; x < array.length; x++) {
            result[x] = Integer.valueOf(array[x]);
        }
        return result;
    }
    
    /**
     * Forms the response for an XML request of data
     */
    private String createXMLResponse(HttpServletRequest req, HttpServletResponse resp, LiteYukonUser user) {
        resp.setHeader("Content-Type", "text/xml");
        //force this request not to be cached
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Expires", "0");
        resp.setHeader("Cache-Control", "no-store");
        
        String type = ParamUtil.getString( req, "type" );
        String method = ParamUtil.getString( req, "method" );
        String[] allIds = ParamUtil.getStrings( req, "id" );
        Date timeFrame = new Date(ParamUtil.getLong(req, "lastUpdate"));
        
        String ovuvEnabled = getOvUvEnabled(user);
        
        final CBCDisplay cbcDisplay = new CBCDisplay(user);

        // Only add content into the XML response message that has changed since "lastUpdate".
        WebUpdatedDAO<Integer> updatedObjMap = cbcCache.getUpdatedObjMap();
        List<Integer> updatedIds = updatedObjMap.getUpdatedIdsSince(timeFrame, toIntegerArray(allIds));
        
        try {
            ResultXML[] xmlMsgs = new ResultXML[updatedIds.size()];

            //check to see if the id was updated
            for( int i = 0; i < updatedIds.size(); i++) {
                
                final String updatedId = updatedIds.get(i).toString();
                //go get the XML data for the specific type of element
                if(handleSubstationGET(updatedId, xmlMsgs, i, cbcDisplay, ovuvEnabled)){
                    continue;
                }
                else if(handleSubGET(updatedId, xmlMsgs, i, cbcDisplay, ovuvEnabled)) {
                    continue;
                }
                else if(handleFeederGET(updatedId, xmlMsgs, i, cbcDisplay, ovuvEnabled)) {
                    continue;
                }
                else if(handleCapBankGET(updatedId, xmlMsgs, i, cbcDisplay, ovuvEnabled)) {
                    continue;
                }
            }

            CTILogger.debug(req.getServletPath() + "	  type = " + type + ", method = " + method );
            CTILogger.debug("URL = " + req.getRequestURL().toString() + "?" + req.getQueryString() );
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
    private boolean handleSubstationGET( String ids, ResultXML[] xmlMsgs, int indx, 
            CBCDisplay cbcDisplay, String ovuvEnabled) {
    	SubStation sub;
    	try {
        	sub = cbcCache.getSubstation( new Integer(ids) );
        } catch(NotFoundException e) {
            return false;
        }

        String[] optParams = {
            /*param0*/cbcDisplay.getHTMLFgColor(sub),
            /*param1*/cbcDisplay.getSubstationValueAt (sub, CBCDisplay.SUB_NAME_COLUMN).toString(),
            /*param2*/cbcDisplay.getSubstationValueAt(sub, CBCDisplay.SUB_POWER_FACTOR_COLUMN).toString(),
            /*param3*/(sub.getVerificationFlag().booleanValue())? "true" : "false",
            /*param4*/ovuvEnabled
        };

        xmlMsgs[indx] = new ResultXML(
            sub.getCcId().toString(),
            cbcDisplay.getSubstationValueAt(sub, CBCDisplay.SUB_CURRENT_STATE_COLUMN).toString(),     
            optParams );

        return true;
    }

    /**
     * Sets the XML data for the given SubBus id. Return true if the given
     * id is a SubBus id, else returns false.
     *  
     */
    private boolean handleSubGET( String ids, ResultXML[] xmlMsgs, int indx, 
            CBCDisplay cbcDisplay, String ovuvEnabled) {
    	SubBus sub;
    	try{ 
    		sub = cbcCache.getSubBus( new Integer(ids) );
    	} catch(NotFoundException e) {
            return false;
        }

        String[] optParams = {
            /*param0*/cbcDisplay.getHTMLFgColor(sub),
            /*param1*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_TARGET_COLUMN).toString(),
            /*param2*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_VAR_LOAD_COLUMN).toString(),
            /*param3*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_TIME_STAMP_COLUMN).toString(),
            /*param4*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_POWER_FACTOR_COLUMN).toString(),
            /*param5*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_WATTS_COLUMN).toString(),
            /*param6*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN).toString(),
            /*param7*/(sub.getVerificationFlag().booleanValue())? "true" : "false",
            /*param8*/cbcDisplay.getSubBusValueAt (sub, CBCDisplay.SUB_NAME_COLUMN).toString(),
            /*param9*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_TARGET_POPUP).toString(),
            /*param10*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_VAR_LOAD_POPUP).toString(),
            /*param11*/cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_WARNING_POPUP).toString(),
            /*param12*/ovuvEnabled
        };

        xmlMsgs[indx] = new ResultXML( sub.getCcId().toString(),
                                       cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_CURRENT_STATE_COLUMN).toString(),		
            optParams );
        xmlMsgs[indx].setWarning(cbcDisplay.getSubBusValueAt(sub, CBCDisplay.SUB_WARNING_IMAGE).toString());
        return true;
    }

    /**
     * Sets the XML data for the given Feeder id. Return true if the given
     * id is a Feeder id, else returns false.
     *  
     */
    private boolean handleFeederGET( String ids, ResultXML[] xmlMsgs, int indx, 
            CBCDisplay cbcDisplay, String ovuvEnabled) {
    	Feeder fdr;
    	try {
    		fdr = cbcCache.getFeeder( new Integer(ids) );
    	} catch( NotFoundException e) {
            return false;
        }
    	
        String[] optParams = {
            /*param0*/cbcDisplay.getHTMLFgColor(fdr),
            /*param1*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_TARGET_COLUMN).toString(),
            /*param2*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_VAR_LOAD_COLUMN).toString(),
            /*param3*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_TIME_STAMP_COLUMN).toString(),
            /*param4*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_POWER_FACTOR_COLUMN).toString(),
            /*param5*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_WATTS_COLUMN).toString(),
            /*param6*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN).toString(),
            /*param7*/cbcDisplay.getFeederValueAt (fdr, CBCDisplay.FDR_NAME_COLUMN).toString(),
            /*param8*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_TARGET_POPUP).toString(),
            /*param9*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_VAR_LOAD_POPUP).toString(),
            /*param10*/cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_WARNING_POPUP).toString(),
            /*param11*/ovuvEnabled
        };

        xmlMsgs[indx] = new ResultXML(
            fdr.getCcId().toString(),
            cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_CURRENT_STATE_COLUMN).toString(),
            optParams );
        xmlMsgs[indx].setWarning(cbcDisplay.getFeederValueAt(fdr, CBCDisplay.FDR_WARNING_IMAGE).toString());

        return true;
    }

    /**
     * Sets the XML data for the given CapBank id. Return true if the given
     * id is a CapBank id, else returns false.
     * @param req 
     *  
     */
    private boolean handleCapBankGET(String ids, ResultXML[] xmlMsgs, int indx, 
            CBCDisplay cbcDisplay, String ovuvEnabled) {
    	CapBankDevice capBank;
    	try {
    		capBank = cbcCache.getCapBankDevice( new Integer(ids) );
    	} catch( NotFoundException e) {
            return false;
    	}
        
        String liteStates = init_All_Manual_Cap_States();
        //get all the system states and concat them into a string
        String[] optParams = {
            /*param0*/cbcDisplay.getHTMLFgColor(capBank),
            /*param1*/cbcDisplay.getCapBankValueAt(capBank, CBCDisplay.CB_TIME_STAMP_COLUMN).toString(),
            /*param2*/cbcDisplay.getCapBankValueAt(capBank, CBCDisplay.CB_DAILY_MAX_TOTAL_OP_COLUMN).toString(),
            /*param3*/cbcDisplay.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN).toString(),
            /*param4*/ovuvEnabled,
            /*param5*/liteStates,
            /*param6*/cbcDisplay.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_POPUP).toString(),
            /*param7*/cbcDisplay.getCapBankValueAt(capBank, CBCDisplay.CB_WARNING_POPUP).toString()
        };

        xmlMsgs[indx] = new ResultXML(
            capBank.getCcId().toString(),
            cbcDisplay.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN).toString(),
            optParams );
        xmlMsgs[indx].setWarning(cbcDisplay.getCapBankValueAt(capBank, CBCDisplay.CB_WARNING_IMAGE).toString());

        return true;
    }
    
    /**
     * @return
     */
    private String init_All_Manual_Cap_States() {
        String liteStates = "";
        LiteState[] cbcStates = CBCUtils.getCBCStateNames();
        //create a comma separated string of all states
        //"Any:-1,Open:0,Close:1"
        for (int i = 0; i < cbcStates.length; i++) {
            LiteState state = cbcStates[i];
            liteStates += state.toString() + ":" + state.getStateRawState();
            if (i < (cbcStates.length - 1)) {
                liteStates+= ",";
            }
        }
        return liteStates;
    }

    /**
     * @param req
     * @return
     */
    private String getOvUvEnabled(LiteYukonUser user) {
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
    @SuppressWarnings("static-access")
    private String createNavigation( HttpServletRequest req ) {
        String pageType = ParamUtil.getString( req, "pageType" );
        String modType = ParamUtil.getString( req, "modType", "" );
        //keep the param list that follows modType=
        String[] realStrs = req.getQueryString().split("modType=.*?&" );
        String realParams =	realStrs.length > 0 ? realStrs[1] : "";
        String retURL = ""; 
        if( DBEditorNav.PAGE_TYPE_EDITOR.equals(pageType) ) {
            retURL = DBEditorNav.getEditorURL(modType);
        } else if( DBEditorNav.PAGE_TYPE_DELETE.equals(pageType) ) {
            retURL = DBEditorNav.getDeleteURL(modType);
        } else if (DBEditorNav.PAGE_TYPE_COPY.equals(pageType)) {
            retURL = DBEditorNav.getCopyURL(modType);
        }

        //add any additional parameters need for the page we are redirecting to
        if( realParams.length() > 0 ) {
            retURL += (retURL.indexOf("?") > 0 ? "&" : "?") + realParams;		
        }
        return retURL;
    }

    /**
     * Allows the execution of commands to the cbc server for all
     * CBC object types.
     * @throws ServletRequestBindingException 
     * @throws IOException 
     */
    private synchronized void executeCommand( HttpServletRequest req, HttpServletResponse resp,  LiteYukonUser user) throws ServletRequestBindingException, IOException {
        int cmdID = ServletRequestUtils.getIntParameter(req, "cmdID", 0);
        int paoID = ServletRequestUtils.getIntParameter(req, "paoID", 0);
        String controlType = ServletRequestUtils.getStringParameter(req, "controlType", "");
        List<String> subsInConflict = new ArrayList<String>();

        if (controlType.equalsIgnoreCase(CapControlConst.CMD_TYPE_SPECIAL_AREA) && cmdID == CBCCommand.ENABLE_AREA) {
            // Check for other special areas with this special area's subIds that are enabled.
            List<SubStation> subs = cbcCache.getSubstationsBySpecialArea(paoID);
            for(SubStation sub : subs) {
                boolean alreadyUsed = sub.getSpecialAreaEnabled();
                if(alreadyUsed) {
                    CBCSpecialArea otherArea = cbcCache.getCBCSpecialArea(sub.getSpecialAreaId());
                    if(!otherArea.getCcDisableFlag()) {
                        String areaName = otherArea.getCcName();
                        String value = areaName + " : " + sub.getCcName();
                        subsInConflict.add(value);
                    }
                }
            }

            boolean success = subsInConflict.isEmpty();
            if(success) {
                float[] optParams = StringUtils.toFloatArray(ServletRequestUtils.getStringParameters(req, "opt"));
                String operationalState = ServletRequestUtils.getStringParameter(req, "operationalState");
                CTILogger.debug(req.getServletPath() +
                                "     cmdID = " + cmdID +
                                ", controlType = " + controlType +
                                ", paoID = " + paoID +
                                ", opt = " + optParams +
                                ", operationalState = " + operationalState);
                
                final CBCCommandExec cbcExecutor = new CBCCommandExec(cbcCache, user);
                
                //send the command
                cbcExecutor.commandExecuteMethod(controlType, cmdID, paoID, optParams, operationalState);
            }
            
            
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", success);
            jsonObject.put("errMessage", "Error: Some substation/s in this area are already on another enabled special area:\n\n" 
                           + org.apache.commons.lang.StringUtils.join(subsInConflict, ",\n") 
                           + "\n\nRemove them from the other area or disable the other area first.");
            String jsonStr = jsonObject.toString();
            resp.addHeader("X-JSON", jsonStr);
        }else {
            float[] optParams = StringUtils.toFloatArray(ServletRequestUtils.getStringParameters(req, "opt"));
            String operationalState = ServletRequestUtils.getStringParameter(req, "operationalState");
            CTILogger.debug(req.getServletPath() +
                            "	  cmdID = " + cmdID +
                            ", controlType = " + controlType +
                            ", paoID = " + paoID +
                            ", opt = " + optParams +
                            ", operationalState = " + operationalState);
            
            final CBCCommandExec cbcExecutor = new CBCCommandExec(cbcCache, user);
            
            if(controlType.equalsIgnoreCase(CapControlConst.CMD_TYPE_SPECIAL_AREA)) {
                JSONObject jsonObject = new JSONObject();
                Boolean success = true;
                jsonObject.put("success", success);
                String jsonStr = jsonObject.toString();
                resp.addHeader("X-JSON", jsonStr);
            }
            
            //send the command
            cbcExecutor.commandExecuteMethod(controlType, cmdID, paoID, optParams, operationalState);
        }
    }
}