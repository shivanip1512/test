package com.cannontech.stars.web.servlet;

import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.stars.util.*;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.web.action.*;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsGetExitInterviewQuestionsResponse;
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
    private static boolean serverAtRemote = false;

    private static final String loginURL = "/login.jsp";
    private static final String homeURL = "/operator/Operations.jsp";

    public SOAPClient() {
        super();
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
			
			serverAtRemote = true;
			ServerUtils.setPILConnectionServlet( (com.cannontech.servlet.PILConnectionServlet)
        			getServletContext().getAttribute(com.cannontech.servlet.PILConnectionServlet.SERVLET_CONTEXT_ID) );
		}
		catch (java.util.MissingResourceException mre) {
			SOAP_SERVER_URL = "http://localhost/servlet/SOAPServer";
		}
		soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );
	}

    private StarsOperation sendRecvOperation(StarsOperation operation) {
        try {
            StringWriter sw = new StringWriter();
            operation.marshal( sw );
            String reqStr = XMLUtil.removeXMLDecl( sw.toString() );

            CTILogger.info( "*** Send Message ***  " + reqStr );
            String respStr = soapMsgr.call( reqStr );
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
        
		if (action.equalsIgnoreCase("RefreshCache")) {
			if (!serverAtRemote) SOAPServer.refreshCache();
			resp.sendRedirect( loginURL ); return;
		}

        HttpSession session = req.getSession(false);
        if (session == null && !action.endsWith("Login")) {
        	resp.sendRedirect( loginURL ); return;
        }
    	
        SOAPMessage reqMsg = null;
        SOAPMessage respMsg = null;

        String nextURL = homeURL;       // The next URL we're going to, operation succeed -> destURL, operation failed -> errorURL
        String destURL = null;			// URL we should go to if action succeed
        String errorURL = homeURL;		// URL we should go to if action failed
        
        ActionBase clientAction = null;
		
        if (action.equalsIgnoreCase("OperatorLogin")) {
        	MultiAction actions = new MultiAction();
        	actions.addAction( new LoginAction(), req, session );
        	actions.addAction( new GetEnrollmentProgramsAction(), req, session );
        	actions.addAction( new GetCustSelListsAction(), req, session );

        	clientAction = (ActionBase) actions;
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	nextURL = errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
        }
        else if (action.equalsIgnoreCase("UserLogin")) {
            MultiAction actions = new MultiAction();
        	actions.addAction( new LoginAction(), req, session );
    		actions.addAction( new GetEnrollmentProgramsAction(), req, session );
        	actions.addAction( new GetCustAccountAction(), req, session );
        	
        	clientAction = (ActionBase) actions;
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	nextURL = errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
        }
		else if (action.equalsIgnoreCase("NewCustAccount")) {
			clientAction = new NewCustAccountAction();
			destURL = "/operator/Consumer/Update.jsp";
			nextURL = errorURL = "/operator/Consumer/New.jsp";
		}
		else if (action.equalsIgnoreCase("ProgramSignUp")) {
			clientAction = new ProgramSignUpAction();
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	nextURL = errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
		}
        else if (action.equalsIgnoreCase("SearchCustAccount")) {
            clientAction = new SearchCustAccountAction();
            destURL = "/operator/Consumer/Update.jsp";
            nextURL = errorURL = "/operator/Consumer/SearchResults.jsp";
        }
        else if (action.equalsIgnoreCase("GetCustAccount")) {
        	clientAction = new GetCustAccountAction();
        	destURL = req.getParameter( ServletUtils.ATT_REDIRECT );
        	nextURL = errorURL = req.getParameter( ServletUtils.ATT_REFERRER );
        }
        else if (action.equalsIgnoreCase("UpdateCustAccount")) {
            clientAction = new UpdateCustAccountAction();
            destURL = "/operator/Consumer/Update.jsp";
            nextURL = errorURL = "/operator/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("ReloadCustAccount")) {
        	clientAction = new ReloadCustAccountAction();
            destURL = referer;
            nextURL = errorURL = referer;
        }
        else if (action.equalsIgnoreCase("DeleteCustAccount")) {
        	clientAction = new DeleteCustAccountAction();
        	destURL = "/operator/Operations.jsp";
            nextURL = errorURL = "/operator/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("OptOutProgram")) {
        	MultiAction actions = new MultiAction();
        	actions.addAction( new ProgramOptOutAction(), req, session );
        	session.setAttribute( ServletUtils.ATT_OVER_PAGE_ACTION, actions );
        	
        	//clientAction = new ProgramOptOutAction();
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            nextURL = errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        	
			StarsYukonUser user = (StarsYukonUser) session.getAttribute( ServletUtils.ATT_STARS_YUKON_USER );
            StarsGetExitInterviewQuestionsResponse questions = null;
           	if (user != null)
            	questions = (StarsGetExitInterviewQuestionsResponse) user.getAttribute( ServletUtils.ATT_EXIT_INTERVIEW_QUESTIONS );
            	
            if (questions != null) {
            	if (questions.getStarsExitInterviewQuestionCount() == 0) {
            		actions.addAction( new SendInterviewAnswersAction(), req, session );
            		clientAction = actions;
            		destURL = req.getParameter(ServletUtils.ATT_REDIRECT2);
            	}
            	else {
	            	resp.sendRedirect( destURL );
	            	return;
            	}
            }
            else {
	        	clientAction = new GetInterviewQuestionsAction();
	        	session.setAttribute( ServletUtils.ATT_REFERRER, errorURL );
	        	session.setAttribute( ServletUtils.ATT_REDIRECT2, req.getParameter(ServletUtils.ATT_REDIRECT2) );
            }
        }
        else if (action.equalsIgnoreCase("SendExitAnswers")) {
        	MultiAction actions = (MultiAction) session.getAttribute( ServletUtils.ATT_OVER_PAGE_ACTION );
        	actions.addAction( new SendInterviewAnswersAction(), req, session );
        	clientAction = actions;
        	
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            nextURL = errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("GetExitQuestions")) {
        	clientAction = new GetInterviewQuestionsAction();
        	destURL = (String) session.getAttribute(ServletUtils.ATT_REDIRECT);
            nextURL = errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("ReenableProgram")) {
        	clientAction = new ProgramReenableAction();
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            nextURL = errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("DisableService") || action.equalsIgnoreCase("EnableService") || action.equalsIgnoreCase("Config")) {
            clientAction = new YukonSwitchCommandAction();
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("GetLMCtrlHist")) {
            clientAction = new GetLMCtrlHistAction();
            session.setAttribute( ServletUtils.ATT_REFERRER, referer );
            destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
            nextURL = errorURL = referer;
        }
        else if (action.equalsIgnoreCase("GetNextCallNo")) {
        	clientAction = new GetNextCallNumberAction();
        	destURL = "/operator/Consumer/CreateCalls.jsp";
        	nextURL = errorURL = "/operator/Consumer/CreateCalls.jsp?getCallNo=failed";
        }
        else if (action.equalsIgnoreCase("CreateCall")) {
        	clientAction = new CreateCallAction();
        	destURL = "/operator/Consumer/Calls.jsp";
        	nextURL = errorURL = "/operator/Consumer/CreateCalls.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateCalls")) {
        	clientAction = new UpdateCallReportAction();
        	destURL = "/operator/Consumer/Calls.jsp";
        	nextURL = errorURL = "/operator/Consumer/Calls.jsp";
        }
        else if (action.equalsIgnoreCase("CallTracking")) {
        	clientAction = new CallTrackingAction();
        	destURL = "/operator/Consumer/Calls.jsp";
        }
        else if (action.equalsIgnoreCase("GetNextOrderNo")) {
        	clientAction = new GetNextOrderNumberAction();
        	destURL = "/operator/Consumer/Service.jsp";
        	nextURL = errorURL = "/operator/Consumer/Service.jsp?getOrderNo=failed";
        }
        else if (action.equalsIgnoreCase("CreateOrder")) {
        	clientAction = new CreateServiceRequestAction();
        	destURL = "/operator/Consumer/ServiceSummary.jsp";
        	nextURL = errorURL = "/operator/Consumer/Service.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateOrders")) {
        	clientAction = new UpdateServiceRequestAction();
        	destURL = "/operator/Consumer/ServiceSummary.jsp";
        	nextURL = errorURL = "/operator/Consumer/ServiceSummary.jsp";
        }
        else if (action.equalsIgnoreCase("GetServiceHistory")) {
        	clientAction = new GetServiceHistoryAction();
        	destURL = "/operator/Consumer/ServiceSummary.jsp";
        }
        else if (action.equalsIgnoreCase("GetEnrPrograms")) {
        	clientAction = new GetEnrollmentProgramsAction();
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	nextURL = errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("CreateAppliance")) {
        	clientAction = new CreateApplianceAction();
        	destURL = "/operator/Consumer/Appliance.jsp";
        	nextURL = errorURL = "/operator/Consumer/CreateAppliances.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateAppliance")) {
        	clientAction = new UpdateApplianceAction();
        	destURL = referer;
        	nextURL = errorURL = referer;
        }
        else if (action.equalsIgnoreCase("DeleteAppliance")) {
        	clientAction = new DeleteApplianceAction();
        	destURL = "/operator/Consumer/Update.jsp";
        	nextURL = errorURL = req.getHeader( "referer" );
        }
        else if (action.equalsIgnoreCase("CreateLMHardware")) {
        	clientAction = new CreateLMHardwareAction();
        	destURL = "/operator/Consumer/Inventory.jsp";
        	nextURL = errorURL = "/operator/Consumer/CreateHardware.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateLMHardware")) {
        	clientAction = new UpdateLMHardwareAction();
        	destURL = "/operator/Consumer/Inventory.jsp";
        	nextURL = errorURL = "/operator/Consumer/Inventory.jsp";
        }
        else if (action.equalsIgnoreCase("DeleteLMHardware")) {
        	clientAction = new DeleteLMHardwareAction();
        	destURL = "/operator/Consumer/Update.jsp";
        	nextURL = errorURL = req.getHeader( "referer" );
        }
        else if (action.equalsIgnoreCase("UpdateLogin")) {
        	clientAction = new UpdateLoginAction();
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	nextURL = errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("UpdateThermostatSchedule")) {
        	clientAction = new UpdateThermostatScheduleAction();
        	destURL = req.getParameter(ServletUtils.ATT_REDIRECT);
        	nextURL = errorURL = req.getParameter(ServletUtils.ATT_REFERRER);
        }
        else if (action.equalsIgnoreCase("UpdateThermostatOption")) {
        	clientAction = new UpdateThermostatOptionAction();
        	destURL = "/user/ConsumerStat/stat/Thermostat.jsp";
        	nextURL = errorURL = "/user/ConsumerStat/stat/Thermostat.jsp";
        }
        else {
            CTILogger.info( "SOAPClient: Invalid action type '" + action + "'");
        	session.setAttribute(ServletUtils.ATT_ERROR_MESSAGE, "Invalid action type '" + action + "'");
        	resp.sendRedirect( referer ); return;
        }

        if (clientAction != null) {
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
                    else
                    	nextURL = errorURL;
                }
            }
        }

        resp.sendRedirect( nextURL );
    }

}