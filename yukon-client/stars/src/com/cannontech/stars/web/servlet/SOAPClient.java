package com.cannontech.stars.web.servlet;

import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.ActionBase;
import com.cannontech.stars.web.action.CallTrackingAction;
import com.cannontech.stars.web.action.CreateApplianceAction;
import com.cannontech.stars.web.action.CreateCallAction;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.DeleteApplianceAction;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.GetCustAccountAction;
import com.cannontech.stars.web.action.GetEnergyCompanySettingsAction;
import com.cannontech.stars.web.action.GetLMCtrlHistAction;
import com.cannontech.stars.web.action.GetNextCallNumberAction;
import com.cannontech.stars.web.action.GetNextOrderNumberAction;
import com.cannontech.stars.web.action.GetServiceHistoryAction;
import com.cannontech.stars.web.action.LoginAction;
import com.cannontech.stars.web.action.MultiAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.ProgramOptOutAction;
import com.cannontech.stars.web.action.ProgramReenableAction;
import com.cannontech.stars.web.action.ProgramSignUpAction;
import com.cannontech.stars.web.action.ReloadCustAccountAction;
import com.cannontech.stars.web.action.SearchCustAccountAction;
import com.cannontech.stars.web.action.SendInterviewAnswersAction;
import com.cannontech.stars.web.action.SendOddsForControlAction;
import com.cannontech.stars.web.action.UpdateApplianceAction;
import com.cannontech.stars.web.action.UpdateCallReportAction;
import com.cannontech.stars.web.action.UpdateControlNotificationAction;
import com.cannontech.stars.web.action.UpdateCustAccountAction;
import com.cannontech.stars.web.action.UpdateLMHardwareAction;
import com.cannontech.stars.web.action.UpdateLMHardwareConfigAction;
import com.cannontech.stars.web.action.UpdateLoginAction;
import com.cannontech.stars.web.action.UpdateResidenceInfoAction;
import com.cannontech.stars.web.action.UpdateServiceRequestAction;
import com.cannontech.stars.web.action.UpdateThermostatManualOptionAction;
import com.cannontech.stars.web.action.UpdateThermostatScheduleAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.serialize.StarsExitInterviewQuestions;
import com.cannontech.stars.xml.serialize.StarsGetEnergyCompanySettingsResponse;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPMessenger;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.stars.xml.util.XMLUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPClient extends HttpServlet {

    private static String SOAP_SERVER_URL = null;
    private static SOAPMessenger soapMsgr = null;
    private static boolean serverLocal = true;

    private static final String loginURL = "/login.jsp";
    private static final String homeURL = "/operator/Operations.jsp";

    public SOAPClient() {
        super();
    }

	public static boolean isServerLocal() {
		return serverLocal;
	}

	public static void setServerLocal(boolean serverLocal) {
		SOAPClient.serverLocal = serverLocal;
	}

	/**
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		super.init();
		try {
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle( "config" );
			SOAP_SERVER_URL = bundle.getString( "soap_server" );
			CTILogger.info( "SOAP Server is remotely at \"" + SOAP_SERVER_URL + "\"" );
			
			// "soap_server" is defined in config.properties, which means SOAPServer is running remotely
			setServerLocal( false );
			SOAPServer.setClientLocal( false );
			
			soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );
		}
		catch (java.util.MissingResourceException mre) {
			SOAP_SERVER_URL = "/servlet/SOAPServer";
			CTILogger.info( "SOAP Server is locally at \"" + SOAP_SERVER_URL + "\"" );
		}
		
	}
	
	private SOAPMessenger getSOAPMessenger() {
		if (soapMsgr == null)
			soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );
		return soapMsgr;
	}

    private StarsOperation sendRecvOperation(StarsOperation operation) {
        try {
            StringWriter sw = new StringWriter();
            operation.marshal( sw );
            String reqStr = XMLUtil.removeXMLDecl( sw.toString() );

            CTILogger.info( "*** Send Message ***  " + reqStr );
            String respStr = getSOAPMessenger().call( reqStr );
            CTILogger.info( "*** Receive Message ***  " + respStr );

            StringReader sr = new StringReader( respStr );
            return StarsOperation.unmarshal( sr );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
    	String referer = req.getHeader( "referer" );
        String action = req.getParameter( "action" );
        if (action == null) action = "";

        HttpSession session = req.getSession(false);
        
		if (action.equalsIgnoreCase("RefreshCache")) {
			ServletUtils.clearECProperties();
			if (isServerLocal()) SOAPServer.refreshCache();
        	if (session != null) session.invalidate();
			resp.sendRedirect( loginURL ); return;
		}
		
        if (session == null && !action.endsWith("Login")) {
        	resp.sendRedirect( loginURL ); return;
        }
        
        if (isServerLocal() && SOAPServer.getInstance() == null) {
        	// SOAPServer not initiated yet, let's wake it up!
        	SOAP_SERVER_URL = req.getRequestURL().toString().replaceFirst( "SOAPClient", "SOAPServer" );
        	//SOAP_SERVER_URL = javax.servlet.http.HttpUtils.getRequestURL( req ).toString().replaceFirst( "SOAPClient", "SOAPServer" );
        	
        	StarsOperation respOper = sendRecvOperation( new StarsOperation() );
        	if (respOper == null)	// This is not good!
        		CTILogger.error( "Cannot initiate SOAPServer, following operations may not function properly!" );
        }
    	
        SOAPMessage reqMsg = null;
        SOAPMessage respMsg = null;

        String nextURL = loginURL;		// The next URL we're going to, operation succeed -> destURL, operation failed -> errorURL
        String destURL = null;			// URL we should go to if action succeed
        String errorURL = null;		// URL we should go to if action failed
        ActionBase clientAction = null;
		
        if (action.equalsIgnoreCase("OperatorLogin")) {
        	MultiAction actions = new MultiAction();
        	actions.addAction( new LoginAction(), req, session );
        	actions.addAction( new GetEnergyCompanySettingsAction(), req, session );

        	clientAction = (ActionBase) actions;
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
        }
        else if (action.equalsIgnoreCase("UserLogin")) {
            MultiAction actions = new MultiAction();
        	actions.addAction( new LoginAction(), req, session );
        	actions.addAction( new GetEnergyCompanySettingsAction(), req, session );
        	actions.addAction( new GetCustAccountAction(), req, session );
        	
        	clientAction = (ActionBase) actions;
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
        }
		else if (action.equalsIgnoreCase("NewCustAccount")) {
			clientAction = new NewCustAccountAction();
			if (Boolean.valueOf( req.getParameter("Wizard") ).booleanValue())
				destURL = "/operator/Consumer/Programs.jsp?Wizard=true";
			else
				destURL = "/operator/Consumer/Update.jsp";
			errorURL = "/operator/Consumer/New.jsp";
		}
		else if (action.equalsIgnoreCase("ProgramSignUp")) {
            MultiAction actions = new MultiAction();
            if (Boolean.valueOf( req.getParameter("SignUpChanged") ).booleanValue())
            	actions.addAction( new ProgramSignUpAction(), req, session );
			if (req.getParameter("Email") != null)
	            actions.addAction( new UpdateControlNotificationAction(), req, session );
	            
			clientAction = (ActionBase) actions;
			if (Boolean.valueOf( req.getParameter("Wizard") ).booleanValue()) {
				destURL = "/operator/Consumer/CreateHardware.jsp?Wizard=true";
				errorURL = "/operator/Consumer/Programs.jsp?Wizard=true";
			}
			else {
	        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
	        	errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
			}
		}
        else if (action.equalsIgnoreCase("SearchCustAccount")) {
            clientAction = new SearchCustAccountAction();
            destURL = "/operator/Consumer/Update.jsp";
            errorURL = "/operator/Consumer/SearchResults.jsp";
        }
        else if (action.equalsIgnoreCase("GetCustAccount")) {
        	clientAction = new GetCustAccountAction();
        	destURL = req.getParameter( ServletUtils.ATT_REDIRECT );
        	errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
        }
        else if (action.equalsIgnoreCase("UpdateCustAccount")) {
            clientAction = new UpdateCustAccountAction();
            destURL = "/operator/Consumer/Update.jsp";
            errorURL = "/operator/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("ReloadCustAccount")) {
        	clientAction = new ReloadCustAccountAction();
            destURL = referer;
            errorURL = referer;
        }
        else if (action.equalsIgnoreCase("DeleteCustAccount")) {
        	clientAction = new DeleteCustAccountAction();
        	destURL = "/operator/Operations.jsp";
            errorURL = "/operator/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("OptOutProgram")) {
        	MultiAction actions = new MultiAction();
        	actions.addAction( new ProgramOptOutAction(), req, session );
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        	
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            StarsExitInterviewQuestions questions = null;
           	if (user != null) {
				StarsGetEnergyCompanySettingsResponse ecSettings = (StarsGetEnergyCompanySettingsResponse)
						user.getAttribute( ServletUtils.ATT_ENERGY_COMPANY_SETTINGS );
				if (ecSettings != null)
            		questions = ecSettings.getStarsExitInterviewQuestions();
           	}
            	
            if (questions != null && questions.getStarsExitInterviewQuestionCount() > 0) {
	        	session.setAttribute( ServletUtils.ATT_OVER_PAGE_ACTION, actions );
            	resp.sendRedirect( destURL );
            	return;
            }
            else {	// if no exit interview questions, then skip the next page and send out the command immediately
	    		actions.addAction( new SendInterviewAnswersAction(), req, session );
	    		clientAction = actions;
	    		destURL = req.getParameter(ServletUtils.ATT_REDIRECT2);
            }
        }
        else if (action.equalsIgnoreCase("SendExitAnswers")) {
        	MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_OVER_PAGE_ACTION );
        	session.removeAttribute( ServletUtils.ATT_OVER_PAGE_ACTION );
        	actions.addAction( new SendInterviewAnswersAction(), req, session );
        	
        	clientAction = actions;
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("ReenableProgram")) {
        	clientAction = new ProgramReenableAction();
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("DisableService") || action.equalsIgnoreCase("EnableService")) {
            clientAction = new YukonSwitchCommandAction();
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("Config")) {
        	MultiAction actions = new MultiAction();
        	if (Boolean.valueOf( req.getParameter("ConfigChanged") ).booleanValue())
        		actions.addAction( new UpdateLMHardwareConfigAction(), req, session );
        	actions.addAction( new YukonSwitchCommandAction(), req, session );
        		
        	clientAction = (ActionBase) actions;
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("GetLMCtrlHist")) {
            clientAction = new GetLMCtrlHistAction();
            session.setAttribute( ServletUtils.ATT_REFERRER, referer );
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            errorURL = referer;
        }
        else if (action.equalsIgnoreCase("GetNextCallNo")) {
        	clientAction = new GetNextCallNumberAction();
        	destURL = "/operator/Consumer/CreateCalls.jsp";
        	errorURL = "/operator/Consumer/CreateCalls.jsp?getCallNo=failed";
        }
        else if (action.equalsIgnoreCase("CreateCall")) {
        	clientAction = new CreateCallAction();
        	destURL = "/operator/Consumer/Calls.jsp";
        	errorURL = "/operator/Consumer/CreateCalls.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateCalls")) {
        	clientAction = new UpdateCallReportAction();
        	destURL = "/operator/Consumer/Calls.jsp";
        	errorURL = "/operator/Consumer/Calls.jsp";
        }
        else if (action.equalsIgnoreCase("GetNextOrderNo")) {
        	clientAction = new GetNextOrderNumberAction();
        	destURL = "/operator/Consumer/Service.jsp";
        	errorURL = "/operator/Consumer/Service.jsp?getOrderNo=failed";
        }
        else if (action.equalsIgnoreCase("CreateOrder")) {
        	clientAction = new CreateServiceRequestAction();
        	destURL = "/operator/Consumer/ServiceSummary.jsp";
        	errorURL = "/operator/Consumer/Service.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateOrders")) {
        	clientAction = new UpdateServiceRequestAction();
        	destURL = "/operator/Consumer/ServiceSummary.jsp";
        	errorURL = "/operator/Consumer/ServiceSummary.jsp";
        }
        else if (action.equalsIgnoreCase("CreateAppliance")) {
        	clientAction = new CreateApplianceAction();
        	destURL = "/operator/Consumer/Appliance.jsp";
        	errorURL = "/operator/Consumer/CreateAppliances.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateAppliance")) {
        	clientAction = new UpdateApplianceAction();
        	destURL = referer;
        	errorURL = referer;
        }
        else if (action.equalsIgnoreCase("DeleteAppliance")) {
        	clientAction = new DeleteApplianceAction();
        	destURL = "/operator/Consumer/Update.jsp";
        	errorURL = req.getHeader( "referer" );
        }
        else if (action.equalsIgnoreCase("CreateLMHardware")) {
        	clientAction = new CreateLMHardwareAction();
        	if (req.getParameter("Wizard") != null) {
        		destURL = "/operator/Consumer/Update.jsp";
        		errorURL = "/operator/Consumer/CreateHardware.jsp?Wizard=true";
        	}
        	else {
	        	destURL = "/operator/Consumer/Inventory.jsp";
	        	errorURL = "/operator/Consumer/CreateHardware.jsp";
        	}
        }
        else if (action.equalsIgnoreCase("UpdateLMHardware")) {
        	MultiAction actions = new MultiAction();
        	actions.addAction( new UpdateLMHardwareAction(), req, session );
        	if (Boolean.valueOf( req.getParameter("ConfigChanged") ).booleanValue())
        		actions.addAction( new UpdateLMHardwareConfigAction(), req, session );
        		
        	clientAction = (ActionBase) actions;
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("DeleteLMHardware")) {
        	clientAction = new DeleteLMHardwareAction();
        	destURL = "/operator/Consumer/Update.jsp";
        	errorURL = req.getHeader( "referer" );
        }
        else if (action.equalsIgnoreCase("UpdateLogin")) {
        	clientAction = new UpdateLoginAction();
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("UpdateThermostatSchedule")) {
        	clientAction = new UpdateThermostatScheduleAction();
        	
        	referer = req.getParameter( ServletUtils.ATT_REFERRER );
        	if (referer == null)	// Request is redirected from servlet UpdateThermostat
        		referer = (String) session.getAttribute( ServletUtils.ATT_REFERRER );
        	destURL = referer;
        	errorURL = referer;
        }
        else if (action.equalsIgnoreCase("UpdateThermostatOption")) {
        	clientAction = new UpdateThermostatManualOptionAction();
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("SendControlOdds")) {
        	clientAction = new SendOddsForControlAction();
        	destURL = "/operator/Consumer/Odds.jsp";
        	errorURL = "/operator/Consumer/Odds.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateResidenceInfo")) {
        	clientAction = new UpdateResidenceInfoAction();
        	destURL = "/operator/Consumer/Residence.jsp";
        	errorURL = "/operator/Consumer/Residence.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateCtrlNotification")) {
        	clientAction = new UpdateControlNotificationAction();
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else {
            CTILogger.info( "SOAPClient: Invalid action type '" + action + "'");
        	session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid action type '" + action + "'");
        	resp.sendRedirect( referer ); return;
        }

        if (clientAction != null) {
        	nextURL = errorURL;
            reqMsg = clientAction.build(req, session);

            if (reqMsg != null) {
            	SOAPUtil.logSOAPMsgForOperation( reqMsg, "*** Send Message *** " );
                respMsg = clientAction.process(reqMsg, session);

                if (respMsg != null) {
                	SOAPUtil.logSOAPMsgForOperation( respMsg, "*** Receive Message *** " );
                	
                	session.removeAttribute( ServletUtils.ATT_REDIRECT );
                	session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
                	int status = clientAction.parse(reqMsg, respMsg, session);
                	if (session.getAttribute( ServletUtils.ATT_REDIRECT ) != null)
                		destURL = (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
                	
                    if (status == 0)	// Operation succeed
                        nextURL = destURL;
                    else if (status == StarsConstants.FAILURE_CODE_SESSION_INVALID)
                    	nextURL = loginURL;
                    else {
                    	setErrorMsg( session, status );
                    	nextURL = errorURL;
                    }
                }
                else
                	setErrorMsg( session, StarsConstants.FAILURE_CODE_RESPONSE_NULL );
            }
            else
            	setErrorMsg( session, StarsConstants.FAILURE_CODE_REQUEST_NULL );
        }

        resp.sendRedirect( nextURL );
    }
    
    private void setErrorMsg(HttpSession session, int status) {
    	if (status == StarsConstants.FAILURE_CODE_RUNTIME_ERROR)
    		session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to process response message" );
    	else if (status == StarsConstants.FAILURE_CODE_REQUEST_NULL)
        	session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to build request message" );
    	else if (status == StarsConstants.FAILURE_CODE_RESPONSE_NULL)
        	session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to receive response message" );
    	else if (status == StarsConstants.FAILURE_CODE_NODE_NOT_FOUND)
    		session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Operation failed: invalid response message" );
    }

}