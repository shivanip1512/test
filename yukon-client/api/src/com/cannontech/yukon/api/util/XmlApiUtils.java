package com.cannontech.yukon.api.util;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.jdom.Element;
import org.jdom.transform.JDOMSource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Node;

public class XmlApiUtils {

    public static SimpleXPathTemplate getXPathTemplateForNode(Node node) {
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new DOMSource(node));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());
        
        return template;
    }

    public static SimpleXPathTemplate getXPathTemplateForElement(Element element) {
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new JDOMSource(element));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());
        
        return template;
    }
    
    
    public static void addHeaderToMessage(WebServiceMessage message, QName headerElementName, String headerValue, boolean mustUnderstand) {
        
        SoapMessage soapMessage = ((SoapMessage)message);
        SoapHeader soapHeader = soapMessage.getSoapHeader();

        SoapHeaderElement headerElement = soapHeader.addHeaderElement(headerElementName);
        headerElement.setText(headerValue);
        headerElement.setMustUnderstand(mustUnderstand);
    }

}
