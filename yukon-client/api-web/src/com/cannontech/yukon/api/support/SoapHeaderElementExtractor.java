package com.cannontech.yukon.api.support;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

public class SoapHeaderElementExtractor {

	/**
	 * Given the local part of a header element name, find the associated header value.
	 * Returns null if no header element with named elementName if found.
	 * @param messageContext
	 * @param elementName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String findElementValue(MessageContext messageContext, String elementName) {
		
		String elementValue = null;
		WebServiceMessage webServiceMessage = messageContext.getRequest();
		SoapMessage soapMessage = ((SoapMessage)webServiceMessage);
		SoapHeader soapHeader = soapMessage.getSoapHeader();
		Iterator<SoapHeaderElement> headerElements = soapHeader.examineAllHeaderElements();
		while (headerElements.hasNext()) {
			
			SoapHeaderElement headerElement = headerElements.next();
			QName headerName = headerElement.getName();
			if (headerName.getLocalPart().equals(elementName)) {
				
				elementValue = headerElement.getText();
			}
		}
		
		return elementValue;
	}
}
