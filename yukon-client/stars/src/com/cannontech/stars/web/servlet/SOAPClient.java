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
import com.cannontech.stars.web.action.GetLMCtrlHistAction;
import com.cannontech.stars.web.action.GetServiceHistoryAction;
import com.cannontech.stars.web.action.NewCustAccountAction;
import com.cannontech.stars.web.action.OperatorLoginAction;
import com.cannontech.stars.web.action.SearchCustAccountAction;
import com.cannontech.stars.web.action.UpdateCustAccountAction;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPMessenger;
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
        if (session == null) resp.sendRedirect( loginURL );

        SOAPMessage reqMsg = null;
        SOAPMessage respMsg = null;

        String nextURL = homeURL;       // The next URL we're going to, default value is the default error URL
        String destURL = null;			// URL we should go to if action succeed
        String errorURL = null;		// URL we should go to if action failed
        ActionBase clientAction = null;

		if (action.equalsIgnoreCase("NewCustAccount")) {
			clientAction = new NewCustAccountAction();
			destURL = "/OperatorDemos/Consumer/Update.jsp";
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
            destURL = "/OperatorDemos/Consumer/OptOut.jsp";
            nextURL = errorURL = "/OperatorDemos/Consumer/OptOut.jsp";

            PILConnectionServlet connContainer = (PILConnectionServlet)
                    getServletContext().getAttribute(PILConnectionServlet.SERVLET_CONTEXT_ID);
            session.setAttribute(PILConnectionServlet.SERVLET_CONTEXT_ID, connContainer);
        }
        else if (action.equalsIgnoreCase("GetLMCtrlHist")) {
            clientAction = new GetLMCtrlHistAction();
            destURL = "/OperatorDemos/Consumer/ContHist.jsp"
                    + "?AppNo=" + req.getParameter("AppNo")
                    + "&BackURL=" + req.getParameter("BackURL");
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
        else if (action.equalsIgnoreCase("ConsumerSwitchGetLMCtrlHist")) {
            clientAction = new GetLMCtrlHistAction();
            destURL = "/UserDemos/ConsumerSwitch/switch/ContHist.jsp"
                    + "?AppNo=" + req.getParameter("AppNo")
                    + "&BackURL=" + req.getParameter("BackURL");
        }
        else if (action.equalsIgnoreCase("ConsumerStatGetLMCtrlHist")) {
            clientAction = new GetLMCtrlHistAction();
            destURL = "/UserDemos/ConsumerStat/stat/ContHist.jsp"
                    + "?AppNo=" + req.getParameter("AppNo")
                    + "&BackURL=" + req.getParameter("BackURL");
        }
        else if (action.equalsIgnoreCase("OperatorLogin")) {
        	clientAction = new OperatorLoginAction();
        	destURL = homeURL;
        	nextURL = errorURL = loginURL;
        }
        else if (action.equalsIgnoreCase("ConsumerSwitchLogin")) {
            clientAction = new SearchCustAccountAction();
            destURL = "/UserDemos/ConsumerSwitch/switch/ProgramHist.jsp";
            nextURL = errorURL = "/UserDemos/ConsumerSwitch/login.jsp";

            session.setAttribute("ENERGY_COMPANY_ID", new Integer(1));
        }
        else if (action.equalsIgnoreCase("ConsumerStatLogin")) {
            clientAction = new SearchCustAccountAction();
            destURL = "/UserDemos/ConsumerStat/stat/ProgramHist.jsp";
            nextURL = errorURL = "/UserDemos/ConsumerStat/login.jsp";

            session.setAttribute("ENERGY_COMPANY_ID", new Integer(1));
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
                respMsg = clientAction.process(reqMsg, session);

                if (respMsg != null) {
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