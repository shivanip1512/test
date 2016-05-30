package com.cannontech.notif.outputs;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

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
    
    public void addCollection(String key, Iterable<? extends Object> values) {
        Element parent = new Element(key);
        Element list = new Element("list");
        parent.setContent(list);
        for (Object item : values) {
            list.addContent(new Element("value").setText(item.toString()));
        }
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
