package com.cannontech.yukon.api.utils;

import org.jdom.Element;
import org.jdom.Namespace;

import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

public class LoadManagementTestUtils {

    private static Namespace ns = YukonXml.getYukonNamespace();
    
    public static Element createStartStopRequestElement(String serviceRequestName,
                                              String nameNodeName,
                                              String nameModeValue,
                                              String startDateTimeStr,
                                              String stopDateTimeStr) {
        
        Element requestElement = null;
        Element tmpElement = null;
        
        requestElement = new Element(serviceRequestName, ns);
        tmpElement = XmlUtils.createStringElement(nameNodeName, ns, nameModeValue);
        requestElement.addContent(tmpElement);
        
        if (startDateTimeStr != null) {
            tmpElement = XmlUtils.createStringElement("startDateTime", ns, startDateTimeStr);
            requestElement.addContent(tmpElement);
        }
        
        if (stopDateTimeStr != null) {
            tmpElement = XmlUtils.createStringElement("stopDateTime", ns, stopDateTimeStr);
            requestElement.addContent(tmpElement);
        }
        
        return requestElement;
    }
    
}
