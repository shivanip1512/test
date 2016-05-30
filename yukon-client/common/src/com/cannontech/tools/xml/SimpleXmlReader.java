package com.cannontech.tools.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class SimpleXmlReader extends SimpleXmlBase {

    private SAXBuilder builder = new SAXBuilder();

    public SimpleXmlReader(File xmlFile) throws JDOMException, IOException {
        this.document = (Document) builder.build(xmlFile);
    }
    
    public SimpleXmlReader(InputStream xmlFile) throws JDOMException, IOException {
        this.document = (Document) builder.build(xmlFile);
    }

    /**
     * Returns the element specified by name parameter. Will return null
     * if no element is found. If this element exists it will modify the working 
     * element to the element returned.
     * 
     * Supports multiple elements in string separated by spaces
     *
     * @param name
     * @return
     */
    public Element getElement(String name) {
        return navigateTo(name.split(" "));
    }

    /**
     * Returns the value found inside the element specified by name parameter. Will
     * return null if no element is found. Changes the current working element to 
     * the element returned.
     * 
     * Supports multiple elements in string separated by spaces
     * 
     * @param name
     * @return
     */
    public String getElementValue(String name) {
        Element element = getElement(name);
        if (element == null) {
            return null;
        } else {
            return element.getText();
        }
    }
}
