package com.cannontech.stars.server.servlet;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.messaging.JAXMServlet;
import javax.xml.messaging.ReqRespListener;
import javax.xml.soap.SOAPMessage;

import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsSuccess;
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

public class SOAPServer extends JAXMServlet implements ReqRespListener {

    public SOAPServer() {
        super();
    }

    public SOAPMessage onMessage(SOAPMessage message) {
        StarsOperation respOper = new StarsOperation();

        try {
            String reqStr = SOAPUtil.parseSOAPBody( message );
            StringReader sr = new StringReader( reqStr );
            StarsOperation reqOper = StarsOperation.unmarshal( sr );

            if (reqOper.getStarsOperatorLogin() != null) {
                StarsSuccess success = new StarsSuccess();
                success.setDescription( "User login successful" );
                respOper.setStarsSuccess( success );
            }
            else {
                StarsSuccess success = new StarsSuccess();
                success.setDescription( "Operation successful" );
                respOper.setStarsSuccess( success );
            }
        }
        catch (Exception e) {
            e.printStackTrace();

            StarsFailure failure = new StarsFailure();
            failure.setStatusCode( StarsConstants.FAILURE_CODE_OPERATION_FAILED );
            failure.setDescription( e.getMessage() );
            respOper.setStarsFailure( failure );
        }

        try {
            StringWriter sw = new StringWriter();
            respOper.marshal( sw );
            String respStr = XMLUtil.removeXMLDecl( sw.toString() );
            return SOAPUtil.buildSOAPMessage(respStr, "");
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }

        return null;
    }
}