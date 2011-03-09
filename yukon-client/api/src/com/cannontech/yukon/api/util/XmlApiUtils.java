package com.cannontech.yukon.api.util;

import javax.xml.transform.dom.DOMSource;

import org.jdom.Element;
import org.jdom.transform.JDOMSource;
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

}
