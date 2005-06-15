package com.cannontech.notif.outputs;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Notification {
    private String _messageType;
    private Document _doc;
    private Element _mainElement;

    public Notification(String messageType) {
        _messageType = messageType;
        _doc = new Document();
        Element root = new Element("notificationmessage");
        _mainElement = new Element(messageType);
        root.addContent(_mainElement);
        _doc.setRootElement(root);
    }

    public void addData(String key, String value) {
        _mainElement.addContent(new Element(key).setText(value));
    }
    
    public void addAttribute(String key, String value) {
        _mainElement.setAttribute(key, value);
    }

    public Document getDocument() {
        return _doc;
    }
    
    public String getMessageType() {
        return _messageType;
    }
    
    public String toString() {
        return _messageType + " notification";
    }
    
    public int hashCode() {
        return _doc.getContent().hashCode();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Notification) {
            Notification that = (Notification) obj;
            return this._doc.getContent().equals(that._doc.getContent());
        }
        return false;
    }
    
    public String getXmlString() {
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        return out.outputString(_doc);
    }
    
}
