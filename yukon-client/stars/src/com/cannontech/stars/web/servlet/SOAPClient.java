package com.cannontech.stars.web.servlet;

import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.servlet.PILConnectionServlet;
import com.cannontech.stars.web.action.ActionBase;
import com.cannontech.stars.web.action.CallTrackingAction;
import com.cannontech.stars.web.action.CreateCallAction;
import com.cannontech.stars.web.action.CreateServiceRequestAction;
import com.cannontech.stars.web.action.GetCustAccountAction;
import com.cannontech.stars.web.action.GetEnrollmentProgramsAction;
import com.cannontech.stars.web.action.GetLMCtrlHistAction;
import com.cannontech.stars.web.action.GetServiceHistoryAction;
import com.cannontech.stars.web.action.LoginAction;
import com.cannontech.stars.web.action.MultiAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.ProgramSignUpAction;
import com.cannontech.stars.web.action.SearchCustAccountAction;
import com.cannontech.stars.web.action.UpdateCustAccountAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
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

    private static final String SOAP_SERVER_URL = "http://operations.cannontech.com/scripts/jrun.dll/servlet/SOAPServer";

    private static final String loginURL = "/login.jsp";
    private static final String homeURL = "/OperatorDemos/Operations.jsp";

    private SOAPMessenger soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );

    public SOAPClient() {
        super();
    }

    private StarsOperation sendRecvOperation(StarsOperation operation) {
        try {
            StringWriter sw = new StringWriter();
            operation.marshal( sw );
            String reqStr = XMLUtil.removeXMLDecl( sw.toString() );

            log( "*** Sent Message ***  " + reqStr );
            String respStr = soapMsgr.call( reqStr );
            log( "*** Received Message ***  " + respStr );

            StringReader sr = new StringReader( respStr );
            return StarsOperation.unmarshal( sr );
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        String action = req.getParameter( "action" );
        if (action == null) return;

        HttpSession session = req.getSession(false);
        if (session == null && !action.endsWith("Login")) {
        	resp.sendRedirect( loginURL ); return;
        }

        SOAPMessage reqMsg = null;
        SOAPMessage respMsg = null;

        String nextURL = homeURL;       // The next URL we're going to, operation succeed -> destURL, operation failed -> errorURL
        String destURL = null;			// URL we should go to if action succeed
        String errorURL = null;		// URL we should go to if action failed
        ActionBase clientAction = null;

		if (action.equalsIgnoreCase("NewCustAccount")) {
			clientAction = new NewCustAccountAction();
			destURL = "/OperatorDemos/Consumer/Update.jsp";
		}
		else if (action.equalsIgnoreCase("ProgramSignUp")) {
			clientAction = new ProgramSignUpAction();
        	destURL = req.getParameter("REDIRECT");
        	nextURL = errorURL = req.getParameter( "REFERRER" );
		}
        else if (action.equalsIgnoreCase("SearchCustAccount")) {
            clientAction = new SearchCustAccountAction();
            destURL = "/OperatorDemos/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateCustAccount")) {
            clientAction = new UpdateCustAccountAction();
            destURL = "/OperatorDemos/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("DisableService") || action.equalsIgnoreCase("EnableService")) {
            clientAction = new YukonSwitchCommandAction();
            destURL = req.getParameter("REDIRECT");
            nextURL = errorURL = req.getParameter("REFERRER");

            PILConnectionServlet connContainer = (PILConnectionServlet)
                    getServletContext().getAttribute(PILConnectionServlet.SERVLET_CONTEXT_ID);
            session.setAttribute(PILConnectionServlet.SERVLET_CONTEXT_ID, connContainer);
        }
        else if (action.equalsIgnoreCase("GetLMCtrlHist")) {
            clientAction = new GetLMCtrlHistAction();
            destURL = req.getParameter("REDIRECT")
            		+ "?AppNo=" + req.getParameter("AppNo")
                    + "&REFERRER=" + req.getParameter("REFERRER");
        }
        else if (action.equalsIgnoreCase("CreateCall")) {
        	clientAction = new CreateCallAction();
        	destURL = "/OperatorDemos/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("CallTracking")) {
        	clientAction = new CallTrackingAction();
        	destURL = "/OperatorDemos/Consumer/Calls.jsp";
        }
        else if (action.equalsIgnoreCase("CreateOrder")) {
        	clientAction = new CreateServiceRequestAction();
        	destURL = "/OperatorDemos/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("GetServiceHistory")) {
        	clientAction = new GetServiceHistoryAction();
        	destURL = "/OperatorDemos/Consumer/ServiceSummary.jsp";
        }
        else if (action.equalsIgnoreCase("GetEnrPrograms")) {
        	clientAction = new GetEnrollmentProgramsAction();
        	destURL = req.getParameter("REDIRECT");
        	nextURL = errorURL = req.getParameter("REFERRER");
        }
        else if (action.equalsIgnoreCase("OperatorLogin")) {
        	session.removeAttribute("OPERATOR");
        	session.removeAttribute("USER");
        	
        	MultiAction actions = new MultiAction();
        	actions.getActionVector().addElement( new LoginAction() );
        	actions.getActionVector().addElement( new GetEnrollmentProgramsAction() );

        	clientAction = (ActionBase) actions;
        	destURL = req.getParameter("REDIRECT");
        	nextURL = errorURL = loginURL;
        }
        else if (action.equalsIgnoreCase("UserLogin")) {
        	session.removeAttribute("OPERATOR");
        	session.removeAttribute("USER");
        	
            MultiAction actions = new MultiAction();
        	actions.getActionVector().addElement( new LoginAction() );
        	actions.getActionVector().addElement( new GetCustAccountAction() );
        	if (session.getAttribute("ENROLLMENT_PROGRAMS") == null)
        		actions.getActionVector().addElement( new GetEnrollmentProgramsAction() );
        	
        	clientAction = (ActionBase) actions;
        	destURL = req.getParameter("REDIRECT");
        	nextURL = errorURL = req.getParameter( "REFERRER" );
        }
        else if (action.equalsIgnoreCase("HoneywellSearchCustAccount")) {
            clientAction = new com.cannontech.stars.honeywell.action.SearchCustAccountAction();
            destURL = "/OperatorDemos/Consumer/Update.jsp";
        }
        else {
            XMLUtil.getLogger( SOAPClient.class ).error( "SOAPClient: Invalid action type: " + action );
        }

        if (clientAction != null) {
            reqMsg = clientAction.build(req, session);

            if (reqMsg != null) {
            	SOAPUtil.logSOAPMsgForOperation( reqMsg, "*** Send Message *** " );
                respMsg = clientAction.process(reqMsg, session);

                if (respMsg != null) {
                	SOAPUtil.logSOAPMsgForOperation( respMsg, "*** Receive Message *** " );
                	int status = clientAction.parse(reqMsg, respMsg, session);
                	
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