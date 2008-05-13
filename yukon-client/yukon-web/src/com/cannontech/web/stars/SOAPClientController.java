package com.cannontech.web.stars;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

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
import com.cannontech.util.ServletUtil;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.stars.service.SwitchContextService;

public class SOAPClientController implements Controller {
    public static final String LOGIN_URL = "/login.jsp";
    public static final String HOME_URL = "/operator/Operations.jsp";
    private SwitchContextService switchContextService;
    
    public void setSwitchContextService(final SwitchContextService switchContextService) {
        this.switchContextService = switchContextService;
    }
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String referer = request.getHeader( "referer" );
        String action = request.getParameter( "action" );
        if (action == null) action = "";

        HttpSession session = request.getSession(false);
        if (session == null) {
            String location = ServletUtil.createSafeUrl(request, LOGIN_URL);
            response.sendRedirect(location);
            return null;
        }
        
        if(referer == null) {
            referer = request.getParameter( ServletUtils.ATT_REFERRER );
        }
        if(referer == null) {
            referer = ((CtiNavObject)session.getAttribute(ServletUtils.NAVIGATE)).getPreviousPage();
        }
        
        
        StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
        if (user == null) {
            String location = ServletUtil.createSafeUrl(request, LOGIN_URL);
            response.sendRedirect(location);
            return null;
        }
        
        if (request.getParameter("SwitchContext") != null && request.getParameter("SwitchContext").length() > 0) {
            try{
                int memberID = Integer.parseInt( request.getParameter("SwitchContext") );
                switchContextService.switchContext(user, request, session, memberID);
                session = request.getSession( false );
            }
            catch (WebClientException e) {
                session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                String location = ServletUtil.createSafeRedirectUrl(request, referer);
                response.sendRedirect(location);
                return null;
            }
        }
        
        session.removeAttribute( ServletUtils.ATT_REDIRECT );
        session.removeAttribute( ServletUtils.ATT_ERROR_MESSAGE );
        session.removeAttribute( ServletUtils.ATT_CONFIRM_MESSAGE );
        
        SOAPMessage reqMsg = null;
        SOAPMessage respMsg = null;
        ActionBase clientAction = null;

        String nextURL = request.getContextPath() + LOGIN_URL;  // The next URL we're going to, operation succeed -> destURL, operation failed -> errorURL
        String destURL = request.getParameter( ServletUtils.ATT_REDIRECT ); // URL we should go to if action succeed
        String errorURL = request.getParameter( ServletUtils.ATT_REFERRER );    // URL we should go to if action failed
        
        // If parameter "ConfirmOnMessagePage" specified, the confirm/error message will be displayed on Message.jsp
        if (request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) != null) {
            session.setAttribute( ServletUtils.ATT_MSG_PAGE_REDIRECT, destURL );
            session.setAttribute( ServletUtils.ATT_MSG_PAGE_REFERRER, errorURL );
            destURL = errorURL = request.getContextPath() +
                    (StarsUtils.isOperator(user.getYukonUser())? "/operator/Admin/Message.jsp" : "/user/ConsumerStat/stat/Message.jsp");
            
            Integer delay = null;
            try {
                delay = Integer.valueOf( request.getParameter(ServletUtils.CONFIRM_ON_MESSAGE_PAGE) );
                destURL += "?delay=" + delay;
            }
            catch (NumberFormatException e) {}
        }
        
        if (action.equalsIgnoreCase("NewCustAccount")) {
            clientAction = new NewCustAccountAction();
            
            if (request.getParameter("Wizard") != null) {
                SOAPMessage msg = clientAction.build( request, session );
                if (msg == null) {
                    String location = ServletUtil.createSafeRedirectUrl(request, errorURL + "?Wizard=true");
                    response.sendRedirect(location);
                    return null;
                }
                
                MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
                if (actions == null) actions = new MultiAction();
                actions.addAction( clientAction, msg );
                session.setAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD, actions );
                
                if (request.getParameter("Submit").equals("Done")) {
                    // Wizard terminated and submitted in the middle
                    destURL = errorURL = request.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
                    session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
                    clientAction = actions;
                }
                else {
                    String location = ServletUtil.createSafeRedirectUrl(request, ServletUtils.ATT_REDIRECT2);
                    response.sendRedirect(location);
                    return null;
                }
            }
        }
        else if (action.equalsIgnoreCase("ProgramSignUp")) {
            clientAction = new ProgramSignUpAction();
            
            String needMoreInfoStr = request.getParameter( ServletUtils.NEED_MORE_INFORMATION );
            boolean needMoreInfo = needMoreInfoStr != null && needMoreInfoStr.equalsIgnoreCase("true");
            
            if (request.getParameter("Wizard") != null) {
                SOAPMessage msg = clientAction.build( request, session );
                if (msg == null) {
                    String location = ServletUtil.createSafeRedirectUrl(request, errorURL + "?Wizard=true");
                    response.sendRedirect(location);
                    return null;
                }
                
                MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
                actions.addAction( clientAction, msg );
                
                if (needMoreInfo) {
                    String location = ServletUtil.createSafeRedirectUrl(request, request.getParameter(ServletUtils.ATT_REDIRECT2) + "?Wizard=true");
                    response.sendRedirect(location);
                    return null;
                }
                
                destURL = errorURL = request.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
                session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
                clientAction = actions;
            }
            else if (needMoreInfo) {
                SOAPMessage msg = clientAction.build( request, session );
                if (msg == null) {
                    String location = ServletUtil.createSafeRedirectUrl(request, errorURL);
                    response.sendRedirect(location);
                    return null;
                }
                
                MultiAction actions = new MultiAction();
                actions.addAction( clientAction, msg );
                session.setAttribute( ServletUtils.ATT_MULTI_ACTIONS, actions );
                
                String location = ServletUtil.createSafeRedirectUrl(request, request.getParameter(ServletUtils.ATT_REDIRECT2));
                response.sendRedirect(location);
                return null;
            }
        }
        else if (action.equalsIgnoreCase("SetAddtEnrollInfo")) {
            clientAction = new ProgramSignUpAction();
            MultiAction actions = (request.getParameter("Wizard") == null)?
                    (MultiAction) session.getAttribute( ServletUtils.ATT_MULTI_ACTIONS ) :
                    (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
            
            try {
                SOAPMessage msg = ProgramSignUpAction.setAdditionalEnrollmentInfo( request, session );
                actions.addAction( clientAction, msg );
            }
            catch (Exception e) {
                if (e instanceof WebClientException)
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                else
                    session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, "Failed to set additional enrollment information" );
                
                String location = ServletUtil.createSafeRedirectUrl(request, errorURL);
                response.sendRedirect(location);
                return null;
            }
            
            clientAction = actions;
            
            if (request.getParameter("Wizard") != null) {
                destURL = errorURL = request.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
                session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
            }
            else {
                session.removeAttribute( ServletUtils.ATT_MULTI_ACTIONS );
            }
        }
        else if (action.equalsIgnoreCase("SearchCustAccount")) {
            clientAction = new SearchCustAccountAction();
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Update.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/SearchResults.jsp";
        }
        else if (action.equalsIgnoreCase("GetCustAccount")) {
            clientAction = new GetCustAccountAction();
        }
        else if (action.equalsIgnoreCase("UpdateCustAccount")) {
            clientAction = new UpdateCustAccountAction();
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Update.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateContacts")) {
            clientAction = new UpdateContactsAction();
            //needed for residential access to contacts
            if(referer.indexOf("/stat/Contacts.jsp") != -1)
            {
                if (destURL == null) destURL = request.getContextPath() + "/user/ConsumerStat/stat/Contacts.jsp";
                if (errorURL == null) errorURL = request.getContextPath() + "/user/ConsumerStat/stat/General.jsp";                  
            }
            else
            {
                if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Contacts.jsp";
                if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/Contacts.jsp";      
            }
        }
        else if (action.equalsIgnoreCase("DeleteCustAccount")) {
            clientAction = new DeleteCustAccountAction();
            if (destURL == null) destURL = request.getContextPath() + "/operator/Operations.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("OptOutProgram")
            || action.equalsIgnoreCase("RepeatLastOptOut")
            || action.equalsIgnoreCase("OverrideLMHardware"))
        {
            clientAction = new ProgramOptOutAction();
            SOAPMessage msg = clientAction.build( request, session );
            if (msg == null) {
                String location = ServletUtil.createSafeRedirectUrl(request, errorURL);
                response.sendRedirect(location);
                return null;
            }
            
            MultiAction actions = new MultiAction();
            actions.addAction( clientAction, msg );
            
            if (request.getParameter(ServletUtils.NEED_MORE_INFORMATION) != null) {
                session.setAttribute( ServletUtils.ATT_MULTI_ACTIONS, actions );
                String location = ServletUtil.createSafeRedirectUrl(request, request.getParameter(ServletUtils.ATT_REDIRECT2));
                response.sendRedirect(location);
                return null;
            }
            
            SendOptOutNotificationAction action2 = new SendOptOutNotificationAction();
            SOAPMessage msg2 = action2.build( request, session );
            if (msg2 == null) {
                String location = ServletUtil.createSafeRedirectUrl(request, errorURL);
                response.sendRedirect(location);
                return null;
            }
            
            actions.addAction( action2, msg2 );
            clientAction = actions;
        }
        else if (action.equalsIgnoreCase("SendOptOutNotification")) {
            clientAction = new SendOptOutNotificationAction();
            SOAPMessage msg = clientAction.build( request, session );
            if (msg == null) {
                String location = ServletUtil.createSafeRedirectUrl(request, errorURL);
                response.sendRedirect(location);
                return null;
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
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Calls.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/CreateCalls.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateCall")) {
            clientAction = new UpdateCallReportAction();
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Calls.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/Calls.jsp";
        }
        else if (action.equalsIgnoreCase("DeleteCall")) {
            clientAction = new DeleteCallReportAction();
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Calls.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/Calls.jsp";
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
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Appliance.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/CreateAppliances.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateAppliance")) {
            clientAction = new UpdateApplianceAction();
        }
        else if (action.equalsIgnoreCase("DeleteAppliance")) {
            clientAction = new DeleteApplianceAction();
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("CreateLMHardware")) {
            clientAction = new CreateLMHardwareAction();
            
            if (request.getParameter("Wizard") != null) {
                SOAPMessage msg = clientAction.build( request, session );
                if (msg == null) {
                    String location = ServletUtil.createSafeRedirectUrl(request, errorURL + "?Wizard=true");
                    response.sendRedirect(location);
                    return null;
                }
                
                MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_NEW_ACCOUNT_WIZARD );
                actions.addAction( clientAction, msg );
                
                if (request.getParameter("Done") == null) {
                    String location = ServletUtil.createSafeRedirectUrl(request, request.getParameter(ServletUtils.ATT_REDIRECT2));
                    response.sendRedirect(location);
                    return null;
                }
                
                // Wizard terminated and submitted in the middle
                destURL = errorURL = request.getContextPath() + "/operator/Consumer/NewFinal.jsp?Wizard=true";
                session.setAttribute( ServletUtils.ATT_REDIRECT, destURL );
                clientAction = actions;
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
                UpdateLoginAction.generatePassword( request, session );
                String location = ServletUtil.createSafeRedirectUrl(request, destURL);
                response.sendRedirect(location);
            }
            catch (WebClientException e) {
                session.setAttribute( ServletUtils.ATT_ERROR_MESSAGE, e.getMessage() );
                String location = ServletUtil.createSafeRedirectUrl(request, errorURL);
                response.sendRedirect(location);
            }
            return null;
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
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Odds.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/Odds.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateResidenceInfo")) {
            clientAction = new UpdateResidenceInfoAction();
            if (destURL == null) destURL = request.getContextPath() + "/operator/Consumer/Residence.jsp";
            if (errorURL == null) errorURL = request.getContextPath() + "/operator/Consumer/Residence.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateCtrlNotification")) {
            clientAction = new UpdateControlNotificationAction();
        }
        else {
            CTILogger.info( "SOAPClient: Invalid action type '" + action + "'");
            session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid action type '" + action + "'");
            String location = ServletUtil.createSafeRedirectUrl(request, referer);
            response.sendRedirect(location);
            return null;
        }
        
        if (destURL == null) destURL = referer;
        if (errorURL == null) errorURL = referer;

        if (clientAction != null) {
            nextURL = errorURL;
            reqMsg = clientAction.build(request, session);

            if (reqMsg != null) {
                SOAPUtil.logSOAPMsgForOperation( reqMsg, "*** Send Message *** " );
                respMsg = clientAction.process(reqMsg, session);

                if (respMsg != null) {
                    SOAPUtil.logSOAPMsgForOperation( respMsg, "*** Receive Message *** " );
                    
                    int status = clientAction.parse(reqMsg, respMsg, session);
                    if (session.getAttribute( ServletUtils.ATT_REDIRECT ) != null)
                        destURL = request.getContextPath() + (String) session.getAttribute( ServletUtils.ATT_REDIRECT );
                    
                    if (status == 0)    // Operation succeed
                        nextURL = destURL;
                    else if (status == StarsConstants.FAILURE_CODE_SESSION_INVALID)
                        nextURL = request.getContextPath() + LOGIN_URL;
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

        String location = ServletUtil.createSafeRedirectUrl(request, nextURL);
        response.sendRedirect(location);
        return null;
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
