package com.cannontech.yukon.api.util;

import javax.xml.namespace.QName;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;


public class XmlApiUtils {

    public static void addHeaderToMessage(WebServiceMessage message, QName headerElementName, String headerValue, boolean mustUnderstand) {
        
        SoapMessage soapMessage = ((SoapMessage)message);
        SoapHeader soapHeader = soapMessage.getSoapHeader();

        SoapHeaderElement headerElement = soapHeader.addHeaderElement(headerElementName);
        headerElement.setText(headerValue);
        headerElement.setMustUnderstand(mustUnderstand);
    }

}
