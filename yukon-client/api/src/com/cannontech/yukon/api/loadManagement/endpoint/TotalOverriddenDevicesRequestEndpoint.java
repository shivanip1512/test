package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class TotalOverriddenDevicesRequestEndpoint {

    private OptOutService optOutService;
    private AuthDao authDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();

    private static String byAccountAccountNumberStr = "/y:totalOverriddenDevicesByAccountNumberRequest/y:accountNumber";
    private static String byAccountStartDateTimeStr = "/y:totalOverriddenDevicesByAccountNumberRequest/y:startDateTime";
    private static String byAccountStopDateTimeStr = "/y:totalOverriddenDevicesByAccountNumberRequest/y:stopDateTime";
    
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
        
        // init response
        Element resp = new Element("totalOverriddenDevicesByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // Check authorization
        try {
        	authDao.verifyTrueProperty(user, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT);
        } catch (NotAuthorizedException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			totalOverriddenDevicesByAccountNumberRequest, 
        			e, 
        			"UserNotAuthorized", 
        			"The user is not get total devices overriden.");
        	resp.addContent(fe);
        	return resp;
        }
        
        // run service
        int totalDevices = 0;
        try {
			totalDevices = optOutService.getOptOutDeviceCountForAccount(accountNumber, startTime, stopTime, user);
        } catch (NotFoundException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			totalOverriddenDevicesByAccountNumberRequest, 
        			e, 
        			"InvalidAccountNumber", 
        			"No account with account number: " + accountNumber);
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
        
        // Check authorization
        try {
        	authDao.verifyTrueProperty(user, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT);
        } catch (NotAuthorizedException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			totalOverriddenDevicesByProgramNameRequest, 
        			e, 
        			"UserNotAuthorized", 
        			"The user is not get total devices overriden.");
        	resp.addContent(fe);
        	return resp;
        }
        
        // run service
        int totalDevices = 0;
        try {
			totalDevices = optOutService.getOptOutDeviceCountForProgram(programName, startTime, stopTime, user);
        } catch (NotFoundException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			totalOverriddenDevicesByProgramNameRequest, 
        			e, 
        			"InvalidProgramName", 
        			"No program with name: " + programName);
        	resp.addContent(fe);
        	return resp;
        }
        
        // build response
        resp.addContent(XmlUtils.createLongElement("totalDevices", ns, totalDevices));
        
        return resp;
    }    
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
		this.authDao = authDao;
	}
    
}

