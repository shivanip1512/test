package com.cannontech.yukon.api.utils;

import java.io.IOException;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.core.io.Resource;

import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

public class LoadManagementTestUtils {

    private static Namespace ns = YukonXml.getYukonNamespace();
    
    // START-STOP PROGRAM-SCENARIO
    public static Element createStartStopRequestElement(String serviceRequestName,
                                              String nameNodeName,
                                              String nameModeValue,
                                              String startDateTimeStr,
                                              String stopDateTimeStr,
                                              String version,
                                              Resource requestSchemaResource) {
        
        Element requestElement = null;
        Attribute versionAttribute = null;
        Element tmpElement = null;
        
        requestElement = new Element(serviceRequestName, ns);
        versionAttribute = new Attribute("version", version);
        requestElement.setAttribute(versionAttribute);
        
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
        
        // validate request
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        return requestElement;
    }
    
    // OVERRIDE HISTORY BY ACCT NUMBER
    public static Element createOverrideHistoryByAccountNumberRequestElement(String serviceRequestName,
                                              String accountNumber,
                                              String programName,
                                              String startDateTimeStr,
                                              String stopDateTimeStr,
                                              String version,
                                              Resource requestSchemaResource) throws IOException {
        
        Element requestElement = null;
        Attribute versionAttribute = null;
        Element tmpElement = null;
        
        requestElement = new Element(serviceRequestName, ns);
        versionAttribute = new Attribute("version", version);
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("accountNumber", ns, accountNumber);
        requestElement.addContent(tmpElement);
        
        tmpElement = XmlUtils.createStringElement("startDateTime", ns, startDateTimeStr);
        requestElement.addContent(tmpElement);
        
        tmpElement = XmlUtils.createStringElement("stopDateTime", ns, stopDateTimeStr);
        requestElement.addContent(tmpElement);
        
        if (programName != null) {
        	
        	tmpElement = XmlUtils.createStringElement("programName", ns, programName);
            requestElement.addContent(tmpElement);
        }
        
        // validate request
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        return requestElement;
    }
    
}
