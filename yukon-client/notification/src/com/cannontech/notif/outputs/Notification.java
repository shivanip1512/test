package com.cannontech.notif.outputs;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Notification {
    private String _messageType;
    private Document _doc;
    private Element _root;

    public Notification(String messageType) {
        _messageType = messageType;
        _doc = new Document();
        _root = new Element("notificationmessage");
        _root.setAttribute("type", messageType);
        _doc.setRootElement(_root);
    }

    public boolean containsKey(String key) {
        return _root.getChild(key) != null;
    }

    public String get(String key) {
        return _root.getChildTextTrim(key);
    }

    public void put(String key, String value) {
        _root.addContent(new Element(key).setText(value));
    }

    public void remove(String key) {
        _root.removeChild(key);
    }
    
    public Document getDocument() {
        return _doc;
    }
    
    public String getMessageType() {
        return _messageType;
    }
    
    public String toString() {
        XMLOutputter out = new XMLOutputter(Format.getCompactFormat());
        return out.outputString(_root);
        //return _doc.toString();
    }
}
