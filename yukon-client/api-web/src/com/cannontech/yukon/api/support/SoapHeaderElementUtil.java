package com.cannontech.yukon.api.support;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapHeaderException;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.TransformerObjectSupport;

import com.cannontech.common.util.xml.XmlUtils;

public class SoapHeaderElementUtil extends TransformerObjectSupport {

	/**
	 * Given the local part of a header element name, find the associated header value.
	 * Returns null if no header element with named elementName if found.
	 * @param messageContext
	 * @param desiredHeader
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static SoapHeaderElement findElement(WebServiceMessage webServiceMessage, QName desiredHeader) {
		SoapMessage soapMessage = ((SoapMessage)webServiceMessage);
		SoapHeader soapHeader = soapMessage.getSoapHeader();
		Iterator<SoapHeaderElement> headerElements = soapHeader.examineAllHeaderElements();
		while (headerElements.hasNext()) {
			
			SoapHeaderElement headerElement = headerElements.next();
			QName headerName = headerElement.getName();
			if (headerName.equals(desiredHeader)) {
				return headerElement;
			}
		}
		
		return null;
	}
	
	public static String findElementValue(WebServiceMessage webServiceMessage, QName elementName) {
		SoapHeaderElement headerElement = findElement(webServiceMessage, elementName);
		if (headerElement == null) {
			return null;
		}
		String value = headerElement.getText();
		return value;
	}
	
    
    public static void copySoapHeaderFromRequestToResponse(MessageContext messageContext, QName headerName) throws SoapHeaderException, TransformerException {
    	
        // copy the soap header to response
        SoapMessage responseSoapMessage = ((SoapMessage)messageContext.getResponse());
		SoapHeader responseSoapHeader = responseSoapMessage.getSoapHeader();
		
		SoapHeaderElement matchingRequestElement = findElement(messageContext.getRequest(), headerName);
		if (matchingRequestElement != null) {
			XmlUtils.transform(matchingRequestElement.getSource(), responseSoapHeader.getResult());
		}
    }
	
}
