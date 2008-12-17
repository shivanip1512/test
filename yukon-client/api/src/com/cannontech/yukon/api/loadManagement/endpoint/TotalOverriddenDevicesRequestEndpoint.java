package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.OverrideService;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class TotalOverriddenDevicesRequestEndpoint {

    private OverrideService overrideService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private static String byAccountAccountNumberStr = "/y:totalOverriddenDevicesByAccountNumberRequest/y:accountNumber";
    private static String byAccountStartDateTimeStr = "/y:totalOverriddenDevicesByAccountNumberRequest/y:startDateTime";
    private static String byAccountStopDateTimeStr = "/y:totalOverriddenDevicesByAccountNumberRequest/y:stopDateTime";
    private static String byAccountProgramNameStr = "/y:totalOverriddenDevicesByAccountNumberRequest/y:programName";
    
    private static String byProgramProgramNameStr = "/y:totalOverriddenDevicesByProgramNameRequest/y:programName";
    private static String byProgramStartDateTimeStr = "/y:totalOverriddenDevicesByProgramNameRequest/y:startDateTime";
    private static String byProgramStopDateTimeStr = "/y:totalOverriddenDevicesByProgramNameRequest/y:stopDateTime";

    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="totalOverriddenDevicesByAccountNumberRequest")
    public Element invokeDevicesByAccount(Element totalOverriddenDevicesByAccountNumberRequest, LiteYukonUser user) throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(totalOverriddenDevicesByAccountNumberRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(totalOverriddenDevicesByAccountNumberRequest);
        
        String accountNumber = template.evaluateAsString(byAccountAccountNumberStr);
        Date startTime = template.evaluateAsDate(byAccountStartDateTimeStr);
        Date stopTime = template.evaluateAsDate(byAccountStopDateTimeStr);
        String programName = template.evaluateAsString(byAccountProgramNameStr);
        
        // init response
        Element resp = new Element("totalOverriddenDevicesByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        //TODO decide what exception(s) this might throw, add error code details
        long totalDevices = 0; 
        try {
            totalDevices = overrideService.getTotalOverridenDevicesByAccountNumber(accountNumber, programName, startTime, stopTime, user);
        } catch (StarsInvalidArgumentException e) {
            
            Element fe = XMLFailureGenerator.generateFailure(totalOverriddenDevicesByAccountNumberRequest, e, "ERROR_CODE", "ERROR_DESCRIPTION");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        resp.addContent(XmlUtils.createLongElement("totalDevices", ns, totalDevices));
        
        return resp;
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="totalOverriddenDevicesByProgramNameRequest")
    public Element invokeDevicesByProgram(Element totalOverriddenDevicesByProgramNameRequest, LiteYukonUser user) throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(totalOverriddenDevicesByProgramNameRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(totalOverriddenDevicesByProgramNameRequest);
        
        String programName = template.evaluateAsString(byProgramProgramNameStr);
        Date startTime = template.evaluateAsDate(byProgramStartDateTimeStr);
        Date stopTime = template.evaluateAsDate(byProgramStopDateTimeStr);
        
        // init response
        Element resp = new Element("totalOverriddenDevicesByProgramNameResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        //TODO decide what exception(s) this might throw, add error code details
        long totalDevices = 0; 
        try {
            totalDevices = overrideService.getTotalOverridenDevicesByProgramName(programName, startTime, stopTime, user);
        } catch (StarsInvalidArgumentException e) {
            
            Element fe = XMLFailureGenerator.generateFailure(totalOverriddenDevicesByProgramNameRequest, e, "ERROR_CODE", "ERROR_DESCRIPTION");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        resp.addContent(XmlUtils.createLongElement("totalDevices", ns, totalDevices));
        
        return resp;
    }    
    
    @Autowired
    public void setOverrideService(OverrideService overrideService) {
        this.overrideService = overrideService;
    }
}

