package com.cannontech.common.util.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.springframework.core.io.Resource;

import com.cannontech.common.util.Iso8601DateUtil;

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
        
        String dateStr = Iso8601DateUtil.formatIso8601Date(value);
        
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
    
    public static Element createElementFromResource(Resource resource) throws JDOMException, IOException {
        
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(resource.getInputStream());
        Element inputElement = document.getRootElement();
        return inputElement;
    }
    
    public static void printElement(Element element, String title) throws IOException {
        
        if (!StringUtils.isBlank(title)) {
            System.out.println(title + ":");
        }
        xmlOutputter.output(element, System.out);
        System.out.println();
        System.out.println();
    }
    
    private static TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static void transform(Source source, Result result) throws TransformerException {
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
    }

}
