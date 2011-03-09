package com.cannontech.yukon.api.util;

import java.util.Properties;

import javax.xml.transform.dom.DOMSource;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.transform.JDOMSource;
import org.w3c.dom.Node;

import com.cannontech.common.util.xml.SimpleXPathTemplate;

public class YukonXml {
    private static final Namespace yukonNamespace = Namespace.getNamespace("y", "http://yukon.cannontech.com/api");
    private static final Namespace yukonNamespaceForDefault = Namespace.getNamespace("http://yukon.cannontech.com/api");
    private static Properties properties = new Properties();
    static {
        properties.put(yukonNamespace.getPrefix(), yukonNamespace.getURI());
    }
    
    public static Namespace getYukonNamespace() {
        return yukonNamespace;
    }
    
    public static Namespace getYukonNamespaceForDefault() {
        return yukonNamespaceForDefault;
    }
    
    public static Properties getYukonNamespaceAsProperties() {
        return properties;
    }
    
    public static Element createRequestElement(String requestName) {
        
        Element requestElement = new Element(requestName, getYukonNamespace());
        
        return requestElement;
    }

    public static SimpleXPathTemplate getXPathTemplateForNode(Node node) {
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new DOMSource(node));
        template.setNamespaces(getYukonNamespaceAsProperties());
        
        return template;
    }

    public static SimpleXPathTemplate getXPathTemplateForElement(Element element) {
        
        SimpleXPathTemplate template = new SimpleXPathTemplate();
        template.setContext(new JDOMSource(element));
        template.setNamespaces(getYukonNamespaceAsProperties());
        
        return template;
    }
}
