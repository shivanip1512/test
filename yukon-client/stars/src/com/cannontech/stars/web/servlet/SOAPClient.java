package com.cannontech.stars.web.servlet;

import java.io.*;
import java.util.*;
import javax.xml.soap.*;
import javax.servlet.http.*;

import com.cannontech.stars.web.action.*;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;
import org.apache.commons.logging.*;
import org.apache.log4j.Logger;
import com.cannontech.servlet.PILConnectionServlet;

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

    SOAPMessenger soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );

    public SOAPClient() {
        super();
    }

    private void log(String str, StarsOperation operation) {
        try {
            Log logger = XMLUtil.getLogger( SOAPClient.class.getName() );

            StringWriter sw = new StringWriter();
            operation.marshal( sw );
            String operStr = XMLUtil.removeXMLDecl( sw.toString() );

            logger.info("SOAPClient: " + str + operStr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

        StarsOperation reqOper = null;
        StarsOperation respOper = null;

        String nextURL = homeURL;       // The next URL we're going to
        String destURL = null;          // URL we should go to if action succeeded
        ActionBase clientAction = null;

        if (action.equalsIgnoreCase("SearchCustAccount")) {
            clientAction = new SearchCustAccountAction();
            destURL = "/OperatorDemos/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("UpdateCustAccount")) {
            clientAction = new UpdateCustAccountAction();
            destURL = "/OperatorDemos/Consumer/Update.jsp";
        }
        else if (action.equalsIgnoreCase("DisableService") || action.equalsIgnoreCase("EnableService")) {
            clientAction = new YukonSwitchCommandAction();
            destURL = "/OperatorDemos/Consumer/Programs.jsp";
            nextURL = "/OperatorDemos/Consumer/Programs.jsp";

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
        else if (action.equalsIgnoreCase("ConsumerSwitchLogin")) {
            clientAction = new SearchCustAccountAction();
            destURL = "/UserDemos/ConsumerSwitch/switch/ProgramHist.jsp";
            nextURL = "/UserDemos/ConsumerSwitch/login.jsp";

            session.setAttribute("ENERGY_COMPANY_ID", new Integer(1));
        }
        else if (action.equalsIgnoreCase("ConsumerStatLogin")) {
            clientAction = new SearchCustAccountAction();
            destURL = "/UserDemos/ConsumerStat/stat/ProgramHist.jsp";
            nextURL = "/UserDemos/ConsumerStat/login.jsp";

            session.setAttribute("ENERGY_COMPANY_ID", new Integer(1));
        }
        else {
            XMLUtil.getLogger( SOAPClient.class ).error( "SOAPClient: Invalid action type: " + action );
        }

        if (clientAction != null) {
            reqOper = clientAction.build(req, session);
            log("### Request Operation ### ", reqOper);

            session.setAttribute("REQUEST_OPERATION", reqOper);
            session.removeAttribute("RESPONSE_OPERATION");

            if (reqOper != null) {
                respOper = clientAction.process(reqOper, session);
                log("### Response Operation ### ", respOper);

                if (respOper != null) {
                    if (clientAction.parse(respOper, session))
                        nextURL = destURL;
                }
            }

            session.removeAttribute("REQUEST_OPERATION");
        }

        resp.sendRedirect( nextURL );
    }
}