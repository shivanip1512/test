package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Date;
import java.util.List;

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
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class OverrideHistoryRequestEndpoint {

	private OptOutService optOutService;
	private AuthDao authDao;
	
    private Namespace ns = YukonXml.getYukonNamespace();
    private static String byAccountAccountNumberStr = "/y:overrideHistoryByAccountNumberRequest/y:accountNumber";
    private static String byAccountStartDateTimeStr = "/y:overrideHistoryByAccountNumberRequest/y:startDateTime";
    private static String byAccountStopDateTimeStr = "/y:overrideHistoryByAccountNumberRequest/y:stopDateTime";
    
    private static String byProgramProgramNameStr = "/y:overrideHistoryByProgramNameRequest/y:programName";
    private static String byProgramStartDateTimeStr = "/y:overrideHistoryByProgramNameRequest/y:startDateTime";
    private static String byProgramStopDateTimeStr = "/y:overrideHistoryByProgramNameRequest/y:stopDateTime";

    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="overrideHistoryByAccountNumberRequest")
    public Element invokeOverrideByAccount(Element overrideHistoryByAccountNumberRequest, LiteYukonUser user) throws Exception {
        
    	
        //Verify Request message version
    	XmlVersionUtils.verifyYukonMessageVersion(overrideHistoryByAccountNumberRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(overrideHistoryByAccountNumberRequest);
        
        String accountNumber = template.evaluateAsString(byAccountAccountNumberStr);
        Date startTime = template.evaluateAsDate(byAccountStartDateTimeStr);
        Date stopTime = template.evaluateAsDate(byAccountStopDateTimeStr);
        
        // init response
        Element resp = new Element("overrideHistoryByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // Check authorization
        try {
        	authDao.verifyTrueProperty(user, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT);
        } catch (NotAuthorizedException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			overrideHistoryByAccountNumberRequest, 
        			e, 
        			"UserNotAuthorized", 
        			"The user is not authorized to view override history.");
        	resp.addContent(fe);
        	return resp;
        }
        // run service
        List<OverrideHistory> overrideHistoryList;
        try {
			overrideHistoryList = optOutService.getOptOutHistoryForAccount(accountNumber, startTime, stopTime, user);
        } catch (NotFoundException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			overrideHistoryByAccountNumberRequest, 
        			e, 
        			"InvalidAccountNumber", 
        			"No account with account number: " + accountNumber);
        	resp.addContent(fe);
        	return resp;
        }

        // build response
        resp.addContent(buildHistoryEntriesElement(overrideHistoryList));
        return resp;
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="overrideHistoryByProgramNameRequest")
    public Element invokeOverrideByProgram(Element overrideHistoryByProgramNameRequest, LiteYukonUser user) throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(overrideHistoryByProgramNameRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(overrideHistoryByProgramNameRequest);

        String programName = template.evaluateAsString(byProgramProgramNameStr);        
        Date startTime = template.evaluateAsDate(byProgramStartDateTimeStr);
        Date stopTime = template.evaluateAsDate(byProgramStopDateTimeStr);
        
        // init response
        Element resp = new Element("overrideHistoryByProgramNameResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // Check authorization
        try {
        	authDao.verifyTrueProperty(user, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT);
        } catch (NotAuthorizedException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			overrideHistoryByProgramNameRequest, 
        			e, 
        			"UserNotAuthorized", 
        			"The user is not authorized to view override history.");
        	resp.addContent(fe);
        	return resp;
        }
        
        // run service
        List<OverrideHistory> overrideHistoryList;
        try {
			overrideHistoryList = optOutService.getOptOutHistoryByProgram(programName, startTime, stopTime, user);
        } catch (NotFoundException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			overrideHistoryByProgramNameRequest, 
        			e, 
        			"InvalidProgramName", 
        			"No program with name: " + programName);
        	resp.addContent(fe);
        	return resp;
        }
        
        // build response
        resp.addContent(buildHistoryEntriesElement(overrideHistoryList));
        return resp;
    }    
    
    // builds overrideHistoryEntries element
    private Element buildHistoryEntriesElement(List<OverrideHistory> overrideHistoryList) {
        
        // build overrideHistoryEntries element content
        Element overrideHistoryEntries = new Element("overrideHistoryEntries", ns);
        for (OverrideHistory overrideHistory : overrideHistoryList) {
            Element overrideHistoryElement = new Element("overrideHistory", ns);
            overrideHistoryElement.addContent(XmlUtils.createStringElement("serialNumber", ns, overrideHistory.getSerialNumber()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("programName", ns, overrideHistory.getProgramName()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("accountNumber", ns, overrideHistory.getAccountNumber()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("status", ns, overrideHistory.getStatus().name()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("scheduledDateTime", ns, overrideHistory.getScheduledDate()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("startDateTime", ns, overrideHistory.getStartDate()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("stopDateTime", ns, overrideHistory.getStopDate()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("userName", ns, overrideHistory.getUserName()));
            overrideHistoryElement.addContent(XmlUtils.createLongElement("overrideNumber", ns, overrideHistory.getOverrideNumber()));
            overrideHistoryElement.addContent(XmlUtils.createBooleanElement("countedAgainstLimit", ns, overrideHistory.isCountedAgainstLimit()));
            overrideHistoryEntries.addContent(overrideHistoryElement);
        }
        return overrideHistoryEntries;
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
