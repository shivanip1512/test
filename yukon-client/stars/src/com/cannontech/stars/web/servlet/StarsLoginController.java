package com.cannontech.stars.web.servlet;

import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.servlet.LoginController;
import com.cannontech.stars.xml.serialize.StarsLogin;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.util.SOAPMessenger;
import com.cannontech.stars.xml.util.XMLUtil;

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
        StarsLogin login = new StarsLogin();
        login.setUsername( req.getParameter("USERNAME") );
        login.setPassword( req.getParameter("PASSWORD") );
        reqOper.setStarsLogin( login );

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