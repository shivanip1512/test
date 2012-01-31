package com.cannontech.common.util.xml;

import java.util.Properties;

import javax.xml.transform.dom.DOMSource;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.transform.JDOMSource;
import org.w3c.dom.Node;


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

    public static YukonXPathTemplate getXPathTemplateForNode(Node node) {
        
        YukonXPathTemplate template = new YukonXPathTemplate();
        template.setContext(new DOMSource(node));
        template.setNamespaces(getYukonNamespaceAsProperties());
        
        return template;
    }

    public static YukonXPathTemplate getXPathTemplateForElement(Element element) {
        
        YukonXPathTemplate template = new YukonXPathTemplate();
        template.setContext(new JDOMSource(element));
        template.setNamespaces(getYukonNamespaceAsProperties());
        
        return template;
    }
}
