package com.cannontech.tools.xml;

import org.jdom.Document;
import org.jdom.Element;

public class SimpleXmlBase {
    
    private Element workingElement;
    protected Document document;

    /**
     * Navigates down the chain of elements specified by parameter names
     * The final element is returned and the workingElement is changed to match
     * If the element isn't found, the workingElement is left unchanged and null is returned
     * 
     * @param names
     * @return workingElement : Element
     */
    public Element navigateTo(String[] names) {
        Element destElement = null;
        Element sourceElement = workingElement;
        for (String name : names) {
            destElement = setWorkingElement(getWorkingElement().getChild(name));
            if (destElement == null) {
                // We can go no further
                // Reset the working element back to sourceElement started with
                // Return null. Note: it is the callers responsibility to check for null
                setWorkingElement(sourceElement);
                break;
            }
        }
        return destElement;
    }
    
    /**
     * Returns the workingElement
     * 
     * @return workingElement : Element
     */
    public Element getWorkingElement() {
        if (workingElement == null) {
            return document.getRootElement();
        }
        return workingElement;
    }
    
    /**
     * Sets and returns the workingElement
     * 
     * @param workingElement
     * @return workingElement : Element
     */
    public Element setWorkingElement(Element workingElement) {
        this.workingElement = workingElement;
        return workingElement;
    }
    
    /**
     * Sets and returns root as the workingElement
     * 
     * @param workingElement
     * @return workingElement : Element
     */
    public Element setWorkingElementRoot() {
        return setWorkingElement(document.getRootElement());
    }
    
    /**
     * Returns the root element of the document
     */
    public Element getRootElement() {
        return document.getRootElement();
    }
}
