package com.cannontech.stars.web.servlet;

import com.cannontech.servlet.LoginController;

import java.io.*;
import javax.servlet.http.*;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class StarsLoginController extends LoginController {

    private static final String SOAP_SERVER_URL = "http://operations.cannontech.com/scripts/jrun.dll/servlet/SOAPServer";

	private static final String loginURL = "/login.jsp";

    SOAPMessenger soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );

    public StarsLoginController() {
        super();
    }

    public void service(HttpServletRequest req, HttpServletResponse resp) throws javax.servlet.ServletException, java.io.IOException {
        StarsOperation reqOper = new StarsOperation();
        StarsOperatorLogin login = new StarsOperatorLogin();
        login.setUsername( req.getParameter("USERNAME") );
        login.setPassword( req.getParameter("PASSWORD") );
        login.setDbAlias( req.getParameter("DATABASEALIAS") );
        reqOper.setStarsOperatorLogin( login );

        try {
            StringWriter sw = new StringWriter();
            reqOper.marshal( sw );
            String reqStr = XMLUtil.removeXMLDecl( sw.toString() );

            log( "*** Sent Message ***  " + reqStr );
            String respStr = soapMsgr.call( reqStr );
            log( "*** Received Message ***  " + respStr );

            StringReader sr = new StringReader( respStr );
            StarsOperation respOper = StarsOperation.unmarshal( sr );

            if (respOper.getStarsSuccess() != null) {
                HttpSession session = req.getSession(true);
                super.service(req, resp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        resp.sendRedirect( loginURL );
    }
}