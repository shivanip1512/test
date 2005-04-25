package com.cannontech.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.ActionBase;
import com.cannontech.stars.web.action.ApplyThermostatScheduleAction;
import com.cannontech.stars.web.action.CreateApplianceAction;
import com.cannontech.stars.web.action.CreateCallAction;
import com.cannontech.stars.web.action.CreateLMHardwareAction;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.DeleteApplianceAction;
import com.cannontech.stars.web.action.DeleteCallReportAction;
import com.cannontech.stars.web.action.DeleteCustAccountAction;
import com.cannontech.stars.web.action.DeleteLMHardwareAction;
import com.cannontech.stars.web.action.DeleteServiceRequestAction;
import com.cannontech.stars.web.action.DeleteThermostatScheduleAction;
import com.cannontech.stars.web.action.GetCustAccountAction;
import com.cannontech.stars.web.action.MultiAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.ProgramOptOutAction;
import com.cannontech.stars.web.action.ProgramReenableAction;
import com.cannontech.stars.web.action.ProgramSignUpAction;
import com.cannontech.stars.web.action.SaveThermostatScheduleAction;
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
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.web.navigation.CtiNavObject;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPClient extends HttpServlet {

	public static final String LOGIN_URL = "/login.jsp";
	public static final String HOME_URL = "/operator/Operations.jsp";

	public SOAPClient() {
		super();
	}

	/**
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		super.init();
	}

	public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
		String referer = req.getHeader( "referer" );
		String action = req.getParameter( "action" );
		if (action == null) action = "";

		HttpSession session = req.getSession(false);
		if (session == null) {
			resp.sendRedirect( req.getContextPath() + LOGIN_URL );
			return;
		}
		
		if(referer == null)
			referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
		
		StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
		if (user == null) {
			resp.sendRedirect( req.getContextPath() + LOGIN_URL );
			return;
		}
		
		if (req.getParameter("SwitchContext") != null) {
			try{
				int memberID = Integer.parseInt( req.getParameter("SwitchContext") );
				StarsAdmin.switchContext( user, req, session, memberID );
				session = req.getSession( false );
			}
			catch (WebClientException e) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
				resp.sendRedirect( referer );
				return;
			}
		}
    	
		session.removeAttribute( ServletUtils.ATT_REDIRECT );
		session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
		session.removeAttribute( ServletUtils.ATT_CONFIRM_MESSAGE );
    	
		SOAPMessage reqMsg = null;
		SOAPMessage respMsg = null;
		ActionBase clientAction = null;

		String nextURL = req.getContextPath() + LOGIN_URL;	// The next URL we're going to, operation succeed -> destURL, operation failed -> errorURL
		String destURL = req.getParameter( ServletUtils.ATT_REDIRECT );	// URL we should go to if action succeed
		String errorURL = req.getParameter( ServletUtils.ATT_REFERRER );	// URL we should go to if action failed
		
		// If parameter "ConfirmOnMessagePage" specified, the confirm/error message will be displayed on Message.jsp
		if (req.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null) {
			session.setAttribute( ServletUtils.ATT_MSG_PAGE_REDIRECT, destURL );
			session.setAttribute( ServletUtils.ATT_MSG_PAGE_REFERRER, errorURL );
			destURL = errorURL = req.getContextPath() +
					(StarsUtils.isOperator(user)? "/operator/Admin/Message.jsp" : "/user/ConsumerStat/stat/Message.jsp");
			
			Integer delay = null;
			try {
				delay = Integer.valueOf( req.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) );
				destURL += "?delay=" + delay;
			}
			catch (NumberFormatException e) {}
		}
		
		if (action.equalsIgnoreCase("NewCustAccount")) {
			clientAction = new NewCustAccountAction();
			
			if (req.getParameter("Wizard") != null) {
				SOAPMessage msg = clientAction.build( req, session );
				if (msg == null) {
					resp.sendRedirect( errorURL + "?Wizard=true" );
					return;
				}
				
				MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
				if (actions == null) actions = new MultiAction();
				actions.addAction( clientAction, msg );
				session.setAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD, actions );
				
				if (req.getParameter("Submit").equals("Done")) {
					// Wizard terminated and submitted in the middle
					destURL = errorURL = req.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
					session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
					clientAction = actions;
				}
				else {
					resp.sendRedirect( req.getParameter(ServletUtils.ATT_REDIRECT2) );
					return;
				}
			}
		}
		else if (action.equalsIgnoreCase("ProgramSignUp")) {
			clientAction = new ProgramSignUpAction();
			
			String needMoreInfoStr = req.getParameter( ServletUtils.NEED_MORE_INFORMATION );
			boolean needMoreInfo = needMoreInfoStr != null && needMoreInfoStr.equalsIgnoreCase("true");
			
			if (req.getParameter("Wizard") != null) {
				SOAPMessage msg = clientAction.build( req, session );
				if (msg == null) {
					resp.sendRedirect( errorURL + "?Wizard=true" );
					return;
				}
				
				MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
				actions.addAction( clientAction, msg );
				
				if (needMoreInfo) {
					resp.sendRedirect( req.getParameter(ServletUtils.ATT_REDIRECT2) + "?Wizard=true" );
					return;
				}
				else {
					destURL = errorURL = req.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
					session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
					clientAction = actions;
				}
			}
			else if (needMoreInfo) {
				SOAPMessage msg = clientAction.build( req, session );
				if (msg == null) {
					resp.sendRedirect( errorURL );
					return;
				}
				
				MultiAction actions = new MultiAction();
				actions.addAction( clientAction, msg );
				session.setAttribute( ServletUtils.ATT_MULTI_ACTIONS, actions );
				
				resp.sendRedirect( req.getParameter(ServletUtils.ATT_REDIRECT2) );
				return;
			}
		}
		else if (action.equalsIgnoreCase("SetAddtEnrollInfo")) {
			clientAction = new ProgramSignUpAction();
			MultiAction actions = (req.getParameter("Wizard") == null)?
					(MultiAction) session.getAttribute( ServletUtils.ATT_MULTI_ACTIONS ) :
					(MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
			
			try {
				SOAPMessage msg = ProgramSignUpAction.setAdditionalEnrollmentInfo( req, session );
				actions.addAction( clientAction, msg );
			}
			catch (Exception e) {
				if (e instanceof WebClientException)
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
				else
					session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to set additional enrollment information" );
				resp.sendRedirect( errorURL );
				return;
			}
			
			clientAction = actions;
			
			if (req.getParameter("Wizard") != null) {
				destURL = errorURL = req.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
				session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
			}
			else {
				session.removeAttribute( ServletUtils.ATT_MULTI_ACTIONS );
			}
		}
		else if (action.equalsIgnoreCase("SearchCustAccount")) {
			clientAction = new SearchCustAccountAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/SearchResults.jsp";
		}
		else if (action.equalsIgnoreCase("GetCustAccount")) {
			clientAction = new GetCustAccountAction();
		}
		else if (action.equalsIgnoreCase("UpdateCustAccount")) {
			clientAction = new UpdateCustAccountAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateContacts")) {
			clientAction = new UpdateContactsAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Contacts.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/Contacts.jsp";
		}
		else if (action.equalsIgnoreCase("DeleteCustAccount")) {
			clientAction = new DeleteCustAccountAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Operations.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
		}
		else if (action.equalsIgnoreCase("OptOutProgram")
			|| action.equalsIgnoreCase("RepeatLastOptOut")
			|| action.equalsIgnoreCase("OverrideLMHardware"))
		{
			clientAction = new ProgramOptOutAction();
			SOAPMessage msg = clientAction.build( req, session );
			if (msg == null) {
				resp.sendRedirect( errorURL );
				return;
			}
			
			MultiAction actions = new MultiAction();
			actions.addAction( clientAction, msg );
        	
			if (req.getParameter(ServletUtils.NEED_MORE_INFORMATION) != null) {
				session.setAttribute( ServletUtils.ATT_MULTI_ACTIONS, actions );
				resp.sendRedirect( req.getParameter(ServletUtils.ATT_REDIRECT2) );
				return;
			}
			
			SendOptOutNotificationAction action2 = new SendOptOutNotificationAction();
			SOAPMessage msg2 = action2.build( req, session );
			if (msg2 == null) {
				resp.sendRedirect( errorURL );
				return;
			}
			
			actions.addAction( action2, msg2 );
			clientAction = actions;
		}
		else if (action.equalsIgnoreCase("SendOptOutNotification")) {
			clientAction = new SendOptOutNotificationAction();
			SOAPMessage msg = clientAction.build( req, session );
			if (msg == null) {
				resp.sendRedirect( errorURL );
				return;
			}
			
			MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_MULTI_ACTIONS );
			actions.addAction( clientAction, msg );
			session.removeAttribute( ServletUtils.ATT_MULTI_ACTIONS );
        	
			clientAction = actions;
		}
		else if (action.equalsIgnoreCase("ReenableProgram") || action.equalsIgnoreCase("CancelScheduledOptOut")) {
			clientAction = new ProgramReenableAction();
		}
		else if (action.equalsIgnoreCase("DisableLMHardware") || action.equalsIgnoreCase("EnableLMHardware")) {
			clientAction = new YukonSwitchCommandAction();
		}
		else if (action.equalsIgnoreCase("UpdateLMHardwareConfig")) {
			clientAction = new UpdateLMHardwareConfigAction();
		}
		else if (action.equalsIgnoreCase("CreateCall")) {
			clientAction = new CreateCallAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/CreateCalls.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateCall")) {
			clientAction = new UpdateCallReportAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
		}
		else if (action.equalsIgnoreCase("DeleteCall")) {
			clientAction = new DeleteCallReportAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/Calls.jsp";
		}
		else if (action.equalsIgnoreCase("CreateWorkOrder")) {
			clientAction = new CreateServiceRequestAction();
			session.setAttribute(ServletUtils.ATT_REDIRECT, destURL);
		}
		else if (action.equalsIgnoreCase("UpdateWorkOrder")) {
			clientAction = new UpdateServiceRequestAction();
		}
		else if (action.equalsIgnoreCase("DeleteWorkOrder")) {
			clientAction = new DeleteServiceRequestAction();
		}
		else if (action.equalsIgnoreCase("CreateAppliance")) {
			clientAction = new CreateApplianceAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Appliance.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/CreateAppliances.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateAppliance")) {
			clientAction = new UpdateApplianceAction();
		}
		else if (action.equalsIgnoreCase("DeleteAppliance")) {
			clientAction = new DeleteApplianceAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Update.jsp";
		}
		else if (action.equalsIgnoreCase("CreateLMHardware")) {
			clientAction = new CreateLMHardwareAction();
			
			if (req.getParameter("Wizard") != null) {
				SOAPMessage msg = clientAction.build( req, session );
				if (msg == null) {
					resp.sendRedirect( errorURL + "?Wizard=true" );
					return;
				}
				
				MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
				actions.addAction( clientAction, msg );
				
				if (req.getParameter("Done") == null) {
					resp.sendRedirect( req.getParameter(ServletUtils.ATT_REDIRECT2) );
					return;
				}
				else {	// Wizard terminated and submitted in the middle
					destURL = errorURL = req.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
					session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
					clientAction = actions;
				}
			}
		}
		else if (action.equalsIgnoreCase("UpdateLMHardware")) {
			clientAction = new UpdateLMHardwareAction();
			session.setAttribute(ServletUtils.ATT_REDIRECT, destURL);
		}
		else if (action.equalsIgnoreCase("DeleteLMHardware")) {
			clientAction = new DeleteLMHardwareAction();
		}
		else if (action.equalsIgnoreCase("UpdateLogin")) {
			clientAction = new UpdateLoginAction();
		}
		else if (action.equalsIgnoreCase("GeneratePassword")) {
			try {
				UpdateLoginAction.generatePassword( req, session );
				resp.sendRedirect( destURL );
			}
			catch (WebClientException e) {
				session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
				resp.sendRedirect( errorURL );
			}
			return;
		}
		else if (action.equalsIgnoreCase("UpdateThermostatSchedule")) {
			clientAction = new UpdateThermostatScheduleAction();
		}
		else if (action.equalsIgnoreCase("UpdateThermostatOption")) {
			clientAction = new UpdateThermostatManualOptionAction();
		}
		else if (action.equalsIgnoreCase("SaveThermostatSchedule")) {
			clientAction = new SaveThermostatScheduleAction();
			if (errorURL == null) errorURL = referer;
		}
		else if (action.equalsIgnoreCase("ApplyThermostatSchedule")) {
			clientAction = new ApplyThermostatScheduleAction();
		}
		else if (action.equalsIgnoreCase("DeleteThermostatSchedule")) {
			clientAction = new DeleteThermostatScheduleAction();
		}
		else if (action.equalsIgnoreCase("SendControlOdds")) {
			clientAction = new SendOddsForControlAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Odds.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/Odds.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateResidenceInfo")) {
			clientAction = new UpdateResidenceInfoAction();
			if (destURL == null) destURL = req.getContextPath() + "/operator/Consumer/Residence.jsp";
			if (errorURL == null) errorURL = req.getContextPath() + "/operator/Consumer/Residence.jsp";
		}
		else if (action.equalsIgnoreCase("UpdateCtrlNotification")) {
			clientAction = new UpdateControlNotificationAction();
		}
		else {
			CTILogger.info( "SOAPClient: Invalid action type '" + action + "'");
			session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid action type '" + action + "'");
			resp.sendRedirect( referer );
			return;
		}
		
		if (destURL == null) destURL = referer;
		if (errorURL == null) errorURL = referer;

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
			else {
				if (session.getAttribute(ServletUtils.ATT_ERROR_MESSAGE) == null)
					setErrorMsg( session, StarsConstants.FAILURE_CODE_REQUEST_NULL );
			}
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