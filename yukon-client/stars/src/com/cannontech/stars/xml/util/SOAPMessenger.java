package com.cannontech.stars.xml.util;

import java.io.*;
import java.util.*;
import javax.xml.messaging.*;
import javax.xml.soap.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.logging.*;
import org.apache.log4j.Logger;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPMessenger {

    private static MessageFactory msgFac = null;
    private static SOAPConnectionFactory scf = null;

    private static String SOAP_ENV_PREFIX = "soap-env";
    private static String SOAP_ENV_NSURL = "http://schemas.xmlsoap.org/soap/envelope/";

    private String ENDPOINT_URL = null;

    public SOAPMessenger(String endpointURL) {
        ENDPOINT_URL = endpointURL;
    }

    public void setEndpointURL(String newURL) {
        ENDPOINT_URL = newURL;
    }

    private static MessageFactory getMessageFactory() throws Exception {
        if (msgFac == null)
            msgFac = MessageFactory.newInstance();
        return msgFac;
    }

    private static SOAPConnectionFactory getSOAPConnectionFactory() throws Exception {
        if (scf == null)
            scf = SOAPConnectionFactory.newInstance();
        return scf;
    }

    public static SOAPMessage buildSOAPMessage(String bodyStr, String hdrStr) throws Exception {
        StringBuffer strBuf = new StringBuffer();
        strBuf.append( "<" )
              .append( SOAP_ENV_PREFIX )
              .append( ":Envelope xmlns:" )
              .append( SOAP_ENV_PREFIX )
              .append( "=\"" )
              .append( SOAP_ENV_NSURL )
              .append( "\"><" )
              .append( SOAP_ENV_PREFIX )
              .append( ":Header>" )
              .append( hdrStr )
              .append( "</" )
              .append( SOAP_ENV_PREFIX )
              .append( ":Header><" )
              .append( SOAP_ENV_PREFIX )
              .append( ":Body>" )
              .append( bodyStr )
              .append( "</" )
              .append( SOAP_ENV_PREFIX )
              .append( ":Body></" )
              .append( SOAP_ENV_PREFIX )
              .append( ":Envelope>" );

        String msgStr = strBuf.toString();
        ByteArrayInputStream bis = new ByteArrayInputStream( msgStr.getBytes() );

        MimeHeaders mhs = new MimeHeaders();
        mhs.addHeader("Content-Type", "text/xml; charset=\"utf-8\"");
        mhs.addHeader("Content-Length", String.valueOf( msgStr.length() ));
        mhs.addHeader("SOAPAction", "\"\"");

        return getMessageFactory().createMessage(mhs, bis);
    }

    public static String parseSOAPHeader(SOAPMessage soapMsg) throws Exception {
        String soapEnvPrefix = null;
        String soapEnvNamespace = null;
        String soapHdrHeader = null;
        String soapHdrTrailer = null;

        SOAPEnvelope env = soapMsg.getSOAPPart().getEnvelope();
        Iterator it = env.getNamespacePrefixes();

        if (it.hasNext()) {
            soapEnvPrefix = (String) it.next();
            soapEnvNamespace = env.getNamespaceURI( soapEnvPrefix );
            // replace the default settings
        }

        if (soapEnvPrefix != null) {
            soapHdrHeader = "<" + soapEnvPrefix + ":Header>";
            soapHdrTrailer = "</" + soapEnvPrefix + ":Header>";
        }
        else {
            soapHdrHeader = "<Header>";
            soapHdrTrailer = "</Header>";
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soapMsg.writeTo(bos);
        String msgStr = bos.toString();

        int headerPos = msgStr.indexOf( soapHdrHeader );
        int trailerPos = msgStr.indexOf( soapHdrTrailer );
        if (headerPos >= 0 && trailerPos >= 0)
            return msgStr.substring( headerPos + soapHdrHeader.length(), trailerPos );

        return null;
    }

    public static String parseSOAPBody(SOAPMessage soapMsg) throws Exception {
        String soapEnvPrefix = null;
        String soapEnvNsURI = null;
        String soapBodyHeader = null;
        String soapBodyTrailer = null;

        SOAPEnvelope env = soapMsg.getSOAPPart().getEnvelope();
        Iterator it = env.getNamespacePrefixes();

        if (it.hasNext()) {
            soapEnvPrefix = (String) it.next();
            System.out.println( "Namespace Prefix: " + soapEnvPrefix);
            soapEnvNsURI = env.getNamespaceURI( soapEnvPrefix );
            // replace the default settings
        }

        if (soapEnvPrefix != null) {
            soapBodyHeader = "<" + soapEnvPrefix + ":Body>";
            soapBodyTrailer = "</" + soapEnvPrefix + ":Body>";
        }
        else {
            soapBodyHeader = "<" + SOAP_ENV_PREFIX + ":Body>";
            soapBodyTrailer = "</" + SOAP_ENV_PREFIX + ":Body>";
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soapMsg.writeTo(bos);
        String msgStr = bos.toString();

        int headerPos = msgStr.indexOf( soapBodyHeader );
        int trailerPos = msgStr.indexOf( soapBodyTrailer );
        if (headerPos >= 0 && trailerPos >= 0)
            return msgStr.substring( headerPos + soapBodyHeader.length(), trailerPos );

        return null;
    }

    private void log(String str, SOAPMessage msg) {
        try {
            Log logger = XMLUtil.getLogger( SOAPMessenger.class.getName() );

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            msg.writeTo(bos);
            String msgStr = bos.toString();

            logger.info("SOAPMessenger: " + str + msgStr);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String call(String request) throws Exception {
        SOAPMessage reqMsg = buildSOAPMessage(request, "");

        URLEndpoint url = new URLEndpoint( ENDPOINT_URL );
        SOAPConnection conn = getSOAPConnectionFactory().createConnection();

        log("### Sent Message ### ", reqMsg);
        SOAPMessage respMsg = conn.call(reqMsg, url);
        log("### Received Message ### ", respMsg);

        return parseSOAPBody( respMsg );
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}