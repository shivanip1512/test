package com.cannontech.cbc.oneline.elements;

import java.util.Hashtable;

import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.DrawingElement;
import com.loox.jloox.LxAbstractText;

@SuppressWarnings("serial")
public class HiddenTextElement extends LxAbstractText implements DrawingElement {

    Hashtable<String, String> properties = new Hashtable<String, String>();
    String elementID = "";
    String link = "";
    Drawing drawing = null;
    int version = 0;

    public HiddenTextElement(String id, String name) {
        elementID = id;
        setName(name);
    }

    public Hashtable<String, String> getProperties() {
        return properties;
    }

    public void addProperty(String key, String value) {
        properties.put(key, value);
    }

    public Drawing getDrawing() {
        return drawing;
    }

    public String getElementID() {
        return elementID;
    }

    public String getLinkTo() {
        return link;
    }

    public int getVersion() {
        return version;
    }

    public boolean isCopyable() {
        return false;
    }

    public void setDrawing(Drawing d) {
        drawing = d;
    }

    public void setLinkTo(String linkTo) {
        link = linkTo;
    }

    public void setVersion(int newVer) {
        version = newVer;
    }

}
