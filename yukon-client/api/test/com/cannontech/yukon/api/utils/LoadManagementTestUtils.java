package com.cannontech.yukon.api.utils;

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
    
    // CONTROL HISTORY REQUEST
    public static Element createProgramControlHistoryRequestElement(String programName,
                                              String startDateTimeStr,
                                              String stopDateTimeStr,
                                              String version,
                                              Resource requestSchemaResource) {
        
        Element requestElement = null;
        Attribute versionAttribute = null;
        Element tmpElement = null;
        
        requestElement = new Element("programControlHistoryRequest", ns);
        versionAttribute = new Attribute("version", version);
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("programName", ns, programName);
        requestElement.addContent(tmpElement);
        
        tmpElement = XmlUtils.createStringElement("startDateTime", ns, startDateTimeStr);
        requestElement.addContent(tmpElement);
        
        if (stopDateTimeStr != null) {
            tmpElement = XmlUtils.createStringElement("stopDateTime", ns, stopDateTimeStr);
            requestElement.addContent(tmpElement);
        }
        
        // validate request
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        return requestElement;
    }
    
    // OVERRIDE HISTORY BY ACCOUNT REQUEST
    public static Element createOverrideHistoryByAccountRequestElement(
    		String accountNumber,
    		String programName,
    		String startDateTimeStr,
    		String stopDateTimeStr,
    		String version,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	Element tmpElement = null;
    	
    	requestElement = new Element("overrideHistoryByAccountNumberRequest", ns);
    	versionAttribute = new Attribute("version", version);
    	requestElement.setAttribute(versionAttribute);
    	
    	tmpElement = XmlUtils.createStringElement("accountNumber", ns, accountNumber);
    	requestElement.addContent(tmpElement);

    	tmpElement = XmlUtils.createStringElement("startDateTime", ns, startDateTimeStr);
    	requestElement.addContent(tmpElement);
    	
    	if (stopDateTimeStr != null) {
    		tmpElement = XmlUtils.createStringElement("stopDateTime", ns, stopDateTimeStr);
    		requestElement.addContent(tmpElement);
    	}

    	if(programName != null) {
    		tmpElement = XmlUtils.createStringElement("programName", ns, programName);
    		requestElement.addContent(tmpElement);
    	}
    	
    	// validate request
    	TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
    	
    	return requestElement;
    }
    
    // OVERRIDE HISTORY BY PROGRAM REQUEST
    public static Element createOverrideHistoryByProgramRequestElement(
    		String programName,
    		String startDateTimeStr,
    		String stopDateTimeStr,
    		String version,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	Element tmpElement = null;
    	
    	requestElement = new Element("overrideHistoryByProgramNameRequest", ns);
    	versionAttribute = new Attribute("version", version);
    	requestElement.setAttribute(versionAttribute);
    	
		tmpElement = XmlUtils.createStringElement("programName", ns, programName);
		requestElement.addContent(tmpElement);
    	
    	tmpElement = XmlUtils.createStringElement("startDateTime", ns, startDateTimeStr);
    	requestElement.addContent(tmpElement);
    	
    	if (stopDateTimeStr != null) {
    		tmpElement = XmlUtils.createStringElement("stopDateTime", ns, stopDateTimeStr);
    		requestElement.addContent(tmpElement);
    	}
    	
    	// validate request
    	TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
    	
    	return requestElement;
    }
    
}
