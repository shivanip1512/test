package com.cannontech.stars.xml.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.stars.xml.serialize.StarsOperation;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class SOAPUtil {

    private static MessageFactory msgFac = null;

    private static String SOAP_ENV_PREFIX = "soapenv";
    private static String SOAP_ENV_NSURL = "http://schemas.xmlsoap.org/soap/envelope/";

    public SOAPUtil() {
    }

    private static MessageFactory getMessageFactory() throws Exception {
        if (msgFac == null)
            msgFac = MessageFactory.newInstance();
        return msgFac;
    }

    public static SOAPMessage createMessage() throws Exception {
        return getMessageFactory().createMessage();
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
        String soapHdrHeader = null;
        String soapHdrTrailer = null;

        SOAPHeader header = soapMsg.getSOAPPart().getEnvelope().getHeader();
        Name hdrName = header.getElementName();

        if (hdrName.getPrefix() == null || hdrName.getPrefix().length() == 0) {
			soapHdrHeader = "<" + SOAP_ENV_PREFIX + ":" + hdrName.getLocalName();
			soapHdrTrailer = "<" + SOAP_ENV_PREFIX + ":" + hdrName.getLocalName() + ">";
        }
        else {
            soapHdrHeader = "<" + hdrName.getPrefix() + ":" + hdrName.getLocalName();
            soapHdrTrailer = "</" + hdrName.getPrefix() + ":" + hdrName.getLocalName() + ">";
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soapMsg.writeTo(bos);
        String msgStr = bos.toString();

		int hdrPos = msgStr.indexOf( soapHdrHeader );
        int endPos = msgStr.indexOf( soapHdrTrailer );
        if (hdrPos >= 0 && endPos >= 0) {
	        int startPos = msgStr.indexOf(">", hdrPos) + 1;
	        if (startPos > 0)
	            return msgStr.substring( startPos, endPos );
        }

        return null;
    }

    public static String parseSOAPBody(SOAPMessage soapMsg) throws Exception {
        String soapBodyHeader = null;
        String soapBodyTrailer = null;

        SOAPBody body = soapMsg.getSOAPPart().getEnvelope().getBody();
        Name bodyName = body.getElementName();

        if (bodyName.getPrefix() == null || bodyName.getPrefix().length() == 0) {
			soapBodyHeader = "<" + SOAP_ENV_PREFIX + ":" + bodyName.getLocalName();
			soapBodyTrailer = "</" + SOAP_ENV_PREFIX + ":" + bodyName.getLocalName() + ">";
        }
        else {
            soapBodyHeader = "<" + bodyName.getPrefix() + ":" + bodyName.getLocalName();
            soapBodyTrailer = "</" + bodyName.getPrefix() + ":" + bodyName.getLocalName() + ">";
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soapMsg.writeTo(bos);
        String msgStr = bos.toString();

		int hdrPos = msgStr.indexOf( soapBodyHeader );
        int endPos = msgStr.indexOf( soapBodyTrailer );
        if (hdrPos >= 0 && endPos >= 0) {
	        int startPos = msgStr.indexOf(">", hdrPos) + 1;
	        if (startPos > 0)
	            return msgStr.substring( startPos, endPos );
        }

        return null;
    }

    public static String parseSOAPElement(SOAPMessage soapMsg, Name elemName) throws Exception {
        String soapElemHeader = null;
        String soapElemTrailer = null;

        if (elemName.getPrefix() == null || elemName.getPrefix().length() == 0) {
			soapElemHeader = "<" + SOAP_ENV_PREFIX + ":" + elemName.getLocalName();
			soapElemTrailer = "</" + SOAP_ENV_PREFIX + ":" + elemName.getLocalName() + ">";
        }
        else {
            soapElemHeader = "<" + elemName.getPrefix() + ":" + elemName.getLocalName();
            soapElemTrailer = "</" + elemName.getPrefix() + ":" + elemName.getLocalName() + ">";
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        soapMsg.writeTo(bos);
        String msgStr = bos.toString();

        int startPos = msgStr.indexOf( soapElemHeader );
        int endPos = msgStr.indexOf( soapElemTrailer );
        if (startPos >= 0 && endPos >= 0)
            return msgStr.substring( startPos, endPos + soapElemTrailer.length() );

        return null;
    }

    public static SOAPMessage buildSOAPMessage(StarsOperation operation) throws Exception {
        StringWriter sw = new StringWriter();
        operation.marshal( sw );
        String operStr = XMLUtil.removeXMLDecl( sw.toString() );

        return buildSOAPMessage(operStr, "");
    }

    public static StarsOperation parseSOAPMsgForOperation(SOAPMessage message) throws Exception {
        String operStr = parseSOAPBody( message );
        StringReader sr = new StringReader( operStr );

        return StarsOperation.unmarshal(sr);
    }
    
    public static void mergeSOAPMsgOfOperation(SOAPMessage msg1, SOAPMessage msg2) throws Exception {
    	SOAPEnvelope env1 = msg1.getSOAPPart().getEnvelope();
    	Iterator it = env1.getBody().getChildElements();// env1.createName(StarsConstants.STARS_OPERATION) ); SN - Get all child elements and then parse through them.
    	if (it.hasNext()) {
    		SOAPElement operElmt1 = (SOAPElement) it.next();
    		if( operElmt1.getLocalName() == StarsConstants.STARS_OPERATION)
    		{
				SOAPEnvelope env2 = msg2.getSOAPPart().getEnvelope();
				it = env2.getBody().getChildElements( );// env1.createName(StarsConstants.STARS_OPERATION) );SN - Get all child elements and then parse through them.
				if (it.hasNext()) {
					SOAPElement operElmt2 = (SOAPElement) it.next();
					if( operElmt2.getLocalName() == StarsConstants.STARS_OPERATION)
					{
						it = operElmt2.getChildElements();
						while (it.hasNext()) {
							SOAPElement elmt = (SOAPElement) it.next();
							
							// Remove all the existing nodes with the same name first
							Iterator it1 = operElmt1.getChildElements( elmt.getElementName() );
							while (it1.hasNext()) {
								SOAPElement elmt1 = (SOAPElement) it1.next();
								elmt1.detachNode();
							}
							
							operElmt1.addChildElement( elmt );
						}
					}
				}
    		}
    	}
    }
    
    public static void logSOAPMsgForOperation(SOAPMessage msg, String leading) {
    	try {
	    	StarsOperation operation = parseSOAPMsgForOperation( msg );
	    	StringWriter sw = new StringWriter();
	    	operation.marshal( sw );
	    	CTILogger.debug( leading + sw.toString() );
    	}
    	catch (Exception e) {
    		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
    	}
    }
    
    public static void addSOAPHeader(SOAPMessage msg, String hdrField) throws Exception {
    	SOAPHeader hdr = msg.getSOAPPart().getEnvelope().getHeader();
    	hdr.addChildElement( hdrField );
    }
}