package com.cannontech.stars.xml.util;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.xml.messaging.URLEndpoint;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPMessenger {

    private static SOAPConnectionFactory scf = null;

    private String ENDPOINT_URL = null;

    public SOAPMessenger(String endpointURL) {
        ENDPOINT_URL = endpointURL;
    }

    public void setEndpointURL(String newURL) {
        ENDPOINT_URL = newURL;
    }

    private static SOAPConnectionFactory getConnectionFactory() throws Exception {
        if (scf == null)
            scf = SOAPConnectionFactory.newInstance();
        return scf;
    }

    private void log(String str, SOAPMessage msg) {
        try {
            String msgStr = null;
            if (msg != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                msg.writeTo(bos);
                msgStr = bos.toString();
            }

            CTILogger.debug("SOAPMessenger: " + str + msgStr);
        }
        catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
    }

    public String call(String request) throws Exception {
        SOAPMessage reqMsg = SOAPUtil.buildSOAPMessage(request, "");
        SOAPMessage respMsg = call(reqMsg);

        return SOAPUtil.parseSOAPBody( respMsg );
    }

    public SOAPMessage call(SOAPMessage reqMsg) throws Exception {
        URLEndpoint url = new URLEndpoint( ENDPOINT_URL );
        SOAPConnection conn = getConnectionFactory().createConnection();

        log("### Sent Message ### ", reqMsg);
        SOAPMessage respMsg = conn.call(reqMsg, url);
        log("### Received Message ### ", respMsg);

        return respMsg;
    }

    public static void main(String[] args) {
        try {
            SOAPMessage msg = MessageFactory.newInstance().createMessage();
            SOAPEnvelope env = msg.getSOAPPart().getEnvelope();

            Name hdrName = env.createName("Header-name");
            SOAPHeaderElement hdrElmt = env.getHeader().addHeaderElement( hdrName );
            hdrElmt.addChildElement("Header-child").addTextNode("Header-text");

            Name bodyName = env.createName("Body-name");
            SOAPBodyElement bodyElmt = env.getBody().addBodyElement( bodyName );
            bodyElmt.addChildElement("Body-child").addTextNode("Body-text");

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            msg.writeTo(bos);
            System.out.println( bos.toString() );

            MimeHeaders hdrs = msg.getMimeHeaders();
            Iterator it = hdrs.getAllHeaders();
            while (it.hasNext()) {
                MimeHeader hdr = (MimeHeader) it.next();
                System.out.println( hdr.getName() + ": " + hdr.getValue() );
            }

            System.out.println( "Header: " + SOAPUtil.parseSOAPHeader(msg) );
            System.out.println( "Body: " + SOAPUtil.parseSOAPBody(msg) );
        } catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
    }
}