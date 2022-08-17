package com.cannontech.tools.xml;

import java.io.IOException;
import java.io.Writer;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class SimpleXmlWriter extends SimpleXmlBase {

    private Writer writer;
    private XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

    public SimpleXmlWriter(Writer writer) {
        this.writer = writer;
        document = new Document();
    }

    /**
     * Writes the document to the writer stream and closes the stream
     */
    public void writeAndClose() throws IOException {
        outputter.output(document, writer);
        writer.close();
    }

    /**
     * Appends a new element to the working element and adds the content
     * Supports multiple elements in string separated by spaces
     * 
     * @param element : String, content : char[]
     * @return current workingElement : Element
     */
    public Element createNewElementWithContent(String element, char[] content) {
        return createNewElement(element).addContent(new String(content));
    }

    /**
     * Appends a new element to the working element and adds the content
     * Supports multiple elements in string separated by spaces
     * 
     * @param element : String, content : String
     * @return current workingElement : Element
     */
    public Element createNewElementWithContent(String element, String content) {
        return createNewElement(element).addContent(content);
    }

    /**
     * Appends a new element to the working element. 
     * Supports multiple elements in string separated by spaces
     * 
     * @param name
     * @return current workingElement : Element
     */
    public Element createNewElement(String name) {
        String[] elements = name.split(" ");
        Element e = null;
        for (String element : elements) {
            e = new Element(element);
            getWorkingElement().addContent(e);
            setWorkingElement(e);
        }
        return e;
    }
    
    /**
     * Sets the root element to the parameter root. Be careful with this
     * as it will remove all other elements added to the previous root element.
     * This really should only be called right as this object is first being set up.
     * 
     * @param root
     * @return
     */
    public Element setRootElement(Element root) {
        document.setRootElement(root);
        return setWorkingElement(root);
    }

}
