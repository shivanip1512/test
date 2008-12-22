package com.cannontech.yukon.api.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMSource;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.core.io.Resource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Node;

public class XmlUtils {

    private static XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
    
    public static List<Integer> convertToIntegerList(List<?> selectNodes) {
        List<Integer> result = new ArrayList<Integer>(selectNodes.size());
        for (Object object : selectNodes) {
            String strValue;
            if (object instanceof Element) {
                Element element = (Element) object;
                strValue = element.getTextTrim();
            } else if (object instanceof Text) {
                Text text = (Text) object;
                strValue = text.getTextTrim();
            } else {
                throw new IllegalArgumentException("Node type must be Element or Text, found: " + object.getClass());
            }
            Integer longValue = Integer.valueOf(strValue);
            result.add(longValue);
        }
        return result;
    }
    
    public static Element createStringElement(String name, Namespace namespace, String value) {
        Element element = new Element(name, namespace);
        
        element.setText(value);
        
        return element;
    }
    
    public static Element createIntegerElement(String name, Namespace namespace, int value) {
        Element element = new Element(name, namespace);
        
        element.setText(Integer.toString(value));
        
        return element;
    }

    public static Element createLongElement(String name, Namespace namespace, long value) {
        Element element = new Element(name, namespace);
        
        element.setText(Long.toString(value));
        
        return element;
    }
    
    /**
     * Creates an element containing a date in ISO format with a UTC zone.
     * Ex: 2008-10-13T12:30:00Z
     * @param name
     * @param namespace
     * @param value
     * @return
     */
    public static Element createDateElement(String name, Namespace namespace, Date value) {
        Element element = new Element(name, namespace);
        
        String dateStr = formatDate(value);
        
        element.setText(dateStr);
        
        return element;
    }
    
    public static Element createDoubleElement(String name, Namespace namespace, double value) {
        Element element = new Element(name, namespace);
        
        String string = Double.toString(value);
        string = string.replace("Infinity", "INF");
        
        element.setText(string);
        
        return element;
    }
    
    public static Element createBooleanElement(String name, Namespace namespace, boolean value) {
        Element element = new Element(name, namespace);
        
        if (value) {
        	element.setText("true");
        } else {
        	element.setText("false");
        }
        
        return element;
    }
    
    /**
     * Returns date formatted as ISO with UTC zone.
     * Ex: 2008-10-13T12:30:00Z
     * @param d
     * @return
     */
    public static String formatDate(Date date) {
        
        DateTime dt = new DateTime(date, DateTimeZone.UTC);
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeNoMillis();
        String dateStr = fmt.print(dt);
        return dateStr;
    }
    
    /**
     * Parses ISO date string into a Date.
     * The ISO string may either contain a UTC zone indicator of "Z"
     * Ex: 2008-10-13T12:30:00Z
     * Or a time zone offset in the format "+|-HH:mm"
     * Ex: 2008-10-13T06:30:00-06:00
     * @param str
     * @return
     */
    public static Date parseDate(String str) {
        
        DateTimeFormatter fmt = ISODateTimeFormat.dateTimeNoMillis();
        DateTime dt = fmt.parseDateTime(str);
        return dt.toDate();
    }
    
    public static Element createElementFromResource(Resource resource) throws JDOMException, IOException {
        
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(resource.getInputStream());
        Element inputElement = document.getRootElement();
        return inputElement;
    }
    
    public static SimpleXPathTemplate getXPathTemplateForElement(Element element) {
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new JDOMSource(element));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());
        
        return template;
    }

    public static SimpleXPathTemplate getXPathTemplateForNode(Node node) {
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new DOMSource(node));
        template.setNamespaces(YukonXml.getYukonNamespaceAsProperties());
        
        return template;
    }
    
    public static void printElement(Element element, String title) throws IOException {
        
        if (!StringUtils.isBlank(title)) {
            System.out.println(title + ":");
        }
        xmlOutputter.output(element, System.out);
        System.out.println();
        System.out.println();
    }
    
    public static void addHeaderToMessage(WebServiceMessage message, QName headerElementName, String headerValue, boolean mustUnderstand) {
    	
    	SoapMessage soapMessage = ((SoapMessage)message);
		SoapHeader soapHeader = soapMessage.getSoapHeader();

		SoapHeaderElement headerElement = soapHeader.addHeaderElement(headerElementName);
		headerElement.setText(headerValue);
		headerElement.setMustUnderstand(mustUnderstand);
    }
    
    @SuppressWarnings("unchecked")
    public static void copySoapHeaders(WebServiceMessage fromMessage, WebServiceMessage toMessage) {
    	
    	// to
        SoapMessage toSoapMessage = ((SoapMessage)toMessage);
		SoapHeader toSoapHeader = toSoapMessage.getSoapHeader();
		
        // from
		SoapMessage fromSoapMessage = ((SoapMessage)fromMessage);
		SoapHeader fromSoapHeader = fromSoapMessage.getSoapHeader();
		Iterator<SoapHeaderElement> fromHeaderElements = fromSoapHeader.examineAllHeaderElements();
		while (fromHeaderElements.hasNext()) {
			
			SoapHeaderElement fromHeaderElement = fromHeaderElements.next();
			
			QName toHeaderElementName = new QName(fromHeaderElement.getName().getNamespaceURI(), fromHeaderElement.getName().getLocalPart(), fromHeaderElement.getName().getPrefix());
			SoapHeaderElement toHeaderElement = toSoapHeader.addHeaderElement(toHeaderElementName);
			toHeaderElement.setText(fromHeaderElement.getText());
			toHeaderElement.setMustUnderstand(fromHeaderElement.getMustUnderstand());
		}
    }
}
