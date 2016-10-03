package com.cannontech.yukon.api.utils;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.core.io.Resource;

import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;

public class LoadManagementTestUtils {

    private static Namespace ns = YukonXml.getYukonNamespace();
    
    // START-STOP PROGRAM-SCENARIO
    public static Element createStartStopRequestElement(String serviceRequestName,
                                              String nameNodeName,
                                              String nameModeValue,
                                              String startDateTimeStr,
                                              String stopDateTimeStr,
                                              String gearName,
                                              String version,
                                              Boolean scenarioWaitForResponse,
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
        
        if (gearName != null) {
            tmpElement = XmlUtils.createStringElement("gearName", ns, gearName);
            requestElement.addContent(tmpElement);
        }
        
        if (serviceRequestName.equals("scenarioStartRequest") || serviceRequestName.equals("scenarioStopRequest")) {
        	if (scenarioWaitForResponse == null) {
        		// don't include tag at all, should be consider a false in endpoint
        	} else if (scenarioWaitForResponse.booleanValue() == true) {
        		tmpElement = XmlUtils.createStringElement("waitForResponse", ns, "true");
                requestElement.addContent(tmpElement);
        	} else if (scenarioWaitForResponse.booleanValue() == false) {
        		tmpElement = XmlUtils.createStringElement("waitForResponse", ns, "false");
                requestElement.addContent(tmpElement);
        	}
        }
        
        // validate request
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        return requestElement;
    }
    
    // CREATE PROGRAM BY STATUS
    public static Element createProgramsByStatus(String serviceRequestName, List<String> programStatuses,
            String version, Resource requestSchemaResource) {

        Element requestElement = new Element(serviceRequestName, ns);
        Attribute versionAttribute = new Attribute("version", version);
        requestElement.setAttribute(versionAttribute);
        for (String programStatus : programStatuses) {
            Element tmpElement = XmlUtils.createStringElement("programStatus", ns, programStatus);
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
    
 // OVERRIDEN DEVICES BY ACCOUNT REQUEST
    public static Element createOverridenDevicesByAccountRequestElement(
    		String accountNumber,
    		String programName,
    		String startDateTimeStr,
    		String stopDateTimeStr,
    		String version,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	Element tmpElement = null;
    	
    	requestElement = new Element("totalOverriddenDevicesByAccountNumberRequest", ns);
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
    
    // OVERRIDEN DEVICES BY PROGRAM REQUEST
    public static Element createOverridenDevicesByProgramRequestElement(
    		String programName,
    		String startDateTimeStr,
    		String stopDateTimeStr,
    		String version,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	Element tmpElement = null;
    	
    	requestElement = new Element("totalOverriddenDevicesByProgramNameRequest", ns);
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
    
    // DECREMENT LIMIT REQUEST
    public static Element createDecrementLimitRequestElement(
    		String accountNumber,
    		String serialNumber,
    		String version,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	Element tmpElement = null;
    	
    	requestElement = new Element("decrementDeviceOverrideLimitRequest", ns);
    	versionAttribute = new Attribute("version", version);
    	requestElement.setAttribute(versionAttribute);
    	
    	tmpElement = XmlUtils.createStringElement("accountNumber", ns, accountNumber);
    	requestElement.addContent(tmpElement);
    	
    	tmpElement = XmlUtils.createStringElement("serialNumber", ns, serialNumber);
    	requestElement.addContent(tmpElement);
    	
    	// validate request
    	TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
    	
    	return requestElement;
    }
    
    // RESET OVERRIDE BY SERIAL NUMBER REQUEST
    public static Element createResetOverrideBySerialNumberRequestElement(
    		String accountNumber,
    		String serialNumber,
    		String version,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	Element tmpElement = null;
    	
    	requestElement = new Element("resetOverrideCountBySerialNumberRequest", ns);
    	versionAttribute = new Attribute("version", version);
    	requestElement.setAttribute(versionAttribute);
    	
    	tmpElement = XmlUtils.createStringElement("accountNumber", ns, accountNumber);
    	requestElement.addContent(tmpElement);
    	
    	tmpElement = XmlUtils.createStringElement("serialNumber", ns, serialNumber);
    	requestElement.addContent(tmpElement);
    	
    	// validate request
    	TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
    	
    	return requestElement;
    }
    
    // CANCEL CURRENT OVERRIDES	 REQUEST
    public static Element createCancleCurrentOverridesRequestElement(
    		String version,
    		String programName,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	
    	requestElement = new Element("cancelAllCurrentOverridesRequest", ns);
    	versionAttribute = new Attribute("version", version);
    	requestElement.setAttribute(versionAttribute);
    	
    	if (programName != null) {
    		Element tmpElement = XmlUtils.createStringElement("programName", ns, programName);
        	requestElement.addContent(tmpElement);
    	}
    	
    	// validate request
    	TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
    	
    	return requestElement;
    }
    
    // COUNT OVERRIDES REQUEST
    public static Element createCountOverridesRequestElement(
    		String version,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	
    	requestElement = new Element("countOverridesTowardsLimitRequest", ns);
    	versionAttribute = new Attribute("version", version);
    	requestElement.setAttribute(versionAttribute);
    	
    	// validate request
    	TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
    	
    	return requestElement;
    }
    
    // Prohibit OVERRIDES REQUEST
    public static Element createProhibitOverridesRequestElement(
    		String version,
    		String programName,
    		OptOutEnabled optOutEnabled,
    		Resource requestSchemaResource) {
    	
    	Element requestElement = null;
    	Attribute versionAttribute = null;
    	
    	requestElement = new Element("prohibitConsumerOverridesRequest", ns);
    	versionAttribute = new Attribute("version", version);
    	requestElement.setAttribute(versionAttribute);

        if (programName != null) {
            Element tmpElement = XmlUtils.createStringElement("programName", ns, programName);
            requestElement.addContent(tmpElement);
        }
        
        if (optOutEnabled != null) {
            Element tmpElement = XmlUtils.createStringElement("action", ns, optOutEnabled.toString());
            requestElement.addContent(tmpElement);
        }
    	
    	// validate request
    	TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
    	
    	return requestElement;
    }
    
}
