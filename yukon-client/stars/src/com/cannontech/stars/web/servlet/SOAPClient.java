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
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.ActionBase;
import com.cannontech.stars.web.action.CreateApplianceAction;
import com.cannontech.stars.web.action.CreateCallAction;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.DeleteApplianceAction;
import com.cannontech.stars.web.action.DeleteCallReportAction;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.DeleteServiceRequestAction;
import com.cannontech.stars.web.action.GetCustAccountAction;
import com.cannontech.stars.web.action.GetEnergyCompanySettingsAction;
import com.cannontech.stars.web.action.GetLMCtrlHistAction;
import com.cannontech.stars.web.action.LoginAction;
import com.cannontech.stars.web.action.MultiAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.ProgramOptOutAction;
import com.cannontech.stars.web.action.ProgramReenableAction;
import com.cannontech.stars.web.action.ProgramSignUpAction;
import com.cannontech.stars.web.action.ReloadCustAccountAction;
import com.cannontech.stars.web.action.SearchCustAccountAction;
import com.cannontech.stars.web.action.SendOddsForControlAction;
import com.cannontech.stars.web.action.SendOptOutNotificationAction;
import com.cannontech.stars.web.action.UpdateApplianceAction;
import com.cannontech.stars.web.action.UpdateCallReportAction;
import com.cannontech.stars.web.action.UpdateContactsAction;
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

	public static final String LOGIN_URL = "/login.jsp";
	public static final String HOME_URL = "/operator/Operations.jsp";

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
		
		// If "stars_soap_server" is not (none), it means SOAPServer is running remotely
		// For now, always consider SOAPServer is running locally
		SOAP_SERVER_URL =
			ClientSession.getInstance().getRolePropertyValue( SystemRole.STARS_SOAP_SERVER );
		
		//keep the URL string null if it is not set
		if( SOAP_SERVER_URL.equals(CtiUtilities.STRING_NONE) )
			SOAP_SERVER_URL = null;


		if (SOAP_SERVER_URL != null) {
			CTILogger.info( "SOAP Server resides remotely at " + SOAP_SERVER_URL );

			StarsOperation respOper = sendRecvOperation( new StarsOperation() );
			if (respOper == null)	// This is not good!
				CTILogger.error( "Cannot connect to SOAPServer, following operations may not function properly!" );
			
			//setServerLocal( false );
			//SOAPServer.setClientLocal( false );
			setServerLocal( true );
			SOAPServer.setClientLocal( true );
			
			soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );
		}
		else {
			setServerLocal( true );
			SOAPServer.setClientLocal( true );
		}
	}
	
	private static SOAPMessenger getSOAPMessenger() {
		if (soapMsgr == null)
			soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );
		return soapMsgr;
	}

	private static StarsOperation sendRecvOperation(StarsOperation operation) {
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
    
	public static void initSOAPServer(HttpServletRequest req) {
		if (isServerLocal() && SOAPServer.getInstance() == null) {
			// SOAPServer not initiated yet, let's wake it up!
			if (SOAP_SERVER_URL == null) {
				String reqURL = req.getRequestURL().toString();
				SOAP_SERVER_URL = reqURL.substring( 0, reqURL.lastIndexOf("/servlet") ) + "/servlet/SOAPServer";
				CTILogger.info( "SOAP Server resides locally at " + SOAP_SERVER_URL );
			}
        	
			StarsOperation respOper = sendRecvOperation( new StarsOperation() );
			if (respOper == null)	// This is not good!
				CTILogger.error( "Cannot initiate SOAPServer, following operations may not function properly!" );
		}
	}

	public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
		String referer = req.getHeader( "referer" );
		String action = req.getParameter( "action" );
		if (action == null) action = "";

		HttpSession session = req.getSession(false);
        
		if (action.equalsIgnoreCase("RefreshCache")) {
			ServletUtils.clear();
			if (isServerLocal()) SOAPServer.refreshCache();
			if (session != null) session.invalidate();
			resp.sendRedirect( req.getContextPath() + LOGIN_URL ); return;
		}
		
		if (session == null && !action.endsWith("Login")) {
			resp.sendRedirect( req.getContextPath() + LOGIN_URL ); return;
		}
    	
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
		session.removeAttribute( ServletUtils.ATT_CONFIRM_MESSAGE );
        
		initSOAPServer( req );
    	
		SOAPMessage reqMsg = null;
		SOAPMessage respMsg = null;

		String nextURL = req.getContextPath() + LOGIN_URL;		// The next URL we're going to, operation succeed -> destURL, operation failed -> errorURL
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
			destURL = req.getContextPath() + "/operator/Consumer/NewFinal.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/New.jsp";
			
			if (req.getParameter("Wizard") != null) {
				MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );;
				if (actions == null) actions = new MultiAction();
				if (!actions.addAction( clientAction, req, session)) {
					resp.sendRedirect( req.getContextPath() + "/operator/Consumer/New.jsp?Wizard=true" );
					return;
				}
				
				session.setAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD, actions );
				resp.sendRedirect( req.getContextPath() + "/operator/Consumer/CreateHardware.jsp?Wizard=true" );
				return;
			}
		}
		else if (action.equalsIgnoreCase("ProgramSignUp")) {
			clientAction = new ProgramSignUpAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
			
			if (req.getParameter("Wizard") != null) {
				MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
				if (!actions.addAction( clientAction, req, session)) {
					resp.sendRedirect( req.getContextPath() + "/operator/Consumer/Programs.jsp?Wizard=true" );
					return;
				}
				
				destURL = errorURL = req.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
				session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
				clientAction = actions;
			}
		}
		else if (action.equalsIgnoreCase("SearchCustAccount")) {
			clientAction = new SearchCustAccountAction();
			destURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/SearchResults.jsp";
		}
		else if (action.equalsIgnoreCase("GetCustAccount")) {
			clientAction = new GetCustAccountAction();
			destURL = req.getParameter( ServletUtils.ATT_REDIRECT );
			errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
		}
		else if (action.equalsIgnoreCase("UpdateCustAccount")) {
			clientAction = new UpdateCustAccountAction();
			destURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateContacts")) {
			clientAction = new UpdateContactsAction();
			destURL = req.getContextPath() + "/operator/Consumer/Contacts.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/Contacts.jsp";
		}
		else if (action.equalsIgnoreCase("ReloadCustAccount")) {
			clientAction = new ReloadCustAccountAction();
			destURL = referer;
			errorURL = referer;
		}
		else if (action.equalsIgnoreCase("DeleteCustAccount")) {
			clientAction = new DeleteCustAccountAction();
			destURL = req.getContextPath() + "/operator/Operations.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
		}
		else if (action.equalsIgnoreCase("OptOutProgram")) {
			MultiAction actions = new MultiAction();
			if (!actions.addAction( new ProgramOptOutAction(), req, session )) {
				resp.sendRedirect( req.getRequestURI() );
				return;
			}
        	
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
				resp.sendRedirect( req.getParameter(ServletUtils.ATT_REDIRECT2) );
				return;
			}
			else {	// if no exit interview questions, then skip the next page and send out the command immediately
				if (!actions.addAction( new SendOptOutNotificationAction(), req, session )) {
					resp.sendRedirect( req.getRequestURI() );
					return;
				}
				
				clientAction = actions;
				destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
				errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
			}
		}
		else if (action.equalsIgnoreCase("SendOptOutNotification")) {
			MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_OVER_PAGE_ACTION );
			if (!actions.addAction( new SendOptOutNotificationAction(), req, session )) {
				resp.sendRedirect( req.getRequestURI() );
				return;
			}
			session.removeAttribute( ServletUtils.ATT_OVER_PAGE_ACTION );
        	
			clientAction = actions;
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("ReenableProgram")) {
			clientAction = new ProgramReenableAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("DisableLMHardware") || action.equalsIgnoreCase("EnableLMHardware")) {
			clientAction = new YukonSwitchCommandAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("UpdateLMHardwareConfig")) {
			MultiAction actions = new MultiAction();
			actions.addAction( new UpdateLMHardwareConfigAction(), req, session );
			actions.addAction( new YukonSwitchCommandAction(), req, session );
        		
			clientAction = (ActionBase) actions;
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("SaveLMHardwareConfig")) {
			clientAction = new UpdateLMHardwareConfigAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("GetLMCtrlHist")) {
			clientAction = new GetLMCtrlHistAction();
			session.setAttribute( ServletUtils.ATT_REFERRER, referer );
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = referer;
		}
		else if (action.equalsIgnoreCase("CreateCall")) {
			clientAction = new CreateCallAction();
			destURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/CreateCalls.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateCall")) {
			clientAction = new UpdateCallReportAction();
			destURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
		}
		else if (action.equalsIgnoreCase("DeleteCall")) {
			clientAction = new DeleteCallReportAction();
			destURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
		}
		else if (action.equalsIgnoreCase("CreateWorkOrder")) {
			clientAction = new CreateServiceRequestAction();
			session.setAttribute(ServletUtils.ATT_REDIRECT, req.getParameter(ServletUtils.ATT_REDIRECT));
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("UpdateWorkOrder")) {
			clientAction = new UpdateServiceRequestAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("DeleteWorkOrder")) {
			clientAction = new DeleteServiceRequestAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("CreateAppliance")) {
			clientAction = new CreateApplianceAction();
			destURL = req.getContextPath() + "/operator/Consumer/Appliance.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/CreateAppliances.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateAppliance")) {
			clientAction = new UpdateApplianceAction();
			destURL = referer;
			errorURL = referer;
		}
		else if (action.equalsIgnoreCase("DeleteAppliance")) {
			clientAction = new DeleteApplianceAction();
			destURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
			errorURL = req.getHeader( "referer" );
		}
		else if (action.equalsIgnoreCase("CreateLMHardware")) {
			clientAction = new CreateLMHardwareAction();
			destURL = req.getContextPath() + "/operator/Consumer/Inventory.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/CreateHardware.jsp";
			
			if (req.getParameter("Wizard") != null) {
				MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
				if (!actions.addAction( clientAction, req, session )) {
					resp.sendRedirect( req.getContextPath() + "/operator/Consumer/CreateHardware.jsp?Wizard=true" );
					return;
				}
				
				resp.sendRedirect( req.getContextPath() + "/operator/Consumer/Programs.jsp?Wizard=true" );
				return;
			}
		}
		else if (action.equalsIgnoreCase("UpdateLMHardware")) {
			clientAction = new UpdateLMHardwareAction();
			session.setAttribute(ServletUtils.ATT_REDIRECT, req.getParameter(ServletUtils.ATT_REDIRECT));
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("DeleteLMHardware")) {
			clientAction = new DeleteLMHardwareAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("UpdateLogin")) {
			clientAction = new UpdateLoginAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("UpdateThermostatSchedule")) {
			clientAction = new UpdateThermostatScheduleAction();
			destURL = req.getParameter( ServletUtils.ATT_REDIRECT );
			errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
		}
		else if (action.equalsIgnoreCase("UpdateThermostatOption")) {
			clientAction = new UpdateThermostatManualOptionAction();
			destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
			errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
		}
		else if (action.equalsIgnoreCase("SendControlOdds")) {
			clientAction = new SendOddsForControlAction();
			destURL = req.getContextPath() + "/operator/Consumer/Odds.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/Odds.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateResidenceInfo")) {
			clientAction = new UpdateResidenceInfoAction();
			destURL = req.getContextPath() + "/operator/Consumer/Residence.jsp";
			errorURL = req.getContextPath() + "/operator/Consumer/Residence.jsp";
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
                	
					int status = clientAction.parse(reqMsg, respMsg, session);
					if (session.getAttribute( ServletUtils.ATT_REDIRECT ) != null)
						destURL = req.getContextPath() + (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
                	
					if (status == 0)	// Operation succeed
						nextURL = destURL;
					else if (status == StarsConstants.FAILURE_CODE_SESSION_INVALID)
						nextURL = req.getContextPath() + LOGIN_URL;
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
    
	public static void setErrorMsg(HttpSession session, int status) {
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